package org.lunelang.language.compiler;

import org.graalvm.collections.EconomicMap;
import org.graalvm.collections.Pair;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.parseDouble;
import static java.lang.Long.parseLong;

public final class SemanticAnalyzer {
    private LocalScope scope = new LocalScope(null);
    private FunctionScope function = new FunctionScope();

    private Block newBlock() {
        return new Block(function.emittedFunction);
    }

    private void setCurrentBlock(Block block) {
        function.currentBlock = block;
    }

    private <I extends Instruction> I append(I instruction) {
        return function.currentBlock.append(instruction);
    }

    private Instruction analyze(LuneParser.TableConstructorContext context) {
        var table = new NewTableInstruction();
        var keyValuePairs = new ArrayList<Pair<Instruction, Instruction>>();

        for (var i = 0; i < context.fields.size(); i++) {
            var field = context.fields.get(i);
            if (field instanceof LuneParser.IndexedFieldContext c) {
                var key = analyze(c.key);
                var value = analyze(c.value);
                keyValuePairs.add(Pair.create(key, value));
            } else if (field instanceof LuneParser.NamedFieldContext c) {
                var key = new StringConstantInstruction(c.key.getText().getBytes(StandardCharsets.UTF_8));
                var value = analyze(c.value);
                keyValuePairs.add(Pair.create(key, value));
            } else if (field instanceof LuneParser.OrdinalFieldContext c) {
                table.appendValue(analyze(c.value, i == context.fields.size() - 1));
            } else {
                throw new ClassCastException();
            }
        }

        append(table);
        for (var kv : keyValuePairs) {
            append(new IndexedStoreInstruction(table, kv.getLeft(), kv.getRight()));
        }

        return table;
    }

    private void analyze(LuneParser.BlockContext context) {
        for (var statement : context.statements) {
            analyze(statement);
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
                    instruction.appendValue(analyze(context.returnValues.get(i)));
                }

                if (!context.returnValues.isEmpty()) {
                    instruction.appendValue(analyze(context.returnValues.get(context.returnValues.size() - 1), true));
                }

                append(instruction);
            }
        }
    }

    private void analyze(LuneParser.EmptyStatementContext context) {
        // Nothing to do
    }

    private void analyze(LuneParser.AssignmentStatementContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private void analyze(LuneParser.FunctionCallStatementContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private void analyze(LuneParser.LabelStatementContext context) {
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

    private void analyze(LuneParser.BreakStatementContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private void analyze(LuneParser.GotoStatementContext context) {
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

    private void analyze(LuneParser.BlockStatementContext context) {
        analyze(context.body);
    }

    private void analyze(LuneParser.WhileStatementContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private void analyze(LuneParser.RepeatStatementContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private void analyze(LuneParser.IfStatementContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private void analyze(LuneParser.NumericForStatementContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private void analyze(LuneParser.GenericForStatementContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private void analyze(LuneParser.FunctionStatementContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private void analyze(LuneParser.LocalFunctionStatementContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private void analyze(LuneParser.LocalStatementContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private void analyze(LuneParser.StatementContext context) {
        if (context instanceof LuneParser.EmptyStatementContext c) {
            analyze(c);
        } else if (context instanceof LuneParser.AssignmentStatementContext c) {
            analyze(c);
        } else if (context instanceof LuneParser.FunctionCallStatementContext c) {
            analyze(c);
        } else if (context instanceof LuneParser.LabelStatementContext c) {
            analyze(c);
        } else if (context instanceof LuneParser.BreakStatementContext c) {
            analyze(c);
        } else if (context instanceof LuneParser.GotoStatementContext c) {
            analyze(c);
        } else if (context instanceof LuneParser.BlockStatementContext c) {
            analyze(c);
        } else if (context instanceof LuneParser.WhileStatementContext c) {
            analyze(c);
        } else if (context instanceof LuneParser.RepeatStatementContext c) {
            analyze(c);
        } else if (context instanceof LuneParser.IfStatementContext c) {
            analyze(c);
        } else if (context instanceof LuneParser.NumericForStatementContext c) {
            analyze(c);
        } else if (context instanceof LuneParser.GenericForStatementContext c) {
            analyze(c);
        } else if (context instanceof LuneParser.FunctionStatementContext c) {
            analyze(c);
        } else if (context instanceof LuneParser.LocalFunctionStatementContext c) {
            analyze(c);
        } else if (context instanceof LuneParser.LocalStatementContext c) {
            analyze(c);
        } else {
            throw new ClassCastException();
        }
    }

    private Instruction analyze(LuneParser.NameExpressionContext context) {
        var name = context.name.getText();

        for (var nextScope = scope; nextScope != null; nextScope = nextScope.parentScope) {
            var local = nextScope.bindings.get(name);
            if (local != null) {
                return append(new LocalLoadInstruction(local));
            }
        }

        for (var nextScope = scope; nextScope != null; nextScope = scope.parentScope) {
            var envLocal = nextScope.bindings.get("_ENV");
            if (envLocal != null) {
                var env = append(new LocalLoadInstruction(envLocal));
                var key = append(new StringConstantInstruction(name.getBytes(StandardCharsets.UTF_8)));
                return append(new BinaryOpInstruction(BinaryOp.Index, env, key));
            }
        }

        throw new AssertionError();
    }

    private Instruction analyze(LuneParser.ParenthesizedExpressionContext context) {
        return analyze(context.wrapped);
    }

    private Instruction analyze(LuneParser.IndexExpressionContext context) {
        var receiver = analyze(context.receiver);
        var key = analyze(context.key);
        return append(new BinaryOpInstruction(BinaryOp.Index, receiver, key));
    }

    private Instruction analyze(LuneParser.MemberExpressionContext context) {
        var receiver = analyze(context.receiver);
        var key = append(new StringConstantInstruction(context.key.getText().getBytes(StandardCharsets.UTF_8)));
        return append(new BinaryOpInstruction(BinaryOp.Index, receiver, key));
    }

    private Instruction analyze(LuneParser.CallExpressionContext context, boolean allowMultipleResults) {
        throw new UnsupportedOperationException("TODO");
    }

    private Instruction analyze(LuneParser.PrefixExpressionContext context, boolean allowMultipleResults) {
        if (context instanceof LuneParser.NameExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.ParenthesizedExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.IndexExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.MemberExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.CallExpressionContext c) {
            return analyze(c, allowMultipleResults);
        } else {
            throw new ClassCastException();
        }
    }

    private Instruction analyze(LuneParser.PrefixExpressionContext context) {
        return analyze(context, false);
    }

    private Instruction analyze(LuneParser.NilExpressionContext context) {
        return append(new NilConstantInstruction());
    }

    private Instruction analyze(LuneParser.FalseExpressionContext context) {
        return append(new BooleanConstantInstruction(false));
    }

    private Instruction analyze(LuneParser.TrueExpressionContext context) {
        return append(new BooleanConstantInstruction(true));
    }

    private Instruction analyze(LuneParser.DecimalIntegerExpressionContext context) {
        var text = context.token.getText();
        Instruction instruction;

        try {
            instruction = new LongConstantInstruction(parseLong(text));
        } catch (NumberFormatException ignored) {
            instruction = new DoubleConstantInstruction(parseDouble(text));
        }

        return append(instruction);
    }

    private Instruction analyze(LuneParser.HexadecimalIntegerExpressionContext context) {
        var text = context.token.getText().replaceFirst("0[xX]", "");
        long value;

        try {
            value = parseLong(text, 16);
        } catch (NumberFormatException ignored) {
            value = new BigInteger(text, 16).longValue();
        }

        return append(new LongConstantInstruction(value));
    }

    private Instruction analyze(LuneParser.DecimalFloatExpressionContext context) {
        return append(new DoubleConstantInstruction(parseDouble(context.token.getText())));
    }

    private Instruction analyze(LuneParser.HexadecimalFloatExpressionContext context) {
        return append(new DoubleConstantInstruction(parseDouble(context.token.getText())));
    }

    private Instruction analyze(LuneParser.ShortLiteralStringExpressionContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private Instruction analyze(LuneParser.LongLiteralStringExpressionContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private Instruction analyze(LuneParser.VarargsExpressionContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private Instruction analyze(LuneParser.TableExpressionContext context) {
        return analyze(context.table);
    }

    private Instruction analyze(LuneParser.FunctionExpressionContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private Instruction analyze(LuneParser.PrefixExpressionExpressionContext context, boolean allowMultipleResults) {
        return analyze(context.wrapped, allowMultipleResults);
    }

    private Instruction analyze(LuneParser.PowerExpressionContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private Instruction analyze(LuneParser.PrefixOperatorExpressionContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private Instruction analyze(LuneParser.MultiplyDivideModuloExpressionContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private Instruction analyze(LuneParser.AddSubtractExpressionContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private Instruction analyze(LuneParser.ConcatenateExpressionContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private Instruction analyze(LuneParser.ShiftExpressionContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private Instruction analyze(LuneParser.BitwiseAndExpressionContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private Instruction analyze(LuneParser.BitwiseXOrExpressionContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private Instruction analyze(LuneParser.BitwiseOrExpressionContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private Instruction analyze(LuneParser.ComparisonExpressionContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    private Instruction analyze(LuneParser.AndExpressionContext context) {
        var result = new LocalVariable();
        var truePath = newBlock();
        var falsePath = newBlock();
        var convergedPath = newBlock();

        var lhs = analyze(context.lhs);
        append(new ConditionalBranchInstruction(lhs, truePath, falsePath));

        setCurrentBlock(truePath);
        var rhs = analyze(context.rhs);
        append(new LocalStoreInstruction(result, rhs));
        append(new UnconditionalBranchInstruction(convergedPath));

        setCurrentBlock(falsePath);
        append(new LocalStoreInstruction(result, lhs));
        append(new UnconditionalBranchInstruction(convergedPath));

        setCurrentBlock(convergedPath);
        return append(new LocalLoadInstruction(result));
    }

    private Instruction analyze(LuneParser.OrExpressionContext context) {
        var result = new LocalVariable();
        var truePath = newBlock();
        var falsePath = newBlock();
        var convergedPath = newBlock();

        var lhs = analyze(context.lhs);
        append(new ConditionalBranchInstruction(lhs, truePath, falsePath));

        setCurrentBlock(truePath);
        append(new LocalStoreInstruction(result, lhs));
        append(new UnconditionalBranchInstruction(convergedPath));

        setCurrentBlock(falsePath);
        var rhs = analyze(context.rhs);
        append(new LocalStoreInstruction(result, rhs));
        append(new UnconditionalBranchInstruction(convergedPath));

        setCurrentBlock(convergedPath);
        return append(new LocalLoadInstruction(result));
    }

    private Instruction analyze(LuneParser.ExpressionContext context, boolean allowMultipleResults) {
        if (context instanceof LuneParser.NilExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.FalseExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.TrueExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.DecimalIntegerExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.HexadecimalIntegerExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.DecimalFloatExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.HexadecimalFloatExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.ShortLiteralStringExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.LongLiteralStringExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.VarargsExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.TableExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.FunctionExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.PrefixExpressionExpressionContext c) {
            return analyze(c, allowMultipleResults);
        } else if (context instanceof LuneParser.PowerExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.PrefixOperatorExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.MultiplyDivideModuloExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.AddSubtractExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.ConcatenateExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.ShiftExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.BitwiseAndExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.BitwiseXOrExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.BitwiseOrExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.ComparisonExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.AndExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.OrExpressionContext c) {
            return analyze(c);
        } else {
            throw new ClassCastException();
        }
    }

    private Instruction analyze(LuneParser.ExpressionContext context) {
        return analyze(context, false);
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
