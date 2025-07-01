/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.item.ModelTransformationMode;

@Environment(value=EnvType.CLIENT)
public record ModelTransformation(Transformation thirdPersonLeftHand, Transformation thirdPersonRightHand, Transformation firstPersonLeftHand, Transformation firstPersonRightHand, Transformation head, Transformation gui, Transformation ground, Transformation fixed) {
    public static final ModelTransformation NONE = new ModelTransformation(Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY);

    public Transformation getTransformation(ModelTransformationMode renderMode) {
        return switch (renderMode) {
            case ModelTransformationMode.THIRD_PERSON_LEFT_HAND -> this.thirdPersonLeftHand;
            case ModelTransformationMode.THIRD_PERSON_RIGHT_HAND -> this.thirdPersonRightHand;
            case ModelTransformationMode.FIRST_PERSON_LEFT_HAND -> this.firstPersonLeftHand;
            case ModelTransformationMode.FIRST_PERSON_RIGHT_HAND -> this.firstPersonRightHand;
            case ModelTransformationMode.HEAD -> this.head;
            case ModelTransformationMode.GUI -> this.gui;
            case ModelTransformationMode.GROUND -> this.ground;
            case ModelTransformationMode.FIXED -> this.fixed;
            default -> Transformation.IDENTITY;
        };
    }

    @Environment(value=EnvType.CLIENT)
    protected static class Deserializer
    implements JsonDeserializer<ModelTransformation> {
        protected Deserializer() {
        }

        @Override
        public ModelTransformation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Transformation lv = this.parseModelTransformation(jsonDeserializationContext, jsonObject, ModelTransformationMode.THIRD_PERSON_RIGHT_HAND);
            Transformation lv2 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, ModelTransformationMode.THIRD_PERSON_LEFT_HAND);
            if (lv2 == Transformation.IDENTITY) {
                lv2 = lv;
            }
            Transformation lv3 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, ModelTransformationMode.FIRST_PERSON_RIGHT_HAND);
            Transformation lv4 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, ModelTransformationMode.FIRST_PERSON_LEFT_HAND);
            if (lv4 == Transformation.IDENTITY) {
                lv4 = lv3;
            }
            Transformation lv5 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, ModelTransformationMode.HEAD);
            Transformation lv6 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, ModelTransformationMode.GUI);
            Transformation lv7 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, ModelTransformationMode.GROUND);
            Transformation lv8 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, ModelTransformationMode.FIXED);
            return new ModelTransformation(lv2, lv, lv4, lv3, lv5, lv6, lv7, lv8);
        }

        private Transformation parseModelTransformation(JsonDeserializationContext ctx, JsonObject json, ModelTransformationMode displayContext) {
            String string = displayContext.asString();
            if (json.has(string)) {
                return (Transformation)ctx.deserialize(json.get(string), (Type)((Object)Transformation.class));
            }
            return Transformation.IDENTITY;
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement functionJson, Type unused, JsonDeserializationContext context) throws JsonParseException {
            return this.deserialize(functionJson, unused, context);
        }
    }
}

