package org.lunelang.language.compiler;

import com.oracle.truffle.api.source.Source;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.graalvm.collections.EconomicMap;
import org.graalvm.collections.Pair;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.lang.Character.isDigit;
import static java.lang.Character.isHighSurrogate;
import static java.lang.Character.isLowSurrogate;
import static java.lang.Character.toCodePoint;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.util.Objects.requireNonNull;

public final class SemanticAnalyzer {
    private final Source source;
    private final String sourceString;
    private LocalScope scope = new LocalScope(null);
    private FunctionScope function = new FunctionScope();

    public SemanticAnalyzer(Source source) {
        this.source = requireNonNull(source);
        this.sourceString = source.toString();
    }

    private static <I extends Instruction> I attachSourceRange(I instruction, Token token) {
        instruction.setSourceOffset(token.getStartIndex());
        instruction.setSourceLength(token.getStopIndex() - token.getStartIndex() + 1);
        return instruction;
    }

    private static <I extends Instruction> I attachSourceRange(I instruction, ParserRuleContext context) {
        instruction.setSourceOffset(context.getStart().getStartIndex());
        instruction.setSourceLength(context.getStop().getStopIndex() - context.getStart().getStartIndex() + 1);
        return instruction;
    }

    private Block newBlock() {
        return new Block(function.emittedFunction);
    }

    private void setCurrentBlock(Block block) {
        function.currentBlock = block;
    }

    private <I extends Instruction> I append(I instruction) {
        return function.currentBlock.append(instruction);
    }

    private byte[] parseShortLiteralString(Token token) {
        var parsed = new ByteVector();

        var nextCharIndex = token.getStartIndex() + 1;
        while (nextCharIndex != token.getStopIndex()) {
            var c = sourceString.charAt(nextCharIndex++);
            if (c == '\\') {
                switch (sourceString.charAt(nextCharIndex++)) {
                    case 'a' -> parsed.appendByte((byte) 0x07);
                    case 'b' -> parsed.appendByte((byte) '\b');
                    case 'f' -> parsed.appendByte((byte) '\f');
                    case 'n' -> parsed.appendByte((byte) '\n');
                    case 'r' -> parsed.appendByte((byte) '\r');
                    case 't' -> parsed.appendByte((byte) '\t');
                    case 'v' -> parsed.appendByte((byte) 0x0b);
                    case '\\' -> parsed.appendByte((byte) '\\');
                    case '"' -> parsed.appendByte((byte) '"');
                    case '\'' -> parsed.appendByte((byte) '\'');

                    case '\r' -> {
                        if (sourceString.charAt(nextCharIndex) == '\n') {
                            nextCharIndex++;
                        }
                        parsed.appendByte((byte) '\n');
                    }

                    case '\n' -> {
                        if (sourceString.charAt(nextCharIndex) == '\r') {
                            nextCharIndex++;
                        }
                        parsed.appendByte((byte) '\n');
                    }

                    case 'z' -> {
                        skipWhitespace: for (;;) {
                            switch (sourceString.charAt(nextCharIndex)) {
                                case ' ', '\f', '\n', '\r', '\t', 0x0b -> nextCharIndex++;
                                default -> {
                                    break skipWhitespace;
                                }
                            }
                        }
                    }

                    case 'x' -> {
                        var b = parseInt(sourceString, nextCharIndex, (nextCharIndex += 2), 16);
                        parsed.appendByte((byte) b);
                    }

                    case 'd' -> {
                        var firstIndex = nextCharIndex;

                        if (isDigit(sourceString.charAt(++nextCharIndex)) && isDigit(sourceString.charAt(++nextCharIndex))) {
                            nextCharIndex++;
                        }

                        var b = parseInt(sourceString, firstIndex, nextCharIndex, 16);
                        if (b > 0xff) {
                            throw new UnsupportedOperationException("TODO handle this");
                        }

                        parsed.appendByte((byte) b);
                    }

                    case 'u' -> {
                        var firstIndex = ++nextCharIndex;
                        while (++nextCharIndex != '}');
                        var cp = parseInt(sourceString, firstIndex, nextCharIndex, 16);
                        parsed.appendUTF8(cp);
                    }
                }
            } else if (isHighSurrogate(c) && isLowSurrogate(sourceString.charAt(nextCharIndex))) {
                parsed.appendUTF8(toCodePoint(c, sourceString.charAt(nextCharIndex++)));
            } else {
                parsed.appendUTF8(c);
            }
        }

        return parsed.toByteArray();
    }

    private byte[] parseLongLiteralString(Token token) {
        var parsed = new ByteVector();
        var bracketDepth = 0;

        var nextCharIndex = token.getStartIndex() + 1;
        while (sourceString.charAt(nextCharIndex++) == '=') {
            bracketDepth++;
        }

        nextCharIndex++;

        if (sourceString.charAt(nextCharIndex) == '\n') {
            nextCharIndex++;
        }

        var endIndex = token.getStopIndex() - bracketDepth - 1;
        while (nextCharIndex != endIndex) {
            var c = sourceString.charAt(nextCharIndex++);
            if (c == '\r') {
                if (sourceString.charAt(nextCharIndex) == '\n') {
                    nextCharIndex++;
                }
                parsed.appendByte((byte) '\n');
            } else if (c == '\n') {
                if (sourceString.charAt(nextCharIndex) == '\r') {
                    nextCharIndex++;
                }
                parsed.appendByte((byte) '\r');
            } else if (isHighSurrogate(c) && isLowSurrogate(sourceString.charAt(nextCharIndex))) {
                parsed.appendUTF8(toCodePoint(c, sourceString.charAt(nextCharIndex++)));
            } else {
                parsed.appendUTF8(c);
            }
        }

        return parsed.toByteArray();
    }

    private Instruction visit(LuneParser.TableConstructorContext context) {
        var table = new NewTableInstruction();
        var keyValuePairs = new ArrayList<Pair<Instruction, Instruction>>();

        for (var i = 0; i < context.fields.size(); i++) {
            var field = context.fields.get(i);
            if (field instanceof LuneParser.IndexedFieldContext c) {
                var key = visit(c.key);
                var value = visit(c.value);
                keyValuePairs.add(Pair.create(key, value));
            } else if (field instanceof LuneParser.NamedFieldContext c) {
                var key = new StringConstantInstruction(c.key.getText().getBytes(StandardCharsets.UTF_8));
                var value = visit(c.value);
                keyValuePairs.add(Pair.create(key, value));
            } else if (field instanceof LuneParser.OrdinalFieldContext c) {
                table.appendValue(visit(c.value, i == context.fields.size() - 1));
            } else {
                throw new ClassCastException();
            }
        }

        append(table);
        for (var kv : keyValuePairs) {
            append(new IndexedStoreInstruction(table, kv.getLeft(), kv.getRight()));
        }

        return attachSourceRange(table, context);
    }

    private void visit(LuneParser.BlockContext context) {
        for (var statement : context.statements) {
            visit(statement);
        }

        if (context.ret != null) {
            if (context.returnValues.size() == 1 &&
                context.returnValues.get(0) instanceof LuneParser.PrefixExpressionExpressionContext c &&
                c.wrapped instanceof LuneParser.CallExpressionContext callContext
            ) {
                throw new UnsupportedOperationException("TODO handle tail call");
            } else {
                var instruction = new ReturnInstruction();

                for (var i = 0; i < context.returnValues.size() - 1; i++) {
                    instruction.appendValue(visit(context.returnValues.get(i)));
                }

                if (!context.returnValues.isEmpty()) {
                    instruction.appendValue(visit(context.returnValues.get(context.returnValues.size() - 1), true));
                }

                append(instruction);
            }
        }
    }

    private void visit(LuneParser.EmptyStatementContext context) {
        // Nothing to do
    }

    private Consumer<Instruction> visit(LuneParser.NamedVariableContext context) {
        var name = context.name.getText();

        for (var nextScope = scope; nextScope != null; nextScope = nextScope.parentScope) {
            var local = nextScope.bindings.get(name);
            if (local != null) {
                return value -> append(new LocalStoreInstruction(local, value));
            }
        }

        for (var nextScope = scope; nextScope != null; nextScope = scope.parentScope) {
            var envLocal = nextScope.bindings.get("_ENV");
            if (envLocal != null) {
                var env = append(new LocalLoadInstruction(envLocal));
                return value -> {
                    var key = append(new StringConstantInstruction(name.getBytes(StandardCharsets.UTF_8)));
                    append(new IndexedStoreInstruction(env, key, value));
                };
            }
        }

        throw new AssertionError();
    }

    private Consumer<Instruction> visit(LuneParser.IndexedVariableContext context) {
        var receiver = visit(context.receiver);
        var key = visit(context.key);
        return value -> append(new IndexedStoreInstruction(receiver, key, value));
    }

    private Consumer<Instruction> visit(LuneParser.MemberVariableContext context) {
        var receiver = visit(context.receiver);
        return value -> {
            var key = append(new StringConstantInstruction(context.key.getText().getBytes(StandardCharsets.UTF_8)));
            append(new IndexedStoreInstruction(receiver, key, value));
        };
    }

    private Consumer<Instruction> visit(LuneParser.VariableContext context) {
        if (context instanceof LuneParser.NamedVariableContext c) {
            return visit(c);
        } else if (context instanceof LuneParser.IndexedVariableContext c) {
            return visit(c);
        } else if (context instanceof LuneParser.MemberVariableContext c) {
            return visit(c);
        } else {
            throw new ClassCastException();
        }
    }

    private void visit(LuneParser.AssignmentStatementContext context) {
        var assigners = context.lhs.stream().map(this::visit).toList();
        var values = new Instruction[context.rhs.size()];

        for (var i = 0; i < values.length; i++) {
            values[i] = visit(context.rhs.get(i), i == values.length - 1);
        }

        for (var i = 0; i < assigners.size(); i++) {
            Instruction assignedValue;

            if (i < values.length - 1) {
                assignedValue = values[i];
            } else {
                assignedValue = append(new ExtractScalarInstruction(values[values.length - 1], i - values.length + 1));
            }

            assigners.get(i).accept(assignedValue);
        }
    }

    private void visit(LuneParser.FunctionCallStatementContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private void visit(LuneParser.LabelStatementContext context) {
        // FIXME: rewrite this

        var name = context.name.getText();
        var labeledBlock = newBlock();

        if (scope.labels.put(name, labeledBlock) != null) {
            throw new UnsupportedOperationException("TODO handle reporting this error");
        }

        var unresolvedLabels = scope.unresolvedLabels.get(name);
        if (unresolvedLabels != null) {
            unresolvedLabels.forEach(it -> it.setTarget(labeledBlock));
        }

        append(new UnconditionalBranchInstruction(labeledBlock));
        setCurrentBlock(labeledBlock);
    }

    private void visit(LuneParser.BreakStatementContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private void visit(LuneParser.GotoStatementContext context) {
        // fixme: rewrite this

        var target = context.target.getText();
        var branch = append(new UnconditionalBranchInstruction(null));

        var targetBlock = scope.labels.get(target);
        if (targetBlock != null) {
            branch.setTarget(targetBlock);
        } else {
            var existingList = scope.unresolvedLabels.get(target);
            if (existingList != null) {
                existingList.add(branch);
            } else {
                var list = new ArrayList<UnconditionalBranchInstruction>();
                list.add(branch);
                scope.unresolvedLabels.put(target, list);
            }
        }

        setCurrentBlock(newBlock());
    }

    private void visit(LuneParser.BlockStatementContext context) {
        visit(context.body);
    }

    private void visit(LuneParser.WhileStatementContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private void visit(LuneParser.RepeatStatementContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private void visit(LuneParser.IfStatementContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private void visit(LuneParser.NumericForStatementContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private void visit(LuneParser.GenericForStatementContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private void visit(LuneParser.FunctionStatementContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private void visit(LuneParser.LocalFunctionStatementContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private void visit(LuneParser.LocalStatementContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private void visit(LuneParser.StatementContext context) {
        if (context instanceof LuneParser.EmptyStatementContext c) {
            visit(c);
        } else if (context instanceof LuneParser.AssignmentStatementContext c) {
            visit(c);
        } else if (context instanceof LuneParser.FunctionCallStatementContext c) {
            visit(c);
        } else if (context instanceof LuneParser.LabelStatementContext c) {
            visit(c);
        } else if (context instanceof LuneParser.BreakStatementContext c) {
            visit(c);
        } else if (context instanceof LuneParser.GotoStatementContext c) {
            visit(c);
        } else if (context instanceof LuneParser.BlockStatementContext c) {
            visit(c);
        } else if (context instanceof LuneParser.WhileStatementContext c) {
            visit(c);
        } else if (context instanceof LuneParser.RepeatStatementContext c) {
            visit(c);
        } else if (context instanceof LuneParser.IfStatementContext c) {
            visit(c);
        } else if (context instanceof LuneParser.NumericForStatementContext c) {
            visit(c);
        } else if (context instanceof LuneParser.GenericForStatementContext c) {
            visit(c);
        } else if (context instanceof LuneParser.FunctionStatementContext c) {
            visit(c);
        } else if (context instanceof LuneParser.LocalFunctionStatementContext c) {
            visit(c);
        } else if (context instanceof LuneParser.LocalStatementContext c) {
            visit(c);
        } else {
            throw new ClassCastException();
        }
    }

    private Instruction visit(LuneParser.NameExpressionContext context) {
        var name = context.name.getText();

        for (var nextScope = scope; nextScope != null; nextScope = nextScope.parentScope) {
            var local = nextScope.bindings.get(name);
            if (local != null) {
                return append(attachSourceRange(new LocalLoadInstruction(local), context));
            }
        }

        for (var nextScope = scope; nextScope != null; nextScope = scope.parentScope) {
            var envLocal = nextScope.bindings.get("_ENV");
            if (envLocal != null) {
                var env = append(attachSourceRange(new LocalLoadInstruction(envLocal), context));
                var key = append(attachSourceRange(new StringConstantInstruction(name.getBytes(StandardCharsets.UTF_8)), context));
                return append(attachSourceRange(new BinaryOpInstruction(BinaryOp.Index, env, key), context));
            }
        }

        throw new AssertionError();
    }

    private Instruction visit(LuneParser.ParenthesizedExpressionContext context) {
        return visit(context.wrapped);
    }

    private Instruction visit(LuneParser.IndexExpressionContext context) {
        var receiver = visit(context.receiver);
        var key = visit(context.key);
        return append(attachSourceRange(new BinaryOpInstruction(BinaryOp.Index, receiver, key), context));
    }

    private Instruction visit(LuneParser.MemberExpressionContext context) {
        var receiver = visit(context.receiver);
        var key = append(attachSourceRange(new StringConstantInstruction(context.key.getText().getBytes(StandardCharsets.UTF_8)), context.key));
        return append(attachSourceRange(new BinaryOpInstruction(BinaryOp.Index, receiver, key), context));
    }

    private Instruction visit(LuneParser.CallExpressionContext context, boolean allowMultipleResults) {
        throw new UnsupportedOperationException("TODO");
    }

    private Instruction visit(LuneParser.PrefixExpressionContext context, boolean allowMultipleResults) {
        if (context instanceof LuneParser.NameExpressionContext c) {
            return visit(c);
        } else if (context instanceof LuneParser.ParenthesizedExpressionContext c) {
            return visit(c);
        } else if (context instanceof LuneParser.IndexExpressionContext c) {
            return visit(c);
        } else if (context instanceof LuneParser.MemberExpressionContext c) {
            return visit(c);
        } else if (context instanceof LuneParser.CallExpressionContext c) {
            return visit(c, allowMultipleResults);
        } else {
            throw new ClassCastException();
        }
    }

    private Instruction visit(LuneParser.PrefixExpressionContext context) {
        return visit(context, false);
    }

    private Instruction visit(LuneParser.NilExpressionContext context) {
        return append(attachSourceRange(new NilConstantInstruction(), context));
    }

    private Instruction visit(LuneParser.FalseExpressionContext context) {
        return append(attachSourceRange(new BooleanConstantInstruction(false), context));
    }

    private Instruction visit(LuneParser.TrueExpressionContext context) {
        return append(attachSourceRange(new BooleanConstantInstruction(true), context));
    }

    private Instruction visit(LuneParser.DecimalIntegerExpressionContext context) {
        var text = context.token.getText();
        Instruction instruction;

        try {
            instruction = new LongConstantInstruction(parseLong(text));
        } catch (NumberFormatException ignored) {
            instruction = new DoubleConstantInstruction(parseDouble(text));
        }

        return append(attachSourceRange(instruction, context));
    }

    private Instruction visit(LuneParser.HexadecimalIntegerExpressionContext context) {
        var text = context.token.getText().replaceFirst("0[xX]", "");
        long value;

        try {
            value = parseLong(text, 16);
        } catch (NumberFormatException ignored) {
            value = new BigInteger(text, 16).longValue();
        }

        return append(attachSourceRange(new LongConstantInstruction(value), context));
    }

    private Instruction visit(LuneParser.DecimalFloatExpressionContext context) {
        return append(attachSourceRange(new DoubleConstantInstruction(parseDouble(context.token.getText())), context));
    }

    private Instruction visit(LuneParser.HexadecimalFloatExpressionContext context) {
        return append(attachSourceRange(new DoubleConstantInstruction(parseDouble(context.token.getText())), context));
    }

    private Instruction visit(LuneParser.ShortLiteralStringExpressionContext context) {
        return append(attachSourceRange(new StringConstantInstruction(parseShortLiteralString(context.token)), context));
    }

    private Instruction visit(LuneParser.LongLiteralStringExpressionContext context) {
        return append(attachSourceRange(new StringConstantInstruction(parseLongLiteralString(context.token)), context));
    }

    private Instruction visit(LuneParser.VarargsExpressionContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private Instruction visit(LuneParser.TableExpressionContext context) {
        return visit(context.table);
    }

    private Instruction visit(LuneParser.FunctionExpressionContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private Instruction visit(LuneParser.PrefixExpressionExpressionContext context, boolean allowMultipleResults) {
        return visit(context.wrapped, allowMultipleResults);
    }

    private Instruction visit(LuneParser.PowerExpressionContext context) {
        var lhs = visit(context.lhs);
        var rhs = visit(context.rhs);
        return append(attachSourceRange(new BinaryOpInstruction(BinaryOp.Power, lhs, rhs), context));
    }

    private Instruction visit(LuneParser.PrefixOperatorExpressionContext context) {
        return append(
            attachSourceRange(
                new UnaryOpInstruction(
                    switch (context.operator.getType()) {
                        case LuneParser.Not -> UnaryOp.Not;
                        case LuneParser.Tilde -> UnaryOp.BitwiseNot;
                        case LuneParser.Minus -> UnaryOp.Negate;
                        case LuneParser.Pound -> UnaryOp.Length;
                        default -> throw new AssertionError();
                    },
                    visit(context.operand)
                ),
                context
            )
        );
    }

    private Instruction visit(LuneParser.MultiplyDivideModuloExpressionContext context) {
        var op = switch (context.operator.getType()) {
            case LuneParser.Star -> BinaryOp.Multiply;
            case LuneParser.Slash -> BinaryOp.Divide;
            case LuneParser.Slash2 -> BinaryOp.FloorDivide;
            case LuneParser.Percent -> BinaryOp.Modulo;
            default -> throw new AssertionError();
        };

        var lhs = visit(context.lhs);
        var rhs = visit(context.rhs);
        return append(attachSourceRange(new BinaryOpInstruction(op, lhs, rhs), context));
    }

    private Instruction visit(LuneParser.AddSubtractExpressionContext context) {
        var op = switch (context.operator.getType()) {
            case LuneParser.Plus -> BinaryOp.Add;
            case LuneParser.Minus -> BinaryOp.Subtract;
            default -> throw new AssertionError();
        };

        var lhs = visit(context.lhs);
        var rhs = visit(context.rhs);
        return append(attachSourceRange(new BinaryOpInstruction(op, lhs, rhs), context));
    }

    private Instruction visit(LuneParser.ConcatenateExpressionContext context) {
        var lhs = visit(context.lhs);
        var rhs = visit(context.rhs);
        return append(attachSourceRange(new BinaryOpInstruction(BinaryOp.Concatenate, lhs, rhs), context));
    }

    private Instruction visit(LuneParser.ShiftExpressionContext context) {
        var op = switch (context.operator.getType()) {
            case LuneParser.LAngle2 -> BinaryOp.LeftShift;
            case LuneParser.RAngle2 -> BinaryOp.RightShift;
            default -> throw new AssertionError();
        };

        var lhs = visit(context.lhs);
        var rhs = visit(context.rhs);
        return append(attachSourceRange(new BinaryOpInstruction(op, lhs, rhs), context));
    }

    private Instruction visit(LuneParser.BitwiseAndExpressionContext context) {
        var lhs = visit(context.lhs);
        var rhs = visit(context.rhs);
        return append(attachSourceRange(new BinaryOpInstruction(BinaryOp.BitwiseAnd, lhs, rhs), context));
    }

    private Instruction visit(LuneParser.BitwiseXOrExpressionContext context) {
        var lhs = visit(context.lhs);
        var rhs = visit(context.rhs);
        return append(attachSourceRange(new BinaryOpInstruction(BinaryOp.BitwiseXOr, lhs, rhs), context));
    }

    private Instruction visit(LuneParser.BitwiseOrExpressionContext context) {
        var lhs = visit(context.lhs);
        var rhs = visit(context.rhs);
        return append(attachSourceRange(new BinaryOpInstruction(BinaryOp.BitwiseOr, lhs, rhs), context));
    }

    private Instruction visit(LuneParser.ComparisonExpressionContext context) {
        var op = switch (context.operator.getType()) {
            case LuneParser.LAngle -> BinaryOp.Less;
            case LuneParser.RAngle -> BinaryOp.Greater;
            case LuneParser.LAngleEquals -> BinaryOp.LessOrEqual;
            case LuneParser.RAngleEquals -> BinaryOp.GreaterOrEqual;
            case LuneParser.Equals2 -> BinaryOp.Equal;
            case LuneParser.TildeEquals -> BinaryOp.NotEqual;
            default -> throw new AssertionError();
        };

        var lhs = visit(context.lhs);
        var rhs = visit(context.rhs);
        return append(attachSourceRange(new BinaryOpInstruction(op, lhs, rhs), context));
    }

    private Instruction visit(LuneParser.AndExpressionContext context) {
        var result = new LocalVariable();
        var truePath = newBlock();
        var falsePath = newBlock();
        var convergedPath = newBlock();

        var lhs = visit(context.lhs);
        append(new ConditionalBranchInstruction(lhs, truePath, falsePath));

        setCurrentBlock(truePath);
        var rhs = visit(context.rhs);
        append(new LocalStoreInstruction(result, rhs));
        append(new UnconditionalBranchInstruction(convergedPath));

        setCurrentBlock(falsePath);
        append(new LocalStoreInstruction(result, lhs));
        append(new UnconditionalBranchInstruction(convergedPath));

        setCurrentBlock(convergedPath);
        return append(new LocalLoadInstruction(result));
    }

    private Instruction visit(LuneParser.OrExpressionContext context) {
        var result = new LocalVariable();
        var truePath = newBlock();
        var falsePath = newBlock();
        var convergedPath = newBlock();

        var lhs = visit(context.lhs);
        append(new ConditionalBranchInstruction(lhs, truePath, falsePath));

        setCurrentBlock(truePath);
        append(new LocalStoreInstruction(result, lhs));
        append(new UnconditionalBranchInstruction(convergedPath));

        setCurrentBlock(falsePath);
        var rhs = visit(context.rhs);
        append(new LocalStoreInstruction(result, rhs));
        append(new UnconditionalBranchInstruction(convergedPath));

        setCurrentBlock(convergedPath);
        return append(new LocalLoadInstruction(result));
    }

    private Instruction visit(LuneParser.ExpressionContext context, boolean allowMultipleResults) {
        if (context instanceof LuneParser.NilExpressionContext c) {
            return visit(c);
        } else if (context instanceof LuneParser.FalseExpressionContext c) {
            return visit(c);
        } else if (context instanceof LuneParser.TrueExpressionContext c) {
            return visit(c);
        } else if (context instanceof LuneParser.DecimalIntegerExpressionContext c) {
            return visit(c);
        } else if (context instanceof LuneParser.HexadecimalIntegerExpressionContext c) {
            return visit(c);
        } else if (context instanceof LuneParser.DecimalFloatExpressionContext c) {
            return visit(c);
        } else if (context instanceof LuneParser.HexadecimalFloatExpressionContext c) {
            return visit(c);
        } else if (context instanceof LuneParser.ShortLiteralStringExpressionContext c) {
            return visit(c);
        } else if (context instanceof LuneParser.LongLiteralStringExpressionContext c) {
            return visit(c);
        } else if (context instanceof LuneParser.VarargsExpressionContext c) {
            return visit(c);
        } else if (context instanceof LuneParser.TableExpressionContext c) {
            return visit(c);
        } else if (context instanceof LuneParser.FunctionExpressionContext c) {
            return visit(c);
        } else if (context instanceof LuneParser.PrefixExpressionExpressionContext c) {
            return visit(c, allowMultipleResults);
        } else if (context instanceof LuneParser.PowerExpressionContext c) {
            return visit(c);
        } else if (context instanceof LuneParser.PrefixOperatorExpressionContext c) {
            return visit(c);
        } else if (context instanceof LuneParser.MultiplyDivideModuloExpressionContext c) {
            return visit(c);
        } else if (context instanceof LuneParser.AddSubtractExpressionContext c) {
            return visit(c);
        } else if (context instanceof LuneParser.ConcatenateExpressionContext c) {
            return visit(c);
        } else if (context instanceof LuneParser.ShiftExpressionContext c) {
            return visit(c);
        } else if (context instanceof LuneParser.BitwiseAndExpressionContext c) {
            return visit(c);
        } else if (context instanceof LuneParser.BitwiseXOrExpressionContext c) {
            return visit(c);
        } else if (context instanceof LuneParser.BitwiseOrExpressionContext c) {
            return visit(c);
        } else if (context instanceof LuneParser.ComparisonExpressionContext c) {
            return visit(c);
        } else if (context instanceof LuneParser.AndExpressionContext c) {
            return visit(c);
        } else if (context instanceof LuneParser.OrExpressionContext c) {
            return visit(c);
        } else {
            throw new ClassCastException();
        }
    }

    private Instruction visit(LuneParser.ExpressionContext context) {
        return visit(context, false);
    }

    private static final class LocalScope {
        private final LocalScope parentScope;
        private EconomicMap<String, LocalVariable> bindings;
        private EconomicMap<String, Block> labels;
        private EconomicMap<String, List<UnconditionalBranchInstruction>> unresolvedLabels;

        private LocalScope(LocalScope parentScope) {
            this.parentScope = parentScope;
        }
    }

    private static final class FunctionScope {
        private final Function emittedFunction = new Function();
        private Block currentBlock = emittedFunction.getEntryBlock();
    }
}