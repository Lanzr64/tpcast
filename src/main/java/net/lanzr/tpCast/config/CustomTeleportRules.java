package net.lanzr.tpCast.config;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Resolves the configured item IDs once for each menu or help request. */
public record CustomTeleportRules(Map<Item, Integer> x, Map<Item, Integer> z) {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static CustomTeleportRules fromConfig() {
        return new CustomTeleportRules(
                readRules(Config.CUSTP_X_ITEMS.get(), "X"),
                readRules(Config.CUSTP_Z_ITEMS.get(), "Z"));
    }

    private static Map<Item, Integer> readRules(List<? extends String> entries, String axis) {
        Map<Item, Integer> rules = new LinkedHashMap<>();
        for (String entry : entries) {
            int separator = entry.indexOf('=');
            if (separator < 1 || separator == entry.length() - 1) {
                LOGGER.warn("Invalid {} coordinate teleport item rule: {}", axis, entry);
                continue;
            }
            ResourceLocation id = ResourceLocation.tryParse(entry.substring(0, separator).trim());
            int blocks;
            try {
                blocks = Integer.parseInt(entry.substring(separator + 1).trim());
            } catch (NumberFormatException error) {
                LOGGER.warn("Invalid {} coordinate teleport item rule: {}", axis, entry);
                continue;
            }
            if (id == null || blocks <= 0 || !ForgeRegistries.ITEMS.containsKey(id)) {
                LOGGER.warn("Invalid {} coordinate teleport item rule: {}", axis, entry);
                continue;
            }
            Item item = ForgeRegistries.ITEMS.getValue(id);
            if (item == null || item == Items.AIR || rules.putIfAbsent(item, blocks) != null) {
                LOGGER.warn("Invalid or duplicate {} coordinate teleport item rule: {}", axis, entry);
            }
        }
        return Collections.unmodifiableMap(rules);
    }
}
