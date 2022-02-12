package org.lunelang.language.nodes.instructions;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.lunelang.language.nodes.BinaryOpNode;
import org.lunelang.language.nodes.InstructionNode;

import java.util.Arrays;

public abstract class LessThanNode extends BinaryOpNode {
    @Override
    public final InstructionNode cloneUninitialized() {
        return LessThanNodeGen.create(getResultSlot(), getLhsSlot(), getRhsSlot());
    }

    @Specialization
    protected void longLong(VirtualFrame frame, long lhs, long rhs) {
        booleanResult(frame, lhs < rhs);
    }

    @Specialization
    protected void longDouble(VirtualFrame frame, long lhs, double rhs) {
        booleanResult(frame, longDoubleBoundary(lhs, rhs));
    }

    @TruffleBoundary(allowInlining = true)
    private static boolean longDoubleBoundary(long lhs, double rhs) {
        return !Double.isNaN(rhs) && lhs < (long) Math.ceil(rhs);
    }

    @Specialization
    protected void doubleLong(VirtualFrame frame, double lhs, long rhs) {
        booleanResult(frame, doubleLongBoundary(lhs, rhs));
    }

    @TruffleBoundary(allowInlining = true)
    private static boolean doubleLongBoundary(double lhs, long rhs) {
        return !Double.isNaN(lhs) && (lhs < -0x1p63 || (long) Math.floor(lhs) < rhs);
    }

    @Specialization
    protected void doubleDouble(VirtualFrame frame, double lhs, double rhs) {
        booleanResult(frame, lhs < rhs);
    }

    @Specialization(guards = {"lhs == cachedLhs", "rhs == cachedRhs"})
    protected void stringStringCached(
        VirtualFrame frame,
        byte[] lhs,
        byte[] rhs,
        @Cached(value = "lhs", weak = true, dimensions = 0) byte[] cachedLhs,
        @Cached(value = "rhs", weak = true, dimensions = 0) byte[] cachedRhs,
        @Cached("stringStringBoundary(cachedLhs, cachedRhs)") boolean cachedResult
    ) {
        booleanResult(frame, cachedResult);
    }

    @Specialization(replaces = "stringStringCached")
    protected void stringStringUncached(VirtualFrame frame, byte[] lhs, byte[] rhs) {
        booleanResult(frame, stringStringBoundary(lhs, rhs));
    }

    @TruffleBoundary(allowInlining = true)
    protected static boolean stringStringBoundary(byte[] lhs, byte[] rhs) {
        return Arrays.compare(lhs, rhs) < 0;
    }
}
