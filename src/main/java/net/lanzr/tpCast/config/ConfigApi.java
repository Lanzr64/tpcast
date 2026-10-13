package net.lanzr.tpCast.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ConfigApi {
    private ConfigApi() {
    }

    public static class ConfigEntry<T extends ForgeConfigSpec.ConfigValue<P>, P> {
        private final T configValue;

        public ConfigEntry(T configValue) {
            this.configValue = configValue;
        }

        public P get() {
            return configValue.get();
        }
    }
}
