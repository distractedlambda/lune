package org.lunelang.language.runtime;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.oracle.truffle.api.CompilerAsserts.neverPartOfCompilation;
import static java.lang.Math.addExact;

public final class InternedStringSet {
    private final ReferenceQueue<InternedString> queue = new ReferenceQueue<>();
    private Entry[] entries = new Entry[8];
    private int overestimatedSize;

    private void grow() {
        var newEntries = new Entry[addExact(entries.length, entries.length / 2)];

        for (var nextEntry : entries) {
            while (nextEntry != null) {
                var entry = nextEntry;
                nextEntry = entry.next;
                var entryString = entry.get();
                if (entryString == null) continue;
                var insertionIndex = entryString.hashCode() % newEntries.length;
                entry.next = newEntries[insertionIndex];
                newEntries[insertionIndex] = entry;
            }
        }

        entries = newEntries;
    }

    public InternedString intern(byte[] bytes) {
        neverPartOfCompilation();

        for (var entry = (Entry) queue.poll(); entry != null; entry = (Entry) queue.poll()) {
            overestimatedSize--;
        }

        if ((overestimatedSize + 1) * 8L > entries.length * 7L) {
            grow();
        }

        var hashCode = (int) FxHash.hash(bytes);
        var index = hashCode % entries.length;

        for (Entry prior = null, entry = entries[index]; entry != null; entry = entry.next) {
            var entryString = entry.get();
            if (entryString == null) {
                if (prior != null) {
                    prior.next = entry.next;
                } else {
                    entries[index] = entry.next;
                }
            } else if (entryString.hashCode() == hashCode && Arrays.equals(bytes, entryString.getBytes())) {
                return entryString;
            } else {
                prior = entry;
            }
        }

        var newString = new InternedString(bytes, hashCode);
        entries[index] = new Entry(newString, queue, entries[index]);
        return newString;
    }

    public InternedString intern(String string) {
        neverPartOfCompilation();
        return intern(string.getBytes(StandardCharsets.UTF_8));
    }

    private static final class Entry extends WeakReference<InternedString> {
        private Entry next;

        public Entry(InternedString referent, ReferenceQueue<InternedString> queue, Entry next) {
            super(referent, queue);
            this.next = next;
        }
    }
}
