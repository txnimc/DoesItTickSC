package toni.doesittick.api;

public final class Tickable {
    public interface EntityType {
        boolean doespotatotick$shouldAlwaysTick();
        void doespotatotick$setShouldAlwaysTick();
        boolean doespotatotick$shouldAlwaysTickInRaid();
        void doespotatotick$setShouldAlwaysTickInRaid();
    }

    public interface Level {
        boolean doespotatotick$isInOptimizableDimension();
        void doespotatotick$setIsInOptimizableDimension();
    }
}
