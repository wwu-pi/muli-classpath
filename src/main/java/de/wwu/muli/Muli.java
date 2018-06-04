package de.wwu.muli;

import de.wwu.muli.search.SolutionIterator;
import de.wwu.muli.solution.MuliFailException;
import de.wwu.muli.solution.Solution;

import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Muli {

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static <T> Stream<Solution<T>> search(Find find, SearchStrategy strategy, Supplier<T> searchRegion) {
        return Muli.muli(searchRegion, strategy);
        // TODO Use find preference to stop search and/or filter results (cf. getOneValue et al).
    }

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static <T> Stream<Solution<T>> search(Find find, Supplier<T> searchRegion) {
        return search(find, SearchStrategy.IterativeDeepening, searchRegion);
    }

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static <T> Stream<Solution<T>> muli(Supplier<T> searchRegion, SearchStrategy strategy) {
        // Create an iterator maintaining the search region.
        SolutionIterator<T> iterator = new SolutionIterator<>(searchRegion);
        setSearchStrategyVM(iterator, strategy);
        // Create a non-parallelisable stream from the iterator.
        return StreamSupport.stream(iterator, false);
    }

    public static <T> T getOneValue(Supplier<T> searchArea) {
        Stream<Solution<T>> search = Muli.<T>search(Find.First, searchArea);
        return search
                .filter(x -> !x.isExceptionControlFlow())
                .findFirst()
                .get()
                .value;
    }

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static native MuliFailException fail(); // Can't be declared as "throws MuliFailE..." because compiler does not recognise it as invariant

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static native void label();

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static native void setSearchStrategyVM(SolutionIterator iterator, SearchStrategy strategy);

    public static native ExecutionMode getVMExecutionMode();
    public static native void setVMExecutionMode(ExecutionMode mode);

    // TODO maybe remove these from VM.
    private static native void recordSolutionAndBacktrackVM(Object solution);
    private static native void recordExceptionAndBacktrackVM(Throwable exception);

    private static native <T> Solution<T>[] getVMRecordedSolutions();
}
