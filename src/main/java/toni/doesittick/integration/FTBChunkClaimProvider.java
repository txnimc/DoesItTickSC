package toni.doesittick.integration;

import toni.doesittick.mixin.ClaimedChunkManagerAccessor;
import dev.ftb.mods.ftbchunks.api.FTBChunksAPI;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public final class FTBChunkClaimProvider implements IChunkClaimProvider {
    @Override
    public boolean isInClaimedChunk(Level level, BlockPos pos) {
        if (!FTBChunksAPI.api().isManagerLoaded()) return true;
        return ((ClaimedChunkManagerAccessor)FTBChunksAPI.api().getManager()).getClaimedChunks().containsKey(new ChunkDimPos(level, pos));
    }
}
