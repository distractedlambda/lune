package org.lunelang.language.runtime;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import static com.oracle.truffle.api.CompilerAsserts.neverPartOfCompilation;
import static java.lang.Math.addExact;

public final class InternedStringSet {
    private final ReferenceQueue<LuneString> queue = new ReferenceQueue<>();
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

    public LuneString intern(LuneString string) {
        neverPartOfCompilation();

        for (var entry = (Entry) queue.poll(); entry != null; entry = (Entry) queue.poll()) {
            overestimatedSize--;
        }

        if ((overestimatedSize + 1) * 8L > entries.length * 7L) {
            grow();
        }

        var hashCode = string.hashCode();
        var index = hashCode % entries.length;

        for (Entry prior = null, entry = entries[index]; entry != null; entry = entry.next) {
            var entryString = entry.get();
            if (entryString == null) {
                if (prior != null) {
                    prior.next = entry.next;
                } else {
                    entries[index] = entry.next;
                }
            } else if (entry.hashCode == hashCode && string.equals(entryString)) {
                return entryString;
            } else {
                prior = entry;
            }
        }

        entries[index] = new Entry(string, queue, hashCode, entries[index]);
        return string;
    }

    public LuneString intern(String string) {
        neverPartOfCompilation();
        return intern(new LuneString(string));
    }

    private static final class Entry extends WeakReference<LuneString> {
        private final int hashCode;
        private Entry next;

        public Entry(LuneString referent, ReferenceQueue<LuneString> queue, int hashCode, Entry next) {
            super(referent, queue);
            this.hashCode = hashCode;
            this.next = next;
        }
    }
}
