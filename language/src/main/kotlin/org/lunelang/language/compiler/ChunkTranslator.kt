package org.lunelang.language.compiler

import org.antlr.v4.runtime.ParserRuleContext
import java.util.concurrent.locks.Condition

class FunctionTranslator(parent: FunctionTranslator?) {
    val function: IrFunction = IrFunction(parent?.function)
    var currentBlock = function.append(IrBlock())

    fun translate(context: LuneParser.NilExpressionContext): Instruction {
        return currentBlock.append(NilConstantInstruction()).attachSourceLocation(context)
    }

    fun translate(context: LuneParser.TrueExpressionContext): Instruction {
        return currentBlock.append(BooleanConstantInstruction(true)).attachSourceLocation(context)
    }

    fun translate(context: LuneParser.FalseExpressionContext): Instruction {
        return currentBlock.append(BooleanConstantInstruction(false)).attachSourceLocation(context)
    }

    fun translate(context: LuneParser.ShortLiteralStringExpressionContext): Instruction {
        TODO("Handle short string literal expressions")
    }

    fun translate(context: LuneParser.LongLiteralStringExpressionContext): Instruction {
        TODO("Handle long string literal expressions")
    }

    fun translate(context: LuneParser.DecimalIntegerExpressionContext): Instruction {
        val tokenText = context.token.text

        val instruction = try {
            LongConstantInstruction(tokenText.toLong())
        } catch (_: NumberFormatException) {
            DoubleConstantInstruction(tokenText.toDouble())
        }

        return currentBlock.append(instruction).attachSourceLocation(context)
    }

    fun translate(context: LuneParser.HexadecimalIntegerExpressionContext): Instruction {
        val instruction = LongConstantInstruction(context.token.text.substring(2).toBigInteger().toLong())
        return currentBlock.append(instruction).attachSourceLocation(context)
    }

    fun translate(context: LuneParser.DecimalFloatExpressionContext): Instruction {
        val instruction = DoubleConstantInstruction(context.token.text.toDouble())
        return currentBlock.append(instruction).attachSourceLocation(context)
    }

    fun translate(context: LuneParser.HexadecimalFloatExpressionContext): Instruction {
        val instruction = DoubleConstantInstruction(context.token.text.toDouble())
        return currentBlock.append(instruction).attachSourceLocation(context)
    }

    fun translate(context: LuneParser.VarargsExpressionContext): Instruction {
        return currentBlock.append(TrailingArgumentsInstruction()).attachSourceLocation(context)
    }

    fun translate(context: LuneParser.PrefixOperatorExpressionContext): Instruction {
        val op = when (context.operator.type) {
            LuneParser.Minus -> UnaryOp.Negate
            LuneParser.Not -> UnaryOp.Not
            LuneParser.Pound -> UnaryOp.Length
            LuneParser.Tilde -> UnaryOp.BitwiseNot
            else -> error("Unexpected unary operator")
        }

        val operand = scalarize(translate(context.operand))

        return currentBlock.append(UnaryOpInstruction(op, operand)).attachSourceLocation(context)
    }

    fun translate(context: LuneParser.AddSubtractExpressionContext): Instruction {
        val op = when (context.operator.type) {
            LuneParser.Plus -> BinaryOp.Add
            LuneParser.Minus -> BinaryOp.Subtract
            else -> error("Unexpected operator")
        }

        val lhs = scalarize(translate(context.lhs))
        val rhs = scalarize(translate(context.rhs))

        return currentBlock.append(BinaryOpInstruction(op, lhs, rhs)).attachSourceLocation(context)
    }

    fun translate(context: LuneParser.PowerExpressionContext): Instruction {
        val lhs = scalarize(translate(context.lhs))
        val rhs = scalarize(translate(context.rhs))
        return currentBlock.append(BinaryOpInstruction(BinaryOp.Power, lhs, rhs)).attachSourceLocation(context)
    }

    fun translate(context: LuneParser.MultiplyDivideModuloExpressionContext): Instruction {
        val op = when (context.operator.type) {
            LuneParser.Star -> BinaryOp.Multiply
            LuneParser.Slash -> BinaryOp.Divide
            LuneParser.Slash2 -> BinaryOp.FloorDivide
            LuneParser.Percent -> BinaryOp.Remainder
            else -> error("Unexpected operator")
        }

        val lhs = scalarize(translate(context.lhs))
        val rhs = scalarize(translate(context.rhs))

        return currentBlock.append(BinaryOpInstruction(op, lhs, rhs)).attachSourceLocation(context)
    }

    fun translate(context: LuneParser.ConcatenateExpressionContext): Instruction {
        val lhs = scalarize(translate(context.lhs))
        val rhs = scalarize(translate(context.rhs))
        return currentBlock.append(BinaryOpInstruction(BinaryOp.Concatenate, lhs, rhs)).attachSourceLocation(context)
    }

    fun translate(context: LuneParser.ShiftExpressionContext): Instruction {
        val op = when (context.operator.type) {
            LuneParser.LAngle2 -> BinaryOp.ShiftLeft
            LuneParser.RAngle2 -> BinaryOp.ShiftRight
            else -> error("Unexpected operator")
        }

        val lhs = scalarize(translate(context.lhs))
        val rhs = scalarize(translate(context.rhs))

        return currentBlock.append(BinaryOpInstruction(op, lhs, rhs)).attachSourceLocation(context)
    }

    fun translate(context: LuneParser.BitwiseAndExpressionContext): Instruction {
        val lhs = scalarize(translate(context.lhs))
        val rhs = scalarize(translate(context.rhs))
        return currentBlock.append(BinaryOpInstruction(BinaryOp.BitwiseAnd, lhs, rhs)).attachSourceLocation(context)
    }

    fun translate(context: LuneParser.BitwiseXOrExpressionContext): Instruction {
        val lhs = scalarize(translate(context.lhs))
        val rhs = scalarize(translate(context.rhs))
        return currentBlock.append(BinaryOpInstruction(BinaryOp.BitwiseXOr, lhs, rhs)).attachSourceLocation(context)
    }

    fun translate(context: LuneParser.BitwiseOrExpressionContext): Instruction {
        val lhs = scalarize(translate(context.lhs))
        val rhs = scalarize(translate(context.rhs))
        return currentBlock.append(BinaryOpInstruction(BinaryOp.BitwiseOr, lhs, rhs)).attachSourceLocation(context)
    }

    fun translate(context: LuneParser.ComparisonExpressionContext): Instruction {
        val op = when (context.operator.type) {
            LuneParser.LAngle -> BinaryOp.LessThan
            LuneParser.RAngle -> BinaryOp.GreaterThan
            LuneParser.LAngleEquals -> BinaryOp.LessOrEqual
            LuneParser.RAngleEquals -> BinaryOp.GreaterOrEqual
            LuneParser.Equals2 -> BinaryOp.Equal
            LuneParser.TildeEquals -> BinaryOp.NotEqual
            else -> error("Unexpected operator")
        }

        val lhs = scalarize(translate(context.lhs))
        val rhs = scalarize(translate(context.rhs))

        return currentBlock.append(BinaryOpInstruction(op, lhs, rhs)).attachSourceLocation(context)
    }

    fun translate(context: LuneParser.AndExpressionContext): Instruction {
        val truePath = IrBlock()
        val convergedPath = IrBlock()
        val result = IrLocal()

        val lhs = scalarize(translate(context.lhs))
        val coercedLhs = currentBlock.append(UnaryOpInstruction(UnaryOp.CoerceToBoolean, lhs))
        currentBlock.append(ConditionalBranchInstruction(truePath, coercedLhs))
        currentBlock.append(LocalStoreInstruction(result, coercedLhs))
        currentBlock.append(UnconditionalBranchInstruction(convergedPath))

        function.append(truePath)
        currentBlock = truePath
        val rhs = scalarize(translate(context.rhs))
        truePath.append(LocalStoreInstruction(result, rhs))

        function.append(convergedPath)
        currentBlock = convergedPath
        return convergedPath.append(LocalLoadInstruction(result))
    }

    fun translate(context: LuneParser.OrExpressionContext): Instruction {
        val falsePath = IrBlock()
        val convergedPath = IrBlock()
        val result = IrLocal()

        val lhs = scalarize(translate(context.lhs))
        val notLhs = currentBlock.append(UnaryOpInstruction(UnaryOp.Not, lhs))
        currentBlock.append(ConditionalBranchInstruction(falsePath, notLhs))
        currentBlock.append(LocalStoreInstruction(result, lhs))
        currentBlock.append(UnconditionalBranchInstruction(convergedPath))

        function.append(falsePath)
        currentBlock = falsePath
        val rhs = scalarize(translate(context.rhs))
        falsePath.append(LocalStoreInstruction(result, rhs))

        function.append(convergedPath)
        currentBlock = convergedPath
        return convergedPath.append(LocalLoadInstruction(result))
    }

    fun translate(context: LuneParser.ExpressionContext): Instruction {
        return when (context) {
            is LuneParser.NilExpressionContext -> translate(context)
            is LuneParser.TrueExpressionContext -> translate(context)
            is LuneParser.FalseExpressionContext -> translate(context)
            is LuneParser.ShortLiteralStringExpressionContext -> translate(context)
            is LuneParser.LongLiteralStringExpressionContext -> translate(context)
            is LuneParser.DecimalIntegerExpressionContext -> translate(context)
            is LuneParser.HexadecimalIntegerExpressionContext -> translate(context)
            is LuneParser.DecimalFloatExpressionContext -> translate(context)
            is LuneParser.HexadecimalFloatExpressionContext -> translate(context)
            is LuneParser.VarargsExpressionContext -> translate(context)
            is LuneParser.PrefixOperatorExpressionContext -> translate(context)
            is LuneParser.AddSubtractExpressionContext -> translate(context)
            is LuneParser.PowerExpressionContext -> translate(context)
            is LuneParser.MultiplyDivideModuloExpressionContext -> translate(context)
            is LuneParser.ConcatenateExpressionContext -> translate(context)
            is LuneParser.ShiftExpressionContext -> translate(context)
            is LuneParser.BitwiseAndExpressionContext -> translate(context)
            is LuneParser.BitwiseXOrExpressionContext -> translate(context)
            is LuneParser.BitwiseOrExpressionContext -> translate(context)
            is LuneParser.ComparisonExpressionContext -> translate(context)
            is LuneParser.AndExpressionContext -> translate(context)
            is LuneParser.OrExpressionContext -> translate(context)
            else -> TODO("Handle other expression types")
        }
    }

    fun scalarize(instruction: Instruction): Instruction {
        return if (instruction.isStrictlyScalar) {
            instruction
        } else {
            currentBlock.append(ScalarizeInstruction(instruction))
        }
    }
}

private fun <I : Instruction> I.attachSourceLocation(context: ParserRuleContext): I {
    val start = context.getStart().startIndex
    val stop = context.getStop().stopIndex

    if (start != -1 && stop != -1) {
        sourceOffset = start
        sourceLength = stop - start + 1
    }

    return this
}
