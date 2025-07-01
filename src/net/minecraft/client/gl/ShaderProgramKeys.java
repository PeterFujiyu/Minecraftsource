/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ShaderProgramKeys {
    private static final List<ShaderProgramKey> ALL = new ArrayList<ShaderProgramKey>();
    public static final ShaderProgramKey BLIT_SCREEN = ShaderProgramKeys.register("blit_screen", VertexFormats.BLIT_SCREEN);
    public static final ShaderProgramKey LIGHTMAP = ShaderProgramKeys.register("lightmap", VertexFormats.BLIT_SCREEN);
    public static final ShaderProgramKey PARTICLE = ShaderProgramKeys.register("particle", VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
    public static final ShaderProgramKey POSITION = ShaderProgramKeys.register("position", VertexFormats.POSITION);
    public static final ShaderProgramKey POSITION_COLOR = ShaderProgramKeys.register("position_color", VertexFormats.POSITION_COLOR);
    public static final ShaderProgramKey POSITION_COLOR_LIGHTMAP = ShaderProgramKeys.register("position_color_lightmap", VertexFormats.POSITION_COLOR_LIGHT);
    public static final ShaderProgramKey POSITION_COLOR_TEX_LIGHTMAP = ShaderProgramKeys.register("position_color_tex_lightmap", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
    public static final ShaderProgramKey POSITION_TEX = ShaderProgramKeys.register("position_tex", VertexFormats.POSITION_TEXTURE);
    public static final ShaderProgramKey POSITION_TEX_COLOR = ShaderProgramKeys.register("position_tex_color", VertexFormats.POSITION_TEXTURE_COLOR);
    public static final ShaderProgramKey RENDERTYPE_SOLID = ShaderProgramKeys.register("rendertype_solid", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
    public static final ShaderProgramKey RENDERTYPE_CUTOUT_MIPPED = ShaderProgramKeys.register("rendertype_cutout_mipped", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
    public static final ShaderProgramKey RENDERTYPE_CUTOUT = ShaderProgramKeys.register("rendertype_cutout", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
    public static final ShaderProgramKey RENDERTYPE_TRANSLUCENT = ShaderProgramKeys.register("rendertype_translucent", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
    public static final ShaderProgramKey RENDERTYPE_TRANSLUCENT_MOVING_BLOCK = ShaderProgramKeys.register("rendertype_translucent_moving_block", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
    public static final ShaderProgramKey RENDERTYPE_ARMOR_CUTOUT_NO_CULL = ShaderProgramKeys.register("rendertype_armor_cutout_no_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
    public static final ShaderProgramKey RENDERTYPE_ARMOR_TRANSLUCENT = ShaderProgramKeys.register("rendertype_armor_translucent", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
    public static final ShaderProgramKey RENDERTYPE_ENTITY_SOLID = ShaderProgramKeys.register("rendertype_entity_solid", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
    public static final ShaderProgramKey RENDERTYPE_ENTITY_CUTOUT = ShaderProgramKeys.register("rendertype_entity_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
    public static final ShaderProgramKey RENDERTYPE_ENTITY_CUTOUT_NO_CULL = ShaderProgramKeys.register("rendertype_entity_cutout_no_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
    public static final ShaderProgramKey RENDERTYPE_ENTITY_CUTOUT_NO_CULL_Z_OFFSET = ShaderProgramKeys.register("rendertype_entity_cutout_no_cull_z_offset", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
    public static final ShaderProgramKey RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL = ShaderProgramKeys.register("rendertype_item_entity_translucent_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
    public static final ShaderProgramKey RENDERTYPE_ENTITY_TRANSLUCENT = ShaderProgramKeys.register("rendertype_entity_translucent", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
    public static final ShaderProgramKey RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE = ShaderProgramKeys.register("rendertype_entity_translucent_emissive", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
    public static final ShaderProgramKey RENDERTYPE_ENTITY_SMOOTH_CUTOUT = ShaderProgramKeys.register("rendertype_entity_smooth_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
    public static final ShaderProgramKey RENDERTYPE_BEACON_BEAM = ShaderProgramKeys.register("rendertype_beacon_beam", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
    public static final ShaderProgramKey RENDERTYPE_ENTITY_DECAL = ShaderProgramKeys.register("rendertype_entity_decal", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
    public static final ShaderProgramKey RENDERTYPE_ENTITY_NO_OUTLINE = ShaderProgramKeys.register("rendertype_entity_no_outline", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
    public static final ShaderProgramKey RENDERTYPE_ENTITY_SHADOW = ShaderProgramKeys.register("rendertype_entity_shadow", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
    public static final ShaderProgramKey RENDERTYPE_ENTITY_ALPHA = ShaderProgramKeys.register("rendertype_entity_alpha", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
    public static final ShaderProgramKey RENDERTYPE_EYES = ShaderProgramKeys.register("rendertype_eyes", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
    public static final ShaderProgramKey RENDERTYPE_ENERGY_SWIRL = ShaderProgramKeys.register("rendertype_energy_swirl", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
    public static final ShaderProgramKey RENDERTYPE_LEASH = ShaderProgramKeys.register("rendertype_leash", VertexFormats.POSITION_COLOR_LIGHT);
    public static final ShaderProgramKey RENDERTYPE_WATER_MASK = ShaderProgramKeys.register("rendertype_water_mask", VertexFormats.POSITION);
    public static final ShaderProgramKey RENDERTYPE_OUTLINE = ShaderProgramKeys.register("rendertype_outline", VertexFormats.POSITION_TEXTURE_COLOR);
    public static final ShaderProgramKey RENDERTYPE_ARMOR_ENTITY_GLINT = ShaderProgramKeys.register("rendertype_armor_entity_glint", VertexFormats.POSITION_TEXTURE);
    public static final ShaderProgramKey RENDERTYPE_GLINT_TRANSLUCENT = ShaderProgramKeys.register("rendertype_glint_translucent", VertexFormats.POSITION_TEXTURE);
    public static final ShaderProgramKey RENDERTYPE_GLINT = ShaderProgramKeys.register("rendertype_glint", VertexFormats.POSITION_TEXTURE);
    public static final ShaderProgramKey RENDERTYPE_ENTITY_GLINT = ShaderProgramKeys.register("rendertype_entity_glint", VertexFormats.POSITION_TEXTURE);
    public static final ShaderProgramKey RENDERTYPE_TEXT = ShaderProgramKeys.register("rendertype_text", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
    public static final ShaderProgramKey RENDERTYPE_TEXT_BACKGROUND = ShaderProgramKeys.register("rendertype_text_background", VertexFormats.POSITION_COLOR_LIGHT);
    public static final ShaderProgramKey RENDERTYPE_TEXT_INTENSITY = ShaderProgramKeys.register("rendertype_text_intensity", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
    public static final ShaderProgramKey RENDERTYPE_TEXT_SEE_THROUGH = ShaderProgramKeys.register("rendertype_text_see_through", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
    public static final ShaderProgramKey RENDERTYPE_TEXT_BACKGROUND_SEE_THROUGH = ShaderProgramKeys.register("rendertype_text_background_see_through", VertexFormats.POSITION_COLOR_LIGHT);
    public static final ShaderProgramKey RENDERTYPE_TEXT_INTENSITY_SEE_THROUGH = ShaderProgramKeys.register("rendertype_text_intensity_see_through", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
    public static final ShaderProgramKey RENDERTYPE_LIGHTNING = ShaderProgramKeys.register("rendertype_lightning", VertexFormats.POSITION_COLOR);
    public static final ShaderProgramKey RENDERTYPE_TRIPWIRE = ShaderProgramKeys.register("rendertype_tripwire", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
    public static final ShaderProgramKey RENDERTYPE_END_PORTAL = ShaderProgramKeys.register("rendertype_end_portal", VertexFormats.POSITION);
    public static final ShaderProgramKey RENDERTYPE_END_GATEWAY = ShaderProgramKeys.register("rendertype_end_gateway", VertexFormats.POSITION);
    public static final ShaderProgramKey RENDERTYPE_CLOUDS = ShaderProgramKeys.register("rendertype_clouds", VertexFormats.POSITION_COLOR);
    public static final ShaderProgramKey RENDERTYPE_LINES = ShaderProgramKeys.register("rendertype_lines", VertexFormats.LINES);
    public static final ShaderProgramKey RENDERTYPE_CRUMBLING = ShaderProgramKeys.register("rendertype_crumbling", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
    public static final ShaderProgramKey RENDERTYPE_GUI = ShaderProgramKeys.register("rendertype_gui", VertexFormats.POSITION_COLOR);
    public static final ShaderProgramKey RENDERTYPE_GUI_OVERLAY = ShaderProgramKeys.register("rendertype_gui_overlay", VertexFormats.POSITION_COLOR);
    public static final ShaderProgramKey RENDERTYPE_GUI_TEXT_HIGHLIGHT = ShaderProgramKeys.register("rendertype_gui_text_highlight", VertexFormats.POSITION_COLOR);
    public static final ShaderProgramKey RENDERTYPE_GUI_GHOST_RECIPE_OVERLAY = ShaderProgramKeys.register("rendertype_gui_ghost_recipe_overlay", VertexFormats.POSITION_COLOR);
    public static final ShaderProgramKey RENDERTYPE_BREEZE_WIND = ShaderProgramKeys.register("rendertype_breeze_wind", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);

    private static ShaderProgramKey register(String id, VertexFormat format) {
        return ShaderProgramKeys.register(id, format, Defines.EMPTY);
    }

    private static ShaderProgramKey register(String is, VertexFormat format, Defines defines) {
        ShaderProgramKey lv = new ShaderProgramKey(Identifier.ofVanilla("core/" + is), format, defines);
        ALL.add(lv);
        return lv;
    }

    public static List<ShaderProgramKey> getAll() {
        return ALL;
    }
}

