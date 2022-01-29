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

class IrFunction {
    private val mutableBlocks = mutableListOf<IrBlock>()

    val blocks: List<IrBlock> get() = mutableBlocks
}

class IrBlock {
    var firstInstruction: AbstractInstruction? = null
        private set

    var lastInstruction: AbstractInstruction? = null
        private set
}

sealed interface Instruction {
    val prior: AbstractInstruction?

    val next: AbstractInstruction?

    val isStrictlyScalar: Boolean
}

sealed class AbstractInstruction : Instruction {
    final override var prior: AbstractInstruction? = null
        private set

    final override var next: AbstractInstruction? = null
        private set
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

class CaptureLoadInstruction(val captureIndex: Int) : AbstractInstruction(), ScalarInstruction

class CaptureStoreInstruction(val captureIndex: Int, val value: Instruction) : AbstractInstruction(), ScalarInstruction

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

class NewClosureInstruction(val function: IrFunction, val captureValues: List<Instruction>) : AbstractInstruction(), ScalarInstruction

class ScalarizeInstruction(val operand: Instruction) : AbstractInstruction(), ScalarInstruction

class CallInstruction(val callee: Instruction, val arguments: List<Instruction>) : AbstractInstruction(), VectorInstruction

class TailCallInstruction(val callee: Instruction, val arguments: List<Instruction>) : AbstractInstruction(), ValuelessInstruction

class IndexedLoadInstruction(val receiver: Instruction, val key: Instruction) : AbstractInstruction(), ScalarInstruction

class IndexedStoreInstruction(val receiver: Instruction, val key: Instruction, val value: Instruction) : AbstractInstruction(), ValuelessInstruction

class TrailingArgumentsInstruction : AbstractInstruction(), VectorInstruction

class BeginCloseScopeInstruction(val local: IrLocal) : AbstractInstruction(), ValuelessInstruction

class EndCloseScopeInstruction(val local: IrLocal) : AbstractInstruction(), ValuelessInstruction
