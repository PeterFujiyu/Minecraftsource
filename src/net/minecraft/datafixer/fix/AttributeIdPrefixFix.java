/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.schemas.Schema;
import java.util.List;
import net.minecraft.datafixer.fix.AttributeRenameFix;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class AttributeIdPrefixFix
extends AttributeRenameFix {
    private static final List<String> PREFIXES = List.of("generic.", "horse.", "player.", "zombie.");

    public AttributeIdPrefixFix(Schema outputSchema) {
        super(outputSchema, "AttributeIdPrefixFix", AttributeIdPrefixFix::removePrefix);
    }

    private static String removePrefix(String id) {
        String string2 = IdentifierNormalizingSchema.normalize(id);
        for (String string3 : PREFIXES) {
            String string4 = IdentifierNormalizingSchema.normalize(string3);
            if (!string2.startsWith(string4)) continue;
            return "minecraft:" + string2.substring(string4.length());
        }
        return id;
    }
}

