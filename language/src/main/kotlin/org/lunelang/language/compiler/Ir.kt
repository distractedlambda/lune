package org.lunelang.language.compiler

import org.graalvm.collections.EconomicSet
import org.graalvm.collections.Equivalence.IDENTITY_WITH_SYSTEM_HASHCODE

enum class UnaryOp {
    Negate,
    Not,
    Length,
    BitwiseNot,
    CoerceToBoolean,
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

class LocalVariable {
    private var _loads: EconomicSet<LocalLoadInstruction>? = null
    private var _stores: EconomicSet<LocalStoreInstruction>? = null

    fun addLoad(instruction: LocalLoadInstruction): LocalLoadInstruction {
        val loads = _loads ?: EconomicSet.create<LocalLoadInstruction?>(IDENTITY_WITH_SYSTEM_HASHCODE).also {
            _loads = it
        }

        loads.add(instruction)

        return instruction
    }

    fun addStore(instruction: LocalStoreInstruction): LocalStoreInstruction {
        val stores = _stores ?: EconomicSet.create<LocalStoreInstruction?>(IDENTITY_WITH_SYSTEM_HASHCODE).also {
            _stores = it
        }

        stores.add(instruction)

        return instruction
    }

    fun removeLoad(instruction: LocalLoadInstruction) {
        _loads?.remove(instruction)
    }

    fun removeStore(instruction: LocalStoreInstruction) {
        _stores?.remove(instruction)
    }
}

class Function(val parent: Function?) {
    var sourceOffset: Int = -1
    var sourceLength: Int = -1
    var entryBlock: Block? = null
}

class Block(function: Function) {
    var firstInstruction: Instruction? = null
    var lastInstruction: Instruction? = null

    private var _predecessors: EconomicSet<Block>? = null

    fun <I : Instruction> append(instruction: I): I {
        assert(instruction.block == null)
        instruction.block = this
        instruction.prior = lastInstruction
        lastInstruction?.next = instruction
        lastInstruction = instruction
        firstInstruction = firstInstruction ?: instruction
        return instruction
    }

    fun addPredecessor(block: Block) {
        val predecessors = _predecessors ?: EconomicSet.create<Block?>(IDENTITY_WITH_SYSTEM_HASHCODE).also {
            _predecessors = it
        }

        predecessors.add(block)
    }
}

sealed class Instruction {
    var sourceOffset: Int = -1
    var sourceLength: Int = -1

    var block: Block? = null
    var prior: Instruction? = null
    var next: Instruction? = null

    private var _valueUses: EconomicSet<Instruction>? = null

    fun <I : Instruction> addValueUse(instruction: I): I {
        val valueUses = _valueUses ?: EconomicSet.create<Instruction?>(IDENTITY_WITH_SYSTEM_HASHCODE).also {
            _valueUses = it
        }

        valueUses.add(instruction)

        return instruction
    }

    fun removeValueUse(instruction: Instruction) {
        _valueUses?.remove(instruction)
    }
}

sealed class ValuelessInstruction : Instruction()

sealed class ScalarInstruction : Instruction()

sealed class VectorInstruction : Instruction()

class LocalLoadInstruction(local: LocalVariable) : ScalarInstruction() {
    var local: LocalVariable = local
        set(newValue) {
            field.removeLoad(this)
            newValue.addLoad(this)
            field = newValue
        }

    init {
        local.addLoad(this)
    }
}

class LocalStoreInstruction(local: LocalVariable, value: ScalarInstruction) : ValuelessInstruction() {
    var local: LocalVariable = local
        set(newValue) {
            field.removeStore(this)
            newValue.addStore(this)
            field = newValue
        }

    var value: ScalarInstruction = value
        set(newValue) {
            field.removeValueUse(this)
            newValue.addValueUse(this)
            field = newValue
        }

    init {
        local.addStore(this)
        value.addValueUse(this)
    }
}

class NilConstantInstruction : ScalarInstruction()

class BooleanConstantInstruction(var value: Boolean) : ScalarInstruction()

class LongConstantInstruction(var value: Long) : ScalarInstruction()

class DoubleConstantInstruction(var value: Double) : ScalarInstruction()

class ObjectConstantExpression(var value: Any) : ScalarInstruction()

class ReturnInstruction : ValuelessInstruction()

class UnconditionalBranchInstruction : ScalarInstruction() {
    var target: Instruction? = null
        set(newValue) {
            field?.removeBranchUse(this)
            newValue?.addBranchUse(this)
            field = newValue
        }
}

class ConditionalBranchInstruction(condition: ScalarInstruction) : ScalarInstruction() {
    var target: Instruction? = null
        set(newValue) {
            field?.removeBranchUse(this)
            newValue?.addBranchUse(this)
            field = newValue
        }

    var condition: ScalarInstruction = condition
        set(newValue) {
            field.removeValueUse(this)
            newValue.addValueUse(this)
            field = newValue
        }

    init {
        condition.addValueUse(this)
    }
}

class UnaryOpInstruction(var op: UnaryOp, operand: ScalarInstruction) : ScalarInstruction() {
    var operand: ScalarInstruction = operand
        set(newValue) {
            field.removeValueUse(this)
            newValue.addValueUse(this)
            field = newValue
        }

    init {
        operand.addValueUse(this)
    }
}

class BinaryOpInstruction(var op: BinaryOp, lhs: ScalarInstruction, rhs: ScalarInstruction) : ScalarInstruction() {
    var lhs: ScalarInstruction = lhs
        set(newValue) {
            field.removeValueUse(this)
            newValue.addValueUse(this)
            field = newValue
        }

    var rhs: ScalarInstruction = rhs
        set(newValue) {
            field.removeValueUse(this)
            newValue.addValueUse(this)
            field = newValue
        }

    init {
        lhs.addValueUse(this)
        rhs.addValueUse(this)
    }
}

class ClosureInstruction(var function: Function) : ScalarInstruction()

class ScalarizeInstruction(operand: VectorInstruction) : ScalarInstruction() {
    var operand: VectorInstruction = operand
        set(newValue) {
            field.removeValueUse(this)
            newValue.addValueUse(this)
            field = newValue
        }

    init {
        operand.addValueUse(this)
    }
}

class CallInstruction(callee: ScalarInstruction, arguments: List<Instruction>) : VectorInstruction() {
    var callee: ScalarInstruction = callee
        set(newValue) {
            field.removeValueUse(this)
            newValue.addValueUse(this)
            field = newValue
        }

    var arguments: List<Instruction> = arguments
        set(newValue) {
            field.forEach { it.removeValueUse(this) }
            newValue.forEach { it.addValueUse(this) }
            field = newValue
        }

    init {
        callee.addValueUse(this)
        arguments.forEach { it.addValueUse(this) }
    }
}

class TailCallInstruction(callee: ScalarInstruction, arguments: List<Instruction>) : ValuelessInstruction() {
    var callee: ScalarInstruction = callee
        set(newValue) {
            field.removeValueUse(this)
            newValue.addValueUse(this)
            field = newValue
        }

    var arguments: List<Instruction> = arguments
        set(newValue) {
            field.forEach { it.removeValueUse(this) }
            newValue.forEach { it.addValueUse(this) }
            field = newValue
        }

    init {
        callee.addValueUse(this)
        arguments.forEach { it.addValueUse(this) }
    }
}

class IndexedLoadInstruction(receiver: ScalarInstruction, key: ScalarInstruction) : ScalarInstruction() {
    var receiver: ScalarInstruction = receiver
        set(newValue) {
            field.removeValueUse(this)
            newValue.addValueUse(this)
            field = newValue
        }

    var key: ScalarInstruction = key
        set(newValue) {
            field.removeValueUse(this)
            newValue.addValueUse(this)
            field = newValue
        }

    init {
        receiver.addValueUse(this)
        key.addValueUse(this)
    }
}

class IndexedStoreInstruction(receiver: ScalarInstruction, key: ScalarInstruction, value: ScalarInstruction) : ScalarInstruction() {
    var receiver: ScalarInstruction = receiver
        set(newValue) {
            field.removeValueUse(this)
            newValue.addValueUse(this)
            field = newValue
        }

    var key: ScalarInstruction = key
        set(newValue) {
            field.removeValueUse(this)
            newValue.addValueUse(this)
            field = newValue
        }

    var value: ScalarInstruction = value
        set(newValue) {
            field.removeValueUse(this)
            newValue.addValueUse(this)
            field = newValue
        }

    init {
        receiver.addValueUse(this)
        key.addValueUse(this)
    }
}

class TrailingArgumentsInstruction(var start: Int) : VectorInstruction()

class FormalArgumentInstruction(var index: Int) : ScalarInstruction()
