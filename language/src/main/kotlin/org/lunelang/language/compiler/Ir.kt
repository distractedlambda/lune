package org.lunelang.language.compiler

enum class UnaryOp {
    Negate,
    Not,
    Length,
    BitwiseNot,
}

enum class BinaryOp {
    Add,
    Subtract,
    Multiply,
    Divide,
    FloorDivide,
    Power,
    Remainder,
    BitwiseAnd,
    BitwiseXOr,
    BitwiseOr,
    ShiftRight,
    ShiftLeft,
    Concatenate,
    LessThan,
    LessOrEqual,
    GreaterThan,
    GreaterOrEqual,
    Equal,
    NotEqual,
}

class IrLocal

class IrFunction(val parent: IrFunction?) {
    var sourceOffset: Int = -1
    var sourceLength: Int = -1

    var firstBlock: IrBlock? = null
    var lastBlock: IrBlock? = null

    fun append(block: IrBlock) {
        block.assertUnlinked()
        block.function = this
        block.prior = lastBlock

        if (lastBlock == null) {
            assert(firstBlock == null)
            firstBlock = block
        } else {
            assert(firstBlock != null)
            lastBlock!!.next = block
        }

        lastBlock = block
    }

    fun prepend(block: IrBlock) {
        block.assertUnlinked()
        block.function = this
        block.next = firstBlock

        if (firstBlock == null) {
            assert(lastBlock == null)
            lastBlock = block
        } else {
            assert(lastBlock != null)
            firstBlock!!.prior = block
        }

        firstBlock = block
    }
}

class IrBlock {
    var sourceOffset: Int = -1
    var sourceLength: Int = -1

    var function: IrFunction? = null
    var prior: IrBlock? = null
    var next: IrBlock? = null

    var firstInstruction: Instruction? = null
    var lastInstruction: Instruction? = null

    fun assertUnlinked() {
        assert(function == null)
        assert(prior == null)
        assert(next == null)
    }

    fun append(instruction: Instruction) {
        instruction.assertUnlinked()
        instruction.block = this
        instruction.prior = lastInstruction

        if (lastInstruction == null) {
            assert(firstInstruction == null)
            firstInstruction = instruction
        } else {
            assert(firstInstruction != null)
            lastInstruction!!.next = instruction
        }

        lastInstruction = instruction
    }

    fun prepend(instruction: Instruction) {
        instruction.assertUnlinked()
        instruction.block = this
        instruction.next = firstInstruction

        if (firstInstruction == null) {
            assert(lastInstruction == null)
            lastInstruction = instruction
        } else {
            assert(lastInstruction != null)
            firstInstruction!!.prior = instruction
        }

        firstInstruction = instruction
    }
}

sealed interface Instruction {
    var sourceOffset: Int
    var sourceLength: Int

    var block: IrBlock?
    var prior: Instruction?
    var next: Instruction?

    val isStrictlyScalar: Boolean

    fun assertUnlinked() {
        assert(block == null)
        assert(prior == null)
        assert(next == null)
    }

    fun unlink() {
        prior?.next = next
        next?.prior = prior

        block?.let {
            if (this === it.firstInstruction) {
                it.firstInstruction = next
            }

            if (this === it.lastInstruction) {
                it.lastInstruction = prior
            }
        }

        block = null
        prior = null
        next = null
    }

    fun makeBefore(other: Instruction) {
        assertUnlinked()
        assert(other.block != null)

        block = other.block
        next = other
        prior = other.prior
        other.prior?.next = this
        other.prior = this

        if (prior == null) {
            other.block!!.firstInstruction = this
        }
    }

    fun makeAfter(other: Instruction) {
        assertUnlinked()
        assert(other.block != null)

        block = other.block
        next = other.next
        prior = other
        other.next?.prior = this
        other.next = this

        if (next == null) {
            other.block!!.lastInstruction = this
        }
    }
}

sealed class AbstractInstruction : Instruction {
    final override var sourceOffset: Int = -1
    final override var sourceLength: Int = -1
    final override var block: IrBlock? = null
    final override var prior: Instruction? = null
    final override var next: Instruction? = null
}

sealed interface ScalarInstruction : Instruction {
    override val isStrictlyScalar get() = true
}

sealed interface VectorInstruction : Instruction {
    override val isStrictlyScalar get() = false
}

sealed interface ValuelessInstruction : Instruction {
    override val isStrictlyScalar get() = false
}

class LocalLoadInstruction(val local: IrLocal) : AbstractInstruction(), ScalarInstruction

class LocalStoreInstruction(val local: IrLocal, val value: Instruction) : AbstractInstruction(), ScalarInstruction

class NilConstantInstruction : AbstractInstruction(), ScalarInstruction

class BooleanConstantInstruction(val value: Boolean) : AbstractInstruction(), ScalarInstruction

class LongConstantInstruction(val value: Long) : AbstractInstruction(), ScalarInstruction

class DoubleConstantInstruction(val value: Double) : AbstractInstruction(), ScalarInstruction

class ObjectConstantExpression(val value: Any) : AbstractInstruction(), ScalarInstruction

class ReturnInstruction(val value: Instruction) : AbstractInstruction(), ScalarInstruction

class UnconditionalBranchInstruction(val target: IrBlock) : AbstractInstruction(), ValuelessInstruction

class ConditionalBranchInstruction(val target: IrBlock, val condition: Instruction) : AbstractInstruction(), ValuelessInstruction

class UnaryOpInstruction(val op: UnaryOp, val operand: Instruction) : AbstractInstruction(), ScalarInstruction

class BinaryOpInstruction(val op: BinaryOp, val lhs: Instruction, val rhs: Instruction) : AbstractInstruction(), ScalarInstruction

class ClosureInstruction(val function: IrFunction) : AbstractInstruction(), ScalarInstruction

class ScalarizeInstruction(val operand: Instruction) : AbstractInstruction(), ScalarInstruction

class CallInstruction(val callee: Instruction, val arguments: List<Instruction>) : AbstractInstruction(), VectorInstruction

class TailCallInstruction(val callee: Instruction, val arguments: List<Instruction>) : AbstractInstruction(), ValuelessInstruction

class IndexedLoadInstruction(val receiver: Instruction, val key: Instruction) : AbstractInstruction(), ScalarInstruction

class IndexedStoreInstruction(val receiver: Instruction, val key: Instruction, val value: Instruction) : AbstractInstruction(), ValuelessInstruction

class TrailingArgumentsInstruction : AbstractInstruction(), VectorInstruction

class BeginCloseScopeInstruction(val local: IrLocal) : AbstractInstruction(), ValuelessInstruction

class EndCloseScopeInstruction(val local: IrLocal) : AbstractInstruction(), ValuelessInstruction
