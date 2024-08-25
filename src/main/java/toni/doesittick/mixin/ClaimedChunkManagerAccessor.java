package toni.doesittick.mixin;

import dev.ftb.mods.ftbchunks.api.ClaimedChunk;
import dev.ftb.mods.ftbchunks.data.ClaimedChunkManagerImpl;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = ClaimedChunkManagerImpl.class, remap = false)
public interface ClaimedChunkManagerAccessor {
    @Accessor("claimedChunks")
    Map<ChunkDimPos, ClaimedChunk> getClaimedChunks();
}