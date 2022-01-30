package org.lunelang.language.compiler

import org.antlr.v4.runtime.ParserRuleContext
import org.graalvm.collections.EconomicMap

class LocalScope(val parent: LocalScope?) {
    private val locals = EconomicMap.create<String, LocalVariable>()
    private val labels = EconomicMap.create<String, Instruction>()

    fun getLocal(name: String): LocalVariable? {
        return locals.get(name) ?: parent?.getLocal(name)
    }

    fun declareLocal(name: String): LocalVariable {
        return LocalVariable().also { locals.put(name, it) }
    }

    fun getLabel(name: String): Instruction? {
        return labels.get(name) ?: parent?.getLabel(name)
    }

    fun <I : Instruction> declareLabel(name: String, target: I): I? {
        return if (labels.putIfAbsent(name, target) == null) {
            target
        } else {
            null
        }
    }
}

class FunctionTranslator(parent: FunctionTranslator?, formalArguments: Iterable<String>) {
    val function: Function = Function(parent?.function)

    private fun translate(context: LuneParser.NilExpressionContext): NilConstantInstruction {
        return function.append(NilConstantInstruction()).attachSourceLocation(context)
    }

    private fun translate(context: LuneParser.TrueExpressionContext): BooleanConstantInstruction {
        return function.append(BooleanConstantInstruction(true)).attachSourceLocation(context)
    }

    private fun translate(context: LuneParser.FalseExpressionContext): BooleanConstantInstruction {
        return function.append(BooleanConstantInstruction(false)).attachSourceLocation(context)
    }

    private fun translate(context: LuneParser.ShortLiteralStringExpressionContext): ObjectConstantExpression {
        TODO("Handle short string literal expressions")
    }

    private fun translate(context: LuneParser.LongLiteralStringExpressionContext): ObjectConstantExpression {
        TODO("Handle long string literal expressions")
    }

    private fun translate(context: LuneParser.DecimalIntegerExpressionContext): ScalarInstruction {
        val tokenText = context.token.text

        val instruction = try {
            LongConstantInstruction(tokenText.toLong())
        } catch (_: NumberFormatException) {
            DoubleConstantInstruction(tokenText.toDouble())
        }

        return function.append(instruction).attachSourceLocation(context)
    }

    private fun translate(context: LuneParser.HexadecimalIntegerExpressionContext): LongConstantInstruction {
        val instruction = LongConstantInstruction(context.token.text.substring(2).toBigInteger().toLong())
        return function.append(instruction).attachSourceLocation(context)
    }

    private fun translate(context: LuneParser.DecimalFloatExpressionContext): DoubleConstantInstruction {
        val instruction = DoubleConstantInstruction(context.token.text.toDouble())
        return function.append(instruction).attachSourceLocation(context)
    }

    private fun translate(context: LuneParser.HexadecimalFloatExpressionContext): DoubleConstantInstruction {
        val instruction = DoubleConstantInstruction(context.token.text.toDouble())
        return function.append(instruction).attachSourceLocation(context)
    }

    private fun translate(context: LuneParser.VarargsExpressionContext): TrailingArgumentsInstruction {
        TODO()
        // return function.append(TrailingArgumentsInstruction()).attachSourceLocation(context)
    }

    private fun translate(context: LuneParser.PrefixOperatorExpressionContext): UnaryOpInstruction {
        val op = when (context.operator.type) {
            LuneParser.Minus -> UnaryOp.Negate
            LuneParser.Not -> UnaryOp.Not
            LuneParser.Pound -> UnaryOp.Length
            LuneParser.Tilde -> UnaryOp.BitwiseNot
            else -> error("Unexpected unary operator")
        }

        val operand = scalarize(translate(context.operand))

        return function.append(UnaryOpInstruction(op, operand)).attachSourceLocation(context)
    }

    private fun translate(context: LuneParser.AddSubtractExpressionContext): BinaryOpInstruction {
        val op = when (context.operator.type) {
            LuneParser.Plus -> BinaryOp.Add
            LuneParser.Minus -> BinaryOp.Subtract
            else -> error("Unexpected operator")
        }

        val lhs = scalarize(translate(context.lhs))
        val rhs = scalarize(translate(context.rhs))

        return function.append(BinaryOpInstruction(op, lhs, rhs)).attachSourceLocation(context)
    }

    private fun translate(context: LuneParser.PowerExpressionContext): BinaryOpInstruction {
        val lhs = scalarize(translate(context.lhs))
        val rhs = scalarize(translate(context.rhs))
        return function.append(BinaryOpInstruction(BinaryOp.Power, lhs, rhs)).attachSourceLocation(context)
    }

    private fun translate(context: LuneParser.MultiplyDivideModuloExpressionContext): BinaryOpInstruction {
        val op = when (context.operator.type) {
            LuneParser.Star -> BinaryOp.Multiply
            LuneParser.Slash -> BinaryOp.Divide
            LuneParser.Slash2 -> BinaryOp.FloorDivide
            LuneParser.Percent -> BinaryOp.Remainder
            else -> error("Unexpected operator")
        }

        val lhs = scalarize(translate(context.lhs))
        val rhs = scalarize(translate(context.rhs))

        return function.append(BinaryOpInstruction(op, lhs, rhs)).attachSourceLocation(context)
    }

    private fun translate(context: LuneParser.ConcatenateExpressionContext): BinaryOpInstruction {
        val lhs = scalarize(translate(context.lhs))
        val rhs = scalarize(translate(context.rhs))
        return function.append(BinaryOpInstruction(BinaryOp.Concatenate, lhs, rhs)).attachSourceLocation(context)
    }

    private fun translate(context: LuneParser.ShiftExpressionContext): BinaryOpInstruction {
        val op = when (context.operator.type) {
            LuneParser.LAngle2 -> BinaryOp.ShiftLeft
            LuneParser.RAngle2 -> BinaryOp.ShiftRight
            else -> error("Unexpected operator")
        }

        val lhs = scalarize(translate(context.lhs))
        val rhs = scalarize(translate(context.rhs))

        return function.append(BinaryOpInstruction(op, lhs, rhs)).attachSourceLocation(context)
    }

    private fun translate(context: LuneParser.BitwiseAndExpressionContext): BinaryOpInstruction {
        val lhs = scalarize(translate(context.lhs))
        val rhs = scalarize(translate(context.rhs))
        return function.append(BinaryOpInstruction(BinaryOp.BitwiseAnd, lhs, rhs)).attachSourceLocation(context)
    }

    private fun translate(context: LuneParser.BitwiseXOrExpressionContext): BinaryOpInstruction {
        val lhs = scalarize(translate(context.lhs))
        val rhs = scalarize(translate(context.rhs))
        return function.append(BinaryOpInstruction(BinaryOp.BitwiseXOr, lhs, rhs)).attachSourceLocation(context)
    }

    private fun translate(context: LuneParser.BitwiseOrExpressionContext): BinaryOpInstruction {
        val lhs = scalarize(translate(context.lhs))
        val rhs = scalarize(translate(context.rhs))
        return function.append(BinaryOpInstruction(BinaryOp.BitwiseOr, lhs, rhs)).attachSourceLocation(context)
    }

    private fun translate(context: LuneParser.ComparisonExpressionContext): BinaryOpInstruction {
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

        return function.append(BinaryOpInstruction(op, lhs, rhs)).attachSourceLocation(context)
    }

    private fun translate(context: LuneParser.AndExpressionContext): ScalarInstruction {
        val result = LocalVariable()

        val lhs = scalarize(translate(context.lhs))
        val branchToTrue = function.append(ConditionalBranchInstruction(lhs))
        val falseConstant = function.append(BooleanConstantInstruction(false))
        function.append(LocalStoreInstruction(result, falseConstant))
        val branchToConverge = function.append(UnconditionalBranchInstruction())
        val rhs = scalarize(translate(context.rhs))

        function.append(truePath)
        currentBlock = truePath
        val rhs = scalarize(translate(context.rhs))
        truePath.append(LocalStoreInstruction(result, rhs))

        function.append(convergedPath)
        currentBlock = convergedPath
        return convergedPath.append(LocalLoadInstruction(result))
    }

    private fun translate(context: LuneParser.OrExpressionContext): Instruction {
        val falsePath = IrBlock()
        val convergedPath = IrBlock()
        val result = LocalVariable()

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

    private fun translate(context: LuneParser.EmptyStatementContext) = Unit

    private fun translate(context: LuneParser.AssignmentStatementContext) {
        TODO("Handle assignment statements")
    }

    private fun translate(context: LuneParser.LabelStatementContext) {
        TODO("Handle label statements")
    }

    fun translate(context: LuneParser.StatementContext) {
        when (context) {
            is LuneParser.EmptyStatementContext -> translate(context)
            is LuneParser.AssignmentStatementContext -> translate(context)
            is LuneParser.LabelStatementContext -> translate(context)
        }
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

    private fun scalarize(instruction: Instruction): Instruction {
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
