/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public class CarvingStepRemoveFix
extends DataFix {
    public CarvingStepRemoveFix(Schema outputSchema) {
        super(outputSchema, false);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("CarvingStepRemoveFix", this.getInputSchema().getType(TypeReferences.CHUNK), CarvingStepRemoveFix::removeCarvingMasks);
    }

    private static Typed<?> removeCarvingMasks(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), dynamic -> {
            Optional optional2;
            Dynamic dynamic2 = dynamic;
            Optional optional = dynamic2.get("CarvingMasks").result();
            if (optional.isPresent() && (optional2 = optional.get().get("AIR").result()).isPresent()) {
                dynamic2 = dynamic2.set("carving_mask", optional2.get());
            }
            return dynamic2.remove("CarvingMasks");
        });
    }
}

