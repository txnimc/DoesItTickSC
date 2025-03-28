package toni.doesittick.mixin;

import net.minecraft.core.Holder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import toni.doesittick.DoesItTick;
import toni.doesittick.api.Tickable;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityType.class)
public class EntityTypeMixin implements Tickable.EntityType {
    @Shadow @Final private Holder.Reference<EntityType<?>> builtInRegistryHolder;
    @Unique private Boolean doespotatotick$shouldAlwaysTick = null;
    @Unique private Boolean doespotatotick$shouldAlwaysTickInRaid = null;

    @Override
    public Boolean doespotatotick$shouldAlwaysTick() {
        if (doespotatotick$shouldAlwaysTick == null) {
            var id = builtInRegistryHolder.key().location();
            doespotatotick$shouldAlwaysTick =
                DoesItTick.ENTITIES_WHITELIST.get().contains(id.toString()) ||
                    DoesItTick.ENTITIES_MOD_ID_WHITELIST.get().contains(id.getNamespace());
        }

        return this.doespotatotick$shouldAlwaysTick;
    }

    @Override
    public Boolean doespotatotick$shouldAlwaysTickInRaid() {
        if (doespotatotick$shouldAlwaysTickInRaid == null) {
            var id = builtInRegistryHolder.key().location();
            doespotatotick$shouldAlwaysTickInRaid =
                DoesItTick.RAID_ENTITIES_WHITELIST.get().contains(id.toString()) ||
                    DoesItTick.RAID_ENTITIES_MOD_ID_LIST.get().contains(id.getNamespace());
        }

        return this.doespotatotick$shouldAlwaysTickInRaid;
    }
}
