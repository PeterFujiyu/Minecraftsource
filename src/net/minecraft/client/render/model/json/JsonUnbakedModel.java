/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelElementTexture;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class JsonUnbakedModel
implements UnbakedModel {
    @VisibleForTesting
    static final Gson GSON = new GsonBuilder().registerTypeAdapter((Type)((Object)JsonUnbakedModel.class), new Deserializer()).registerTypeAdapter((Type)((Object)ModelElement.class), new ModelElement.Deserializer()).registerTypeAdapter((Type)((Object)ModelElementFace.class), new ModelElementFace.Deserializer()).registerTypeAdapter((Type)((Object)ModelElementTexture.class), new ModelElementTexture.Deserializer()).registerTypeAdapter((Type)((Object)Transformation.class), new Transformation.Deserializer()).registerTypeAdapter((Type)((Object)ModelTransformation.class), new ModelTransformation.Deserializer()).create();
    private final List<ModelElement> elements;
    @Nullable
    private final UnbakedModel.GuiLight guiLight;
    @Nullable
    private final Boolean ambientOcclusion;
    @Nullable
    private final ModelTransformation transformations;
    @VisibleForTesting
    private final ModelTextures.Textures textures;
    @Nullable
    private UnbakedModel parent;
    @Nullable
    private final Identifier parentId;

    public static JsonUnbakedModel deserialize(Reader input) {
        return JsonHelper.deserialize(GSON, input, JsonUnbakedModel.class);
    }

    public JsonUnbakedModel(@Nullable Identifier parentId, List<ModelElement> elements, ModelTextures.Textures textures, @Nullable Boolean ambientOcclusion, @Nullable UnbakedModel.GuiLight guiLight, @Nullable ModelTransformation transformations) {
        this.elements = elements;
        this.ambientOcclusion = ambientOcclusion;
        this.guiLight = guiLight;
        this.textures = textures;
        this.parentId = parentId;
        this.transformations = transformations;
    }

    @Override
    @Nullable
    public Boolean getAmbientOcclusion() {
        return this.ambientOcclusion;
    }

    @Override
    @Nullable
    public UnbakedModel.GuiLight getGuiLight() {
        return this.guiLight;
    }

    @Override
    public void resolve(ResolvableModel.Resolver resolver) {
        if (this.parentId != null) {
            this.parent = resolver.resolve(this.parentId);
        }
    }

    @Override
    @Nullable
    public UnbakedModel getParent() {
        return this.parent;
    }

    @Override
    public ModelTextures.Textures getTextures() {
        return this.textures;
    }

    @Override
    @Nullable
    public ModelTransformation getTransformation() {
        return this.transformations;
    }

    @Override
    public BakedModel bake(ModelTextures textures, Baker baker, ModelBakeSettings settings, boolean ambientOcclusion, boolean isSideLit, ModelTransformation transformation) {
        if (this.elements.isEmpty() && this.parent != null) {
            return this.parent.bake(textures, baker, settings, ambientOcclusion, isSideLit, transformation);
        }
        return BasicBakedModel.bake(this.elements, textures, baker.getSpriteGetter(), settings, ambientOcclusion, isSideLit, true, transformation);
    }

    @Nullable
    @VisibleForTesting
    List<ModelElement> getElements() {
        return this.elements;
    }

    @Nullable
    @VisibleForTesting
    Identifier getParentId() {
        return this.parentId;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Deserializer
    implements JsonDeserializer<JsonUnbakedModel> {
        @Override
        public JsonUnbakedModel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            List<ModelElement> list = this.elementsFromJson(jsonDeserializationContext, jsonObject);
            String string = this.parentFromJson(jsonObject);
            ModelTextures.Textures lv = this.texturesFromJson(jsonObject);
            Boolean boolean_ = this.ambientOcclusionFromJson(jsonObject);
            ModelTransformation lv2 = null;
            if (jsonObject.has("display")) {
                JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "display");
                lv2 = (ModelTransformation)jsonDeserializationContext.deserialize(jsonObject2, (Type)((Object)ModelTransformation.class));
            }
            UnbakedModel.GuiLight lv3 = null;
            if (jsonObject.has("gui_light")) {
                lv3 = UnbakedModel.GuiLight.byName(JsonHelper.getString(jsonObject, "gui_light"));
            }
            Identifier lv4 = string.isEmpty() ? null : Identifier.of(string);
            return new JsonUnbakedModel(lv4, list, lv, boolean_, lv3, lv2);
        }

        private ModelTextures.Textures texturesFromJson(JsonObject object) {
            if (object.has("textures")) {
                JsonObject jsonObject2 = JsonHelper.getObject(object, "textures");
                return ModelTextures.fromJson(jsonObject2, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
            }
            return ModelTextures.Textures.EMPTY;
        }

        private String parentFromJson(JsonObject json) {
            return JsonHelper.getString(json, "parent", "");
        }

        @Nullable
        protected Boolean ambientOcclusionFromJson(JsonObject json) {
            if (json.has("ambientocclusion")) {
                return JsonHelper.getBoolean(json, "ambientocclusion");
            }
            return null;
        }

        protected List<ModelElement> elementsFromJson(JsonDeserializationContext context, JsonObject json) {
            if (json.has("elements")) {
                ArrayList<ModelElement> list = new ArrayList<ModelElement>();
                for (JsonElement jsonElement : JsonHelper.getArray(json, "elements")) {
                    list.add((ModelElement)context.deserialize(jsonElement, (Type)((Object)ModelElement.class)));
                }
                return list;
            }
            return List.of();
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement element, Type unused, JsonDeserializationContext ctx) throws JsonParseException {
            return this.deserialize(element, unused, ctx);
        }
    }
}

