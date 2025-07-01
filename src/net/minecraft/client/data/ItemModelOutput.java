/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.data;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.item.Item;

@Environment(value=EnvType.CLIENT)
public interface ItemModelOutput {
    public void accept(Item var1, ItemModel.Unbaked var2);

    public void acceptAlias(Item var1, Item var2);
}

