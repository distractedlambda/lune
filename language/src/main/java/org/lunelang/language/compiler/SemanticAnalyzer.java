package org.lunelang.language.compiler;

import org.graalvm.collections.EconomicMap;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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

    private Instruction analyze(LuneParser.NilExpressionContext context) {
        return append(new NilConstantInstruction());
    }

    private Instruction analyze(LuneParser.FalseExpressionContext context) {
        return append(new BooleanConstantInstruction(false));
    }

    private Instruction analyze(LuneParser.TrueExpressionContext context) {
        return append(new BooleanConstantInstruction(true));
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

    private Instruction analyze(LuneParser.PrefixExpressionContext context) {
        return analyze(context, false);
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

    private Instruction analyze(LuneParser.PrefixExpressionExpressionContext context, boolean allowMultipleResults) {
        return analyze(context.wrapped, allowMultipleResults);
    }

    private Instruction analyze(LuneParser.ExpressionContext context, boolean allowMultipleResults) {
        if (context instanceof LuneParser.FalseExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.TrueExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.AndExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.OrExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.NilExpressionContext c) {
            return analyze(c);
        } else if (context instanceof LuneParser.PrefixExpressionExpressionContext c) {
            return analyze(c, allowMultipleResults);
        } else {
            throw new UnsupportedOperationException("TODO");
        }
    }

    private Instruction analyze(LuneParser.ExpressionContext context) {
        return analyze(context, false);
    }

    // FIXME how tf does label scope work

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
