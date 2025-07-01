/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;

public class EntitySalmonSizeFix
extends ChoiceFix {
    public EntitySalmonSizeFix(Schema outputSchema) {
        super(outputSchema, false, "EntitySalmonSizeFix", TypeReferences.ENTITY, "minecraft:salmon");
    }

    @Override
    protected Typed<?> transform(Typed<?> inputTyped) {
        return inputTyped.update(DSL.remainderFinder(), dynamic -> {
            String string = dynamic.get("type").asString("medium");
            if (string.equals("large")) {
                return dynamic;
            }
            return dynamic.set("type", dynamic.createString("medium"));
        });
    }
}

