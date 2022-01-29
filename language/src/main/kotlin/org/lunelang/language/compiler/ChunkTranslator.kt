package org.lunelang.language.compiler

import com.oracle.truffle.api.frame.FrameDescriptor

class FunctionTranslator(private val parent: FunctionTranslator?) {
    private val frameDescriptorBuilder = FrameDescriptor.newBuilder()
    private val bytecode = ByteArrayList()
    private val objectConstants = mutableListOf<Any>()
    private val localBindings = mutableListOf<MutableMap<String, Int>>()
    private val reusableSlots = hashSetOf<Int>()
    private var conditionalBranchCount = 0
}
