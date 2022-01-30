package org.lunelang.language.compiler

class FunctionTranslator(parent: FunctionTranslator?) {
    val function: IrFunction = IrFunction(parent?.function)
    var currentBlock = IrBlock()

    fun translate(context: LuneParser.NilExpressionContext): Instruction {
        return NilConstantInstruction().also(currentBlock::append)
    }

    fun translate(context: LuneParser.TrueExpressionContext): Instruction {
        return BooleanConstantInstruction(true).also(currentBlock::append)
    }
}
