package org.lunelang.language.compiler

import com.oracle.truffle.api.frame.FrameDescriptor
import com.oracle.truffle.api.frame.FrameSlotKind
import org.lunelang.language.Bytecode
import org.lunelang.language.nodes.InstructionNode
import org.lunelang.language.nodes.instructions.CoerceToBooleanNodeGen

class SemanticTranslator {
    private class FunctionScope {
        val bytecode = ByteArrayBuilder()
        val instructionNodes = mutableListOf<InstructionNode>()
        val frameDescriptorBuilder = FrameDescriptor.newBuilder()
        val freeFrameSlots = mutableListOf<Int>()
        var conditionProfileCount: Int = 0

        fun appendExecute(instruction: InstructionNode) {
            instructionNodes.add(instruction)
            bytecode.appendByte(Bytecode.OP_EXECUTE)
            bytecode.appendInt(instructionNodes.lastIndex)
        }

        fun appendUnlinkedConditionalBranch(conditionSlot: Int): Int {
            bytecode.appendByte(Bytecode.OP_CONDITIONAL_BRANCH)
            val targetOffset = bytecode.size
            bytecode.appendInt(-1)
            bytecode.appendInt(conditionProfileCount++)
            bytecode.appendInt(conditionSlot)
            return targetOffset
        }

        fun appendUnlinkedUnconditionalBranch(): Int {
            bytecode.appendByte(Bytecode.OP_UNCONDITIONAL_BRANCH)
            val targetOffset = bytecode.size
            bytecode.appendInt(-1)
            return targetOffset
        }

        fun allocateFrameSlot(): Int {
            return freeFrameSlots.removeLastOrNull()
                ?: frameDescriptorBuilder.addSlot(FrameSlotKind.Illegal, null, null)
        }

        fun translate(context: LuneParser.NilExpressionContext): Int {
            bytecode.appendByte(Bytecode.OP_LOAD_NIL)
            return allocateFrameSlot().also { bytecode.appendInt(it) }
        }

        fun translate(context: LuneParser.TrueExpressionContext): Int {
            bytecode.appendByte(Bytecode.OP_LOAD_TRUE)
            return allocateFrameSlot().also { bytecode.appendInt(it) }
        }

        fun translate(context: LuneParser.FalseExpressionContext): Int {
            bytecode.appendByte(Bytecode.OP_LOAD_FALSE)
            return allocateFrameSlot().also { bytecode.appendInt(it) }
        }

        fun translate(context: LuneParser.AndExpressionContext): Int {
            val lhsSlot = translate(context.lhs)
            val coercedLhsSlot = allocateFrameSlot()
            appendExecute(CoerceToBooleanNodeGen.create(coercedLhsSlot, lhsSlot))
            freeFrameSlots.add(lhsSlot)
            val trueTargetOffset = appendUnlinkedConditionalBranch(coercedLhsSlot)
            freeFrameSlots.add(coercedLhsSlot)
            val resultSlot = allocateFrameSlot()
            bytecode.appendByte(Bytecode.OP_LOAD_FALSE)
            bytecode.appendInt(resultSlot)
            val convergedTargetOffset = appendUnlinkedUnconditionalBranch()
            bytecode.setInt(trueTargetOffset, bytecode.size)
            val rhsSlot = translate(context.rhs)
            bytecode.appendByte(Bytecode.OP_COPY)
            bytecode.appendInt(resultSlot)
            bytecode.appendInt(rhsSlot)
            freeFrameSlots.add(rhsSlot)
            bytecode.setInt(convergedTargetOffset, bytecode.size)
            return resultSlot
        }


        fun translate(context: LuneParser.ExpressionContext, allowMultipleResults: Boolean = false): Int {
            return when (context) {
                is LuneParser.NilExpressionContext -> translate(context)
                is LuneParser.TrueExpressionContext -> translate(context)
                is LuneParser.FalseExpressionContext -> translate(context)
                is LuneParser.AndExpressionContext -> translate(context)
                else -> TODO()
            }
        }
    }
}
