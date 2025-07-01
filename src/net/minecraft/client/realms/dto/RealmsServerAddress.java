/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.ValueObject;
import net.minecraft.client.realms.util.JsonUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsServerAddress
extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    @Nullable
    public String address;
    @Nullable
    public String resourcePackUrl;
    @Nullable
    public String resourcePackHash;

    public static RealmsServerAddress parse(String json) {
        RealmsServerAddress lv = new RealmsServerAddress();
        try {
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            lv.address = JsonUtils.getNullableStringOr("address", jsonObject, null);
            lv.resourcePackUrl = JsonUtils.getNullableStringOr("resourcePackUrl", jsonObject, null);
            lv.resourcePackHash = JsonUtils.getNullableStringOr("resourcePackHash", jsonObject, null);
        } catch (Exception exception) {
            LOGGER.error("Could not parse RealmsServerAddress: {}", (Object)exception.getMessage());
        }
        return lv;
    }
}

