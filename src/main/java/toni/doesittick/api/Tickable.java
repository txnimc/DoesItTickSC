package toni.doesittick.api;

public final class Tickable {
    public interface EntityType {
        Boolean doespotatotick$shouldAlwaysTick();
        Boolean doespotatotick$shouldAlwaysTickInRaid();
    }

    public interface Level {
        boolean doespotatotick$isInOptimizableDimension();
        void doespotatotick$setIsInOptimizableDimension();
    }
}
