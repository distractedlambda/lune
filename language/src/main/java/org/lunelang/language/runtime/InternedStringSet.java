package org.lunelang.language.runtime;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.oracle.truffle.api.CompilerAsserts.neverPartOfCompilation;
import static java.lang.Math.addExact;

public final class InternedStringSet {
    private final ReferenceQueue<byte[]> queue = new ReferenceQueue<>();
    private Entry[] entries = new Entry[8];
    private int overestimatedSize;

    private void grow() {
        var newEntries = new Entry[addExact(entries.length, entries.length / 2)];

        for (var nextEntry : entries) {
            while (nextEntry != null) {
                var entry = nextEntry;
                nextEntry = entry.next;
                if (entry.get() == null) continue;
                var insertionIndex = entry.hashCode % newEntries.length;
                entry.next = newEntries[insertionIndex];
                newEntries[insertionIndex] = entry;
            }
        }

        entries = newEntries;
    }

    public byte[] intern(byte[] string) {
        neverPartOfCompilation();

        for (var entry = (Entry) queue.poll(); entry != null; entry = (Entry) queue.poll()) {
            overestimatedSize--;
        }

        if ((overestimatedSize + 1) * 8L > entries.length * 7L) {
            grow();
        }

        var hashCode = (int) FxHash.hash(string);
        var index = hashCode % entries.length;

        for (Entry prior = null, entry = entries[index]; entry != null; entry = entry.next) {
            var entryString = entry.get();
            if (entryString == null) {
                if (prior != null) {
                    prior.next = entry.next;
                } else {
                    entries[index] = entry.next;
                }
            } else if (entry.hashCode == hashCode && Arrays.equals(string, entryString)) {
                return entryString;
            } else {
                prior = entry;
            }
        }

        entries[index] = new Entry(string, queue, entries[index], hashCode);
        return string;
    }

    public byte[] intern(String string) {
        neverPartOfCompilation();
        return intern(string.getBytes(StandardCharsets.UTF_8));
    }

    private static final class Entry extends WeakReference<byte[]> {
        private Entry next;
        private final int hashCode;

        public Entry(byte[] referent, ReferenceQueue<byte[]> queue, Entry next, int hashCode) {
            super(referent, queue);
            this.next = next;
            this.hashCode = hashCode;
        }
    }
}
