package toni.doesittick.mixin;

import toni.doesittick.api.Tickable;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityType.class)
public class EntityTypeMixin implements Tickable.EntityType {

    @Unique private boolean doespotatotick$shouldAlwaysTick, doespotatotick$shouldAlwaysTickInRaid;

    @Override
    public boolean doespotatotick$shouldAlwaysTick() {
        return this.doespotatotick$shouldAlwaysTick;
    }

    @Override
    public void doespotatotick$setShouldAlwaysTick() {
        this.doespotatotick$shouldAlwaysTick = true;
    }

    @Override
    public boolean doespotatotick$shouldAlwaysTickInRaid() {
        return this.doespotatotick$shouldAlwaysTickInRaid;
    }

    @Override
    public void doespotatotick$setShouldAlwaysTickInRaid() {
        this.doespotatotick$shouldAlwaysTickInRaid = true;
    }
}
