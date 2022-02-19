package org.lunelang.language.runtime;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import java.lang.ref.WeakReference;

import static com.oracle.truffle.api.CompilerAsserts.neverPartOfCompilation;
import static java.lang.Math.addExact;
import static java.util.Objects.requireNonNull;

public final class InternedSet<E> {
    public interface Equivalence<E> {
        boolean equals(E lhs, E rhs);

        int hashCode(E element);
    }

    private final Class<E> elementClass;
    private final Equivalence<E> equivalence;
    private Entry[] entries = new Entry[8];
    private int overestimatedSize;

    public InternedSet(Class<E> elementClass, Equivalence<E> equivalence) {
        neverPartOfCompilation();
        this.elementClass = requireNonNull(elementClass);
        this.equivalence = requireNonNull(equivalence);
    }

    private void growAndInsert(E element, int hashCode) {
        var newEntries = new Entry[addExact(entries.length, entries.length / 2)];

        for (var nextEntry : entries) {
            while (nextEntry != null) {
                var entry = nextEntry;
                nextEntry = entry.next;

                if (entry.get() == null) {
                    overestimatedSize--;
                    continue;
                }

                var insertionIndex = entry.hashCode % newEntries.length;
                entry.next = newEntries[insertionIndex];
                newEntries[insertionIndex] = entry;
            }
        }

        entries = newEntries;

        var insertionIndex = hashCode % entries.length;
        entries[insertionIndex] = new Entry(element, entries[insertionIndex], hashCode);
    }

    private void insertOverCapacity(E element, int insertionIndex, int hashCode) {
        for (var index = 0; index < entries.length; index++) {
            for (Entry prior = null, entry = entries[index]; entry != null; entry = entry.next) {
                if (entry.get() == null) {
                    overestimatedSize--;
                    if (prior != null) {
                        prior.next = entry.next;
                    } else {
                        entries[index] = entry.next;
                    }
                } else {
                    prior = entry;
                }
            }
        }

        if ((overestimatedSize + 1L) * 4L <= entries.length) {
            entries[insertionIndex] = new Entry(element, entries[insertionIndex], hashCode);
        } else {
            growAndInsert(element, hashCode);
        }
    }

    @TruffleBoundary
    public E intern(E element) {
        var hashCode = equivalence.hashCode(element);
        var index = hashCode % entries.length;

        for (Entry prior = null, entry = entries[index]; entry != null; entry = entry.next) {
            var entryElement = elementClass.cast(entry.get());
            if (entryElement == null) {
                overestimatedSize--;
                if (prior != null) {
                    prior.next = entry.next;
                } else {
                    entries[index] = entry.next;
                }
            } else if (entry.hashCode == hashCode && equivalence.equals(element, entryElement)) {
                return entryElement;
            } else {
                prior = entry;
            }
        }

        if ((overestimatedSize + 1L) * 4L <= entries.length * 3L) {
            entries[index] = new Entry(element, entries[index], hashCode);
        } else {
            insertOverCapacity(element, index, hashCode);
        }

        overestimatedSize++;
        return element;
    }

    private static final class Entry extends WeakReference<Object> {
        private Entry next;
        private final int hashCode;

        public Entry(Object referent, Entry next, int hashCode) {
            super(referent);
            this.next = next;
            this.hashCode = hashCode;
        }
    }
}
