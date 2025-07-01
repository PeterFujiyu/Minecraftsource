/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.data.ModelSupplier;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SimpleModelSupplier
implements ModelSupplier {
    private final Identifier parent;

    public SimpleModelSupplier(Identifier parent) {
        this.parent = parent;
    }

    @Override
    public JsonElement get() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("parent", this.parent.toString());
        return jsonObject;
    }

    @Override
    public /* synthetic */ Object get() {
        return this.get();
    }
}

