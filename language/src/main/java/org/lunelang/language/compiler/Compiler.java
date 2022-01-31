package org.lunelang.language.compiler;

import com.oracle.truffle.api.frame.FrameDescriptor;
import org.graalvm.collections.EconomicMap;
import org.graalvm.collections.EconomicSet;
import org.lunelang.language.nodes.InstructionNode;

import java.util.ArrayList;
import java.util.List;

public class Compiler {
    private static class FunctionScope {
        private final FunctionScope parentScope;

        private final ByteVector bytecode = new ByteVector();
        private final FrameDescriptor.Builder frameDescriptorBuilder = FrameDescriptor.newBuilder();
        private final List<InstructionNode> instructionNodes = new ArrayList<>();
        private final List<Integer> recycledFrameSlots = new ArrayList<>();
        private final EconomicSet<String> captures = EconomicSet.create();
        private LocalScope currentScope = null;
        private int branchProfileCount = 0;

        private FunctionScope(FunctionScope parentScope) {
            this.parentScope = parentScope;
        }

        private int translate(LuneParser.NameExpressionContext context) {
            var name = context.name.getText();
        }

        
    }

    private static class LocalScope {
        private final LocalScope parentScope;

        private final EconomicMap<String, Integer> localFrameSlots = EconomicMap.create();
        private final EconomicMap<String, Integer> labelBytecodeOffsets = EconomicMap.create();
        private final EconomicMap<String, List<Integer>> unresolvedGotoBranchOffsets = EconomicMap.create();

        private LocalScope(LocalScope parentScope) {
            this.parentScope = parentScope;
        }
    }
}
