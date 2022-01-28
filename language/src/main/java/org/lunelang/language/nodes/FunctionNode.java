package org.lunelang.language.nodes;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.profiles.ConditionProfile;
import org.lunelang.language.LuneLanguage;

import java.util.Arrays;
import java.util.Collection;

import static com.oracle.truffle.api.CompilerAsserts.partialEvaluationConstant;
import static com.oracle.truffle.api.CompilerDirectives.shouldNotReachHere;
import static com.oracle.truffle.api.nodes.ExplodeLoop.LoopExplosionKind.MERGE_EXPLODE;

public final class FunctionNode extends RootNode {
    @CompilationFinal(dimensions = 1) private final byte[] bytecode;
    @Children private final InstructionNode[] instructionNodes;
    @CompilationFinal(dimensions = 1) private final ConditionProfile[] branchConditionProfiles;

    public FunctionNode(
        LuneLanguage language,
        FrameDescriptor frameDescriptor,
        byte[] bytecode,
        Collection<InstructionNode> instructionNodes,
        int branchConditionProfileCount
    ) {
        super(language, frameDescriptor);
        this.bytecode = Arrays.copyOf(bytecode, bytecode.length);
        this.instructionNodes = instructionNodes.toArray(InstructionNode[]::new);
        this.branchConditionProfiles = new ConditionProfile[branchConditionProfileCount];

        for (var i = 0; i < branchConditionProfileCount; i++) {
            branchConditionProfiles[i] = ConditionProfile.createCountingProfile();
        }
    }

    @ExplodeLoop(kind = MERGE_EXPLODE)
    private void executeFromIndex(VirtualFrame frame, int bytecodeIndex) {
        while (true) {
            partialEvaluationConstant(bytecodeIndex);
            switch (bytecode[bytecodeIndex]) {
                case Bytecode.OP_INSTRUCTION -> {
                    var instructionNodeIndex = Bytecode.getEmbeddedInt(bytecode, bytecodeIndex + 1);
                    partialEvaluationConstant(instructionNodeIndex);
                    instructionNodes[instructionNodeIndex].execute(frame);
                    bytecodeIndex += 5;
                }

                case Bytecode.OP_UNCONDITIONAL_BRANCH -> {
                    var targetBytecodeIndex = Bytecode.getEmbeddedInt(bytecode, bytecodeIndex + 1);
                    partialEvaluationConstant(targetBytecodeIndex);
                    bytecodeIndex = targetBytecodeIndex;
                }

                case Bytecode.OP_CONDITIONAL_BRANCH -> {
                    var targetBytecodeIndex = Bytecode.getEmbeddedInt(bytecode, bytecodeIndex + 1);
                    partialEvaluationConstant(targetBytecodeIndex);
                    var conditionProfileIndex = Bytecode.getEmbeddedInt(bytecode, bytecodeIndex + 5);
                    partialEvaluationConstant(conditionProfileIndex);
                    var conditionSlot = Bytecode.getEmbeddedInt(bytecode, bytecodeIndex + 9);
                    partialEvaluationConstant(conditionSlot);
                    if (branchConditionProfiles[conditionProfileIndex].profile(frame.getBoolean(conditionSlot))) {
                        bytecodeIndex = targetBytecodeIndex;
                    }
                }

                case Bytecode.OP_RETURN -> {
                    return;
                }

                default -> throw shouldNotReachHere("invalid bytecode instruction");
            }
        }
    }

    @Override
    public Object execute(VirtualFrame frame) {
        executeFromIndex(frame, 0);
        return frame.getObject(0);
    }
}
