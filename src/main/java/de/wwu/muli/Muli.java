package de.wwu.muli;

import de.wwu.muli.search.SolutionIterator;
import de.wwu.muli.solution.MuliFailException;
import de.wwu.muli.solution.Solution;

import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Muli {
    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static <T> Stream<Solution<T>> muli(Supplier<T> searchRegion, SearchStrategy strategy) {
        // Create an iterator maintaining the search region.
        SolutionIterator<T> iterator = new SolutionIterator<T>(searchRegion);
        setSearchStrategyVM(iterator, strategy);
        // Create a non-parallelisable stream from the iterator.
        return StreamSupport.stream(iterator, false);
    }

    public static <T> Stream<Solution<T>> muliWithInputs(Supplier<T> searchRegion,
                                                         SearchStrategy strategy) {
        SolutionIterator<T> iterator = new SolutionIterator<>(searchRegion, true);
        setSearchStrategyVM(iterator, strategy);
        return StreamSupport.stream(iterator, false);
    }

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static <T> Stream<Solution<T>> muli(Supplier<T> searchRegion) {
        return muli(searchRegion, SearchStrategy.DepthFirstSearch);
    }

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static <T> Solution<T> getOneSolution(Supplier<T> searchRegion, SearchStrategy strategy) {
        // Throws NoElementException (via Optional.get()).
        Stream<Solution<T>> search = Muli.<T>muli(searchRegion, strategy);
        return search
                .filter(x -> !x.isExceptionControlFlow())
                .findFirst()
                .get();
    }

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static <T> Solution<T> getOneSolutionEx(Supplier<T> searchRegion, SearchStrategy strategy) {
        // Throws NoElementException (via Optional.get()).
        Stream<Solution<T>> search = Muli.<T>muli(searchRegion, strategy);
        return search
                .findFirst()
                .get();
    }

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static <T> Solution<T>[] getAllSolutions(Supplier<T> searchRegion, SearchStrategy strategy) {
        Stream<Solution<T>> search = Muli.<T>muli(searchRegion, strategy);
        return (Solution<T>[]) search
                .filter(x -> !x.isExceptionControlFlow())
                .toArray((size) -> new Solution[size]);
    }

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static <T> Solution<T>[] getAllSolutionsEx(Supplier<T> searchRegion, SearchStrategy strategy) {
        Stream<Solution<T>> search = Muli.<T>muli(searchRegion, strategy);
        return (Solution<T>[]) search
                .toArray((size) -> new Solution[size]);
    }

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static <T> Solution<T> getOneSolution(Supplier<T> searchRegion) {
        return getOneSolution(searchRegion, SearchStrategy.DepthFirstSearch);
    }

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static <T> Solution<T> getOneSolutionEx(Supplier<T> searchRegion) {
        return getOneSolutionEx(searchRegion, SearchStrategy.DepthFirstSearch);
    }

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static <T> Solution<T>[] getAllSolutions(Supplier<T> searchRegion) {
        return getAllSolutions(searchRegion, SearchStrategy.DepthFirstSearch);
    }

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static <T> Solution<T>[] getAllSolutionsEx(Supplier<T> searchRegion) {
        return getAllSolutionsEx(searchRegion, SearchStrategy.DepthFirstSearch);
    }

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static native MuliFailException fail(); // Can't be declared as "throws MuliFailE..." because compiler does not recognise it as invariant

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static native Object label(Object o);

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static native void setSearchStrategyVM(SolutionIterator iterator, SearchStrategy strategy);

    public static native ExecutionMode getVMExecutionMode();
    public static native void setVMExecutionMode(ExecutionMode mode);

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static native String executeOnShell(String cmd, String pathToTemp, String prefix, String suffix, String script);
}