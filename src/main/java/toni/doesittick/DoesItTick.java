package toni.doesittick;

import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.block.Fallable;
import toni.doesittick.api.Tickable;
import toni.doesittick.integration.FTBChunkClaimProvider;
import toni.doesittick.integration.IChunkClaimProvider;
import toni.doesittick.integration.OPACChunkClaimProvider;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

#if FABRIC
    import net.fabricmc.api.ModInitializer;
    import net.fabricmc.loader.api.FabricLoader;

    #if after_21_1
    import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
    import net.neoforged.fml.config.ModConfig;
    import net.neoforged.neoforge.common.ModConfigSpec;
    import net.neoforged.neoforge.common.ModConfigSpec.*;
    #endif

    #if current_20_1
    import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
    import net.minecraftforge.common.ForgeConfigSpec;
    import net.minecraftforge.common.ForgeConfigSpec.*;
    import net.minecraftforge.fml.config.ModConfig;
    #endif
#endif

#if FORGE
    import net.minecraftforge.common.MinecraftForge;
    import net.minecraftforge.event.entity.player.PlayerEvent;
    import net.minecraftforge.fml.ModLoadingContext;
    import net.minecraftforge.fml.common.Mod;
    import net.minecraftforge.fml.config.ModConfig;
    import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
    import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
    import net.minecraftforge.fml.loading.FMLLoader;
    import net.minecraftforge.registries.ForgeRegistries;
    import net.minecraftforge.fml.common.Mod;
    import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
    import net.minecraftforge.fml.ModLoadingContext;
    import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
    import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
    import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
    #if current_20_1
    import net.minecraftforge.common.ForgeConfigSpec;
    import net.minecraftforge.common.ForgeConfigSpec.*;
    import net.minecraftforge.fml.config.ModConfig;
    #endif
#endif


#if NEO
    import net.neoforged.fml.common.Mod;
    import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
    import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
    import net.neoforged.bus.api.IEventBus;
    import net.neoforged.fml.ModContainer;
    import net.neoforged.fml.common.EventBusSubscriber;
    import net.neoforged.fml.config.ModConfig;
    import net.neoforged.neoforge.common.ModConfigSpec;
    import net.neoforged.neoforge.common.ModConfigSpec.*;
    import net.neoforged.fml.loading.FMLLoader;
#endif


#if FORGELIKE
@Mod("does_it_tick")
#endif
public class DoesItTick #if FABRIC implements ModInitializer #endif{
    public static final String MOD_ID = "does_it_tick";
    private static final boolean IS_FTB_CHUNKS_PRESENT =
            #if fabric
            FabricLoader.getInstance().isModLoaded("ftbchunks")
            #else
            FMLLoader.getLoadingModList().getModFileById("ftbchunks") != null
            #endif;

    private static final boolean IS_OPAC_PRESENT =
            #if fabric
            FabricLoader.getInstance().isModLoaded("openpartiesandclaims")
            #else
            FMLLoader.getLoadingModList().getModFileById("openpartiesandclaims") != null
            #endif;

    public static final @Nullable IChunkClaimProvider FTB_CLAIM_PROVIDER = IS_FTB_CHUNKS_PRESENT ? new FTBChunkClaimProvider() : null;
    public static final @Nullable IChunkClaimProvider OPAC_CLAIM_PROVIDER = IS_OPAC_PRESENT ? new OPACChunkClaimProvider() : null;

    public static final #if current_20_1 ForgeConfigSpec #else ModConfigSpec #endif COMMON_CONFIG;
    public static final IntValue LIVING_HORIZONTAL_TICK_DIST, LIVING_VERTICAL_TICK_DIST;
    public static final BooleanValue OPTIMIZE_ITEM_MOVEMENT, IGNORE_DEAD_ENTITIES, IGNORE_HOSTILE_ENTITIES, IGNORE_PASSIVE_ENTITIES, TICKING_RAIDER_ENTITIES_IN_RAID, OPTIMIZE_ENTITIES_TICKING, SEND_MESSAGE;
    public static final ConfigValue<List<? extends String>> ENTITIES_WHITELIST, ITEMS_WHITELIST, ENTITIES_MOD_ID_WHITELIST, RAID_ENTITIES_WHITELIST, RAID_ENTITIES_MOD_ID_LIST, DIMENSION_WHITELIST;

    static {
        List<? extends String> itemList = ObjectArrayList.wrap(new String[]{"minecraft:cobblestone"});
        List<? extends String> entityModIdList = ObjectArrayList.wrap(new String[]{"create", "witherstormmod"});
        List<? extends String> entityWhiteList = ObjectArrayList.wrap(new String[]{
                "minecraft:ender_dragon", "minecraft:ghast", "minecraft:wither", "minecraft:player",
                "alexsmobs:void_worm", "alexsmobs:void_worm_part", "alexsmobs:spectre",
                "twilightforest:naga", "twilightforest:lich", "twilightforest:yeti", "twilightforest:snow_queen", "twilightforest:minoshroom", "twilightforest:hydra", "twilightforest:knight_phantom", "twilightforest:ur_ghast",
                "atum:pharaoh",
                "mowziesmobs:barako", "mowziesmobs:ferrous_wroughtnaut", "mowziesmobs:frostmaw", "mowziesmobs:naga",
                "aoa3:skeletron", "aoa3:smash", "aoa3:baroness", "aoa3:clunkhead", "aoa3:corallus", "aoa3:cotton_candor", "aoa3:craexxeus", "aoa3:xxeus", "aoa3:creep", "aoa3:crystocore", "aoa3:dracyon", "aoa3:graw", "aoa3:gyro", "aoa3:hive_king", "aoa3:kajaros", "aoa3:miskel", "aoa3:harkos", "aoa3:raxxan", "aoa3:okazor", "aoa3:king_bambambam", "aoa3:king_shroomus", "aoa3:kror", "aoa3:mechbot", "aoa3:nethengeic_wither", "aoa3:red_guardian", "aoa3:blue_guardian", "aoa3:green_guardian", "aoa3:yellow_guardian", "aoa3:rock_rider", "aoa3:shadowlord", "aoa3:tyrosaur", "aoa3:vinecorne", "aoa3:visualent", "aoa3:voxxulon", "aoa3:bane", "aoa3:elusive",
                "gaiadimension:malachite_drone", "gaiadimension:malachite_guard",
                "blue_skies:alchemist", "blue_skies:arachnarch", "blue_skies:starlit_crusher", "blue_skies:summoner",
                "stalwart_dungeons:awful_ghast", "stalwart_dungeons:nether_keeper", "stalwart_dungeons:shelterer_without_armor",
                "dungeonsmod:extrapart", "dungeonsmod:king", "dungeonsmod:deserted", "dungeonsmod:crawler", "dungeonsmod:ironslime", "dungeonsmod:kraken", "dungeonsmod:voidmaster", "dungeonsmod:lordskeleton", "dungeonsmod:winterhunter", "dungeonsmod:sun",
                "forestcraft:beequeen", "forestcraft:iguana_king", "forestcraft:cosmic_fiend", "forestcraft:nether_scourge",
                "cataclysm:ender_golem", "cataclysm:ender_guardian", "cataclysm:ignis", "cataclysm:ignited_revenant", "cataclysm:netherite_monstrosity",
                "iceandfire:fire_dragon", "iceandfire:ice_dragon", "iceandfire:lightning_dragon", "iceandfire:dragon_multipart"
        });

        Builder builder = new Builder();
        builder.comment("DoesPotatoTick?").push("Living Entities Tick Settings");
        OPTIMIZE_ENTITIES_TICKING = builder.comment("If you disable this, entities will not stop ticking when they'are far from you, this mod may be useless for you too").define("OptimizeEntitiesTicking", true);
        LIVING_HORIZONTAL_TICK_DIST = builder.defineInRange("LivingEntitiesMaxHorizontalTickDistance", 64, 1, Integer.MAX_VALUE);
        LIVING_VERTICAL_TICK_DIST = builder.defineInRange("LivingEntitiesMaxVerticalTickDistance", 32, 1, Integer.MAX_VALUE);
        ENTITIES_WHITELIST = builder.comment("If you don't want an entity to be affected by the optimization, you can write its registry name down here.").defineList("EntitiesWhitelist", entityWhiteList, Predicates.alwaysTrue());
        ENTITIES_MOD_ID_WHITELIST = builder.comment("If you don't want entities of a mod to be affected by the optimization, you can write its modid down here").defineList("EntitiesModIDWhiteList", entityModIdList, Predicates.alwaysTrue());
        TICKING_RAIDER_ENTITIES_IN_RAID = builder.comment("With this turned on, all the raider won't stop ticking in raid chunks even if they are far from players (well this is not perfect as the raiders may walk out of the raid range)").define("TickRaidersInRaid", true);
        RAID_ENTITIES_WHITELIST = builder.comment("Similar with entity whitelist, but only take effect in raid.").defineList("RaidEntitiesWhiteList", ObjectArrayList.wrap(new String[]{"minecraft:witch", "minecraft:vex"}), Predicates.alwaysTrue());
        RAID_ENTITIES_MOD_ID_LIST = builder.comment("Similar with entity modID whitelist, but only take effect in raid").defineList("RaidEntitiesModIDWhiteList", new ObjectArrayList<>(), Predicates.alwaysTrue());
        DIMENSION_WHITELIST = builder.comment("Leave this empty for applying to all the dimensions", "Entities in these dimensions will be affected by the optimization").defineList("DimensionWhitelist", new ObjectArrayList<>(), Predicates.alwaysTrue());
        IGNORE_DEAD_ENTITIES = builder.comment("If this is enabled, tickable check will run a lot faster, but the entity will not die out of range").define("IgnoreDeadEntities", false);
        IGNORE_HOSTILE_ENTITIES = builder.comment("If this is enabled, this mod will only work on passive entities.").define("IgnoreHostileEntities", false);
        IGNORE_PASSIVE_ENTITIES = builder.comment("If this is enabled, this mod will only work on hostile entities.").define("IgnorePassiveEntities", false);
        builder.pop();
        builder.push("Item Entities Tick Settings");
        OPTIMIZE_ITEM_MOVEMENT = builder.comment("Slow down item entities' ticking speed by 1/4").define("OptimizeItemMovement", false);
        ITEMS_WHITELIST = builder.comment("If you don't want to let a specific item entity in the world to be effected by the optimization, you can write its registry name down here.", "Require 'OptimizeItemMovement' to be true").defineList("ItemWhiteList", itemList, Predicates.alwaysTrue());
        builder.pop();
        builder.push("Misc");
        SEND_MESSAGE = builder.define("SendWarningMessageWhenPlayerLogIn", true);
        builder.pop();
        COMMON_CONFIG = builder.build();
    }

    private static final Supplier<Set<Item>> ITEMS = Suppliers.memoize(() ->
            ITEMS_WHITELIST.get()
                    .stream()
                    .map(s -> #if forge ForgeRegistries.ITEMS.getValue( #else BuiltInRegistries.ITEM.get( #endif resource(s)))
                    .collect(Collectors.toSet())
    );

    public static ResourceLocation resource(String path) {
        #if AFTER_21_1
        return ResourceLocation.parse(path);
        #else
        return new ResourceLocation(path);
        #endif
    }

    public DoesItTick(#if NEO IEventBus modEventBus, ModContainer modContainer #endif) {

        #if FORGE
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG);
        #elif NEO
        modContainer.registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG);
        #elif FABRIC
            #if AFTER_21_1
            NeoForgeConfigRegistry.INSTANCE.register(DoesItTick.MOD_ID, ModConfig.Type.COMMON, COMMON_CONFIG);
            #else
            ForgeConfigRegistry.INSTANCE.register(DoesItTick.MOD_ID, ModConfig.Type.COMMON, COMMON_CONFIG);
            #endif
        #endif

        #if forge
        MinecraftForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
            if (!SEND_MESSAGE.get()) return;
            if (IS_FTB_CHUNKS_PRESENT) {
                event.getEntity().displayClientMessage(Component.translatable("doesittick.warn.1"), false);
            } else {
                event.getEntity().displayClientMessage(Component.translatable("doesittick.warn.2"), false);
            }
        });
        #endif
    }

    #if FABRIC @Override #endif
    public void onInitialize() {
        #if forge
        for (EntityType<?> entityType : ForgeRegistries.ENTITY_TYPES) {
            ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
            applyWhitelist(id, entityType);
        }
        #else
        BuiltInRegistries.ENTITY_TYPE.entrySet().forEach(kvp -> {
            applyWhitelist(kvp.getKey().location(), kvp.getValue());
        });
        #endif
    }

    private void applyWhitelist(ResourceLocation id, EntityType<?> entityType) {
        if (id != null) {
            if (ENTITIES_WHITELIST.get().contains(id.toString()) || ENTITIES_MOD_ID_WHITELIST.get().contains(id.getNamespace()))
                ((Tickable.EntityType)entityType).doespotatotick$setShouldAlwaysTick();

            if (RAID_ENTITIES_WHITELIST.get().contains(id.toString()) || RAID_ENTITIES_MOD_ID_LIST.get().contains(id.getNamespace()))
                ((Tickable.EntityType)entityType).doespotatotick$setShouldAlwaysTickInRaid();
        }
    }

    // Forg event stubs to call the Fabric initialize methods, and set up cloth config screen
    #if FORGELIKE
    public void commonSetup(FMLCommonSetupEvent event) { onInitialize(); }
    #endif

    public static boolean isTickable(@NotNull Entity entity) {
        if (!OPTIMIZE_ENTITIES_TICKING.get())
            return true;

        Level level = entity.level();
        if (!isOptimizableDim(level))
            return true;

        if (entity instanceof Fallable)
            return true;

        if (entity instanceof LivingEntity) {
            if (!IGNORE_DEAD_ENTITIES.get() && ((LivingEntity) entity).isDeadOrDying())
                return true;

            var isMonster = entity instanceof Monster || entity instanceof Slime;
            if (IGNORE_HOSTILE_ENTITIES.get() && isMonster)
                return true;

            if (IGNORE_PASSIVE_ENTITIES.get() && !isMonster)
                return true;
        }

        if (isOptimizableItemEntity(entity))
            return ThreadLocalRandom.current().nextBoolean() || ThreadLocalRandom.current().nextBoolean();

        BlockPos entityPos = entity.blockPosition();
        if (isInClaimedChunk(level, entityPos))
            return true;

        EntityType<?> entityType = entity.getType();
        if (((Tickable.EntityType) entityType).doespotatotick$shouldAlwaysTick())
            return true;

        if (shouldTickInRaid(level, entityPos, entityType, entity))
            return true;

        return isNearPlayer(level, entityPos);
    }

    private static boolean shouldTickInRaid(Level level, BlockPos blockPos, EntityType<?> entityType, Entity entity) {
        if (level instanceof ServerLevel && ((ServerLevel) level).isRaided(blockPos)) {
            if (entity instanceof Raider) return TICKING_RAIDER_ENTITIES_IN_RAID.get();
            return ((Tickable.EntityType)entityType).doespotatotick$shouldAlwaysTickInRaid();
        }
        return false;
    }

    private static boolean isOptimizableItemEntity(Entity entity) {
        if (!OPTIMIZE_ITEM_MOVEMENT.get()) return false;
        if (entity instanceof ItemEntity) {
            return !ITEMS.get().contains(((ItemEntity) entity).getItem().getItem());
        }
        return false;
    }

    private static boolean isInClaimedChunk(Level level, BlockPos pos) {
        var flag = false;
        if (FTB_CLAIM_PROVIDER != null)
            flag = FTB_CLAIM_PROVIDER.isInClaimedChunk(level, pos);

        if (OPAC_CLAIM_PROVIDER != null)
            flag = flag || OPAC_CLAIM_PROVIDER.isInClaimedChunk(level, pos);

        return flag;
    }

    private static final Supplier<Boolean> ALL_DIMS_ALLOWED = Suppliers.memoize(() -> DIMENSION_WHITELIST.get().isEmpty());

    private static boolean isOptimizableDim(Level level) {
        if (ALL_DIMS_ALLOWED.get()) return true;
        return ((Tickable.Level)level).doespotatotick$isInOptimizableDimension();
    }

    private static final Supplier<Integer> MAX_HEIGHT = Suppliers.memoize(LIVING_VERTICAL_TICK_DIST::get);
    private static final Supplier<Integer> MAX_DIST_SQUARED = Suppliers.memoize(() -> LIVING_HORIZONTAL_TICK_DIST.get() * LIVING_HORIZONTAL_TICK_DIST.get());

    private static boolean isNearPlayer(@NotNull Level level, @NotNull BlockPos pos) {
        int posX = pos.getX();
        int posY = pos.getY();
        int posZ = pos.getZ();
        int maxHeight = MAX_HEIGHT.get();
        int maxDistSquared = MAX_DIST_SQUARED.get();
        for (Player player : level.players()) {
            if (Math.abs(player.getY() - posY) < maxHeight) {
                double x = player.getX() - posX;
                double z = player.getZ() - posZ;
                if ((x * x + z * z) < maxDistSquared) return true;
            }
        }
        return false;
    }
}
