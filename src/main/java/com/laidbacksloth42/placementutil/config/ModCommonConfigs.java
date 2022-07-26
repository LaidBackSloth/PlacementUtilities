package com.laidbacksloth42.placementutil.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModCommonConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<Boolean> TEMPORARY_WAND_ALTERNATIVE;
    public static final ForgeConfigSpec.ConfigValue<Integer> ANGEL_MODIFIER_RANGE_MAX;

    static {
        BUILDER.push("Configs for Placement Utilities");

        TEMPORARY_WAND_ALTERNATIVE = BUILDER.comment("Should the Temporary Wand place blocks, which disappear on" +
                        "RandomTicks instead of 10 Seconds after placement")
                .define("Temporary Wand Alternative", false);
        ANGEL_MODIFIER_RANGE_MAX = BUILDER.comment("The max amount of Range Modifiers on an Angel Wand")
                .defineInRange("Angel Modifier (Range) max", 10, 1, 1000);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
