/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.TriState;
import net.minecraft.util.Util;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

@Environment(value=EnvType.CLIENT)
public abstract class RenderPhase {
    public static final double field_42230 = 8.0;
    protected final String name;
    private final Runnable beginAction;
    private final Runnable endAction;
    protected static final Transparency NO_TRANSPARENCY = new Transparency("no_transparency", () -> RenderSystem.disableBlend(), () -> {});
    protected static final Transparency ADDITIVE_TRANSPARENCY = new Transparency("additive_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final Transparency LIGHTNING_TRANSPARENCY = new Transparency("lightning_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final Transparency GLINT_TRANSPARENCY = new Transparency("glint_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_COLOR, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final Transparency CRUMBLING_TRANSPARENCY = new Transparency("crumbling_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.DST_COLOR, GlStateManager.DstFactor.SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final Transparency OVERLAY_TRANSPARENCY = new Transparency("overlay_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final Transparency TRANSLUCENT_TRANSPARENCY = new Transparency("translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final Transparency VIGNETTE_TRANSPARENCY = new Transparency("vignette_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final Transparency CROSSHAIR_TRANSPARENCY = new Transparency("crosshair_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final Transparency MOJANG_LOGO_TRANSPARENCY = new Transparency("mojang_logo_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(770, 1);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final Transparency NAUSEA_OVERLAY_TRANSPARENCY = new Transparency("nausea_overlay_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final ShaderProgram NO_PROGRAM = new ShaderProgram();
    protected static final ShaderProgram POSITION_COLOR_LIGHTMAP_PROGRAM = new ShaderProgram(ShaderProgramKeys.POSITION_COLOR_LIGHTMAP);
    protected static final ShaderProgram POSITION_PROGRAM = new ShaderProgram(ShaderProgramKeys.POSITION);
    protected static final ShaderProgram POSITION_TEXTURE_PROGRAM = new ShaderProgram(ShaderProgramKeys.POSITION_TEX);
    protected static final ShaderProgram POSITION_COLOR_TEXTURE_LIGHTMAP_PROGRAM = new ShaderProgram(ShaderProgramKeys.POSITION_COLOR_TEX_LIGHTMAP);
    protected static final ShaderProgram POSITION_COLOR_PROGRAM = new ShaderProgram(ShaderProgramKeys.POSITION_COLOR);
    protected static final ShaderProgram POSITION_TEXTURE_COLOR_PROGRAM = new ShaderProgram(ShaderProgramKeys.POSITION_TEX_COLOR);
    protected static final ShaderProgram PARTICLE = new ShaderProgram(ShaderProgramKeys.PARTICLE);
    protected static final ShaderProgram SOLID_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_SOLID);
    protected static final ShaderProgram CUTOUT_MIPPED_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_CUTOUT_MIPPED);
    protected static final ShaderProgram CUTOUT_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_CUTOUT);
    protected static final ShaderProgram TRANSLUCENT_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_TRANSLUCENT);
    protected static final ShaderProgram TRANSLUCENT_MOVING_BLOCK_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_TRANSLUCENT_MOVING_BLOCK);
    protected static final ShaderProgram ARMOR_CUTOUT_NO_CULL_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_ARMOR_CUTOUT_NO_CULL);
    protected static final ShaderProgram ARMOR_TRANSLUCENT = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_ARMOR_TRANSLUCENT);
    protected static final ShaderProgram ENTITY_SOLID_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_ENTITY_SOLID);
    protected static final ShaderProgram ENTITY_CUTOUT_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_ENTITY_CUTOUT);
    protected static final ShaderProgram ENTITY_CUTOUT_NONULL_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_ENTITY_CUTOUT_NO_CULL);
    protected static final ShaderProgram ENTITY_CUTOUT_NONULL_OFFSET_Z_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_ENTITY_CUTOUT_NO_CULL_Z_OFFSET);
    protected static final ShaderProgram ITEM_ENTITY_TRANSLUCENT_CULL_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL);
    protected static final ShaderProgram ENTITY_TRANSLUCENT_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_ENTITY_TRANSLUCENT);
    protected static final ShaderProgram ENTITY_TRANSLUCENT_EMISSIVE_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE);
    protected static final ShaderProgram ENTITY_SMOOTH_CUTOUT_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_ENTITY_SMOOTH_CUTOUT);
    protected static final ShaderProgram BEACON_BEAM_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_BEACON_BEAM);
    protected static final ShaderProgram ENTITY_DECAL_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_ENTITY_DECAL);
    protected static final ShaderProgram ENTITY_NO_OUTLINE_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_ENTITY_NO_OUTLINE);
    protected static final ShaderProgram ENTITY_SHADOW_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_ENTITY_SHADOW);
    protected static final ShaderProgram ENTITY_ALPHA_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_ENTITY_ALPHA);
    protected static final ShaderProgram EYES_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_EYES);
    protected static final ShaderProgram ENERGY_SWIRL_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_ENERGY_SWIRL);
    protected static final ShaderProgram LEASH_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_LEASH);
    protected static final ShaderProgram WATER_MASK_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_WATER_MASK);
    protected static final ShaderProgram OUTLINE_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_OUTLINE);
    protected static final ShaderProgram ARMOR_ENTITY_GLINT_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_ARMOR_ENTITY_GLINT);
    protected static final ShaderProgram TRANSLUCENT_GLINT_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_GLINT_TRANSLUCENT);
    protected static final ShaderProgram GLINT_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_GLINT);
    protected static final ShaderProgram ENTITY_GLINT_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_ENTITY_GLINT);
    protected static final ShaderProgram CRUMBLING_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_CRUMBLING);
    protected static final ShaderProgram TEXT_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_TEXT);
    protected static final ShaderProgram TEXT_BACKGROUND_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_TEXT_BACKGROUND);
    protected static final ShaderProgram TEXT_INTENSITY_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_TEXT_INTENSITY);
    protected static final ShaderProgram TRANSPARENT_TEXT_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_TEXT_SEE_THROUGH);
    protected static final ShaderProgram TRANSPARENT_TEXT_BACKGROUND_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_TEXT_BACKGROUND_SEE_THROUGH);
    protected static final ShaderProgram TRANSPARENT_TEXT_INTENSITY_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_TEXT_INTENSITY_SEE_THROUGH);
    protected static final ShaderProgram LIGHTNING_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_LIGHTNING);
    protected static final ShaderProgram TRIPWIRE_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_TRIPWIRE);
    protected static final ShaderProgram END_PORTAL_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_END_PORTAL);
    protected static final ShaderProgram END_GATEWAY_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_END_GATEWAY);
    protected static final ShaderProgram CLOUDS_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_CLOUDS);
    protected static final ShaderProgram LINES_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_LINES);
    protected static final ShaderProgram GUI_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_GUI);
    protected static final ShaderProgram GUI_OVERLAY_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_GUI_OVERLAY);
    protected static final ShaderProgram GUI_TEXT_HIGHLIGHT_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_GUI_TEXT_HIGHLIGHT);
    protected static final ShaderProgram GUI_GHOST_RECIPE_OVERLAY_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_GUI_GHOST_RECIPE_OVERLAY);
    protected static final ShaderProgram BREEZE_WIND_PROGRAM = new ShaderProgram(ShaderProgramKeys.RENDERTYPE_BREEZE_WIND);
    protected static final Texture MIPMAP_BLOCK_ATLAS_TEXTURE = new Texture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, TriState.FALSE, true);
    protected static final Texture BLOCK_ATLAS_TEXTURE = new Texture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, TriState.FALSE, false);
    protected static final TextureBase NO_TEXTURE = new TextureBase();
    protected static final Texturing DEFAULT_TEXTURING = new Texturing("default_texturing", () -> {}, () -> {});
    protected static final Texturing GLINT_TEXTURING = new Texturing("glint_texturing", () -> RenderPhase.setupGlintTexturing(8.0f), () -> RenderSystem.resetTextureMatrix());
    protected static final Texturing ENTITY_GLINT_TEXTURING = new Texturing("entity_glint_texturing", () -> RenderPhase.setupGlintTexturing(0.16f), () -> RenderSystem.resetTextureMatrix());
    protected static final Lightmap ENABLE_LIGHTMAP = new Lightmap(true);
    protected static final Lightmap DISABLE_LIGHTMAP = new Lightmap(false);
    protected static final Overlay ENABLE_OVERLAY_COLOR = new Overlay(true);
    protected static final Overlay DISABLE_OVERLAY_COLOR = new Overlay(false);
    protected static final Cull ENABLE_CULLING = new Cull(true);
    protected static final Cull DISABLE_CULLING = new Cull(false);
    protected static final DepthTest ALWAYS_DEPTH_TEST = new DepthTest("always", 519);
    protected static final DepthTest EQUAL_DEPTH_TEST = new DepthTest("==", 514);
    protected static final DepthTest LEQUAL_DEPTH_TEST = new DepthTest("<=", 515);
    protected static final DepthTest BIGGER_DEPTH_TEST = new DepthTest(">", 516);
    protected static final WriteMaskState ALL_MASK = new WriteMaskState(true, true);
    protected static final WriteMaskState COLOR_MASK = new WriteMaskState(true, false);
    protected static final WriteMaskState DEPTH_MASK = new WriteMaskState(false, true);
    protected static final Layering NO_LAYERING = new Layering("no_layering", () -> {}, () -> {});
    protected static final Layering POLYGON_OFFSET_LAYERING = new Layering("polygon_offset_layering", () -> {
        RenderSystem.polygonOffset(-1.0f, -10.0f);
        RenderSystem.enablePolygonOffset();
    }, () -> {
        RenderSystem.polygonOffset(0.0f, 0.0f);
        RenderSystem.disablePolygonOffset();
    });
    protected static final Layering VIEW_OFFSET_Z_LAYERING = new Layering("view_offset_z_layering", () -> {
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
        RenderSystem.getProjectionType().apply(matrix4fStack, 1.0f);
    }, () -> {
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.popMatrix();
    });
    protected static final Layering VIEW_OFFSET_Z_LAYERING_FORWARD = new Layering("view_offset_z_layering_forward", () -> {
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
        RenderSystem.getProjectionType().apply(matrix4fStack, -1.0f);
    }, () -> {
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.popMatrix();
    });
    protected static final Layering WORLD_BORDER_LAYERING = new Layering("world_border_layering", () -> {
        RenderSystem.polygonOffset(-3.0f, -3.0f);
        RenderSystem.enablePolygonOffset();
    }, () -> {
        RenderSystem.polygonOffset(0.0f, 0.0f);
        RenderSystem.disablePolygonOffset();
    });
    protected static final Target MAIN_TARGET = new Target("main_target", () -> MinecraftClient.getInstance().getFramebuffer().beginWrite(false), () -> {});
    protected static final Target OUTLINE_TARGET = new Target("outline_target", () -> {
        Framebuffer lv = MinecraftClient.getInstance().worldRenderer.getEntityOutlinesFramebuffer();
        if (lv != null) {
            lv.beginWrite(false);
        } else {
            MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
        }
    }, () -> MinecraftClient.getInstance().getFramebuffer().beginWrite(false));
    protected static final Target TRANSLUCENT_TARGET = new Target("translucent_target", () -> {
        Framebuffer lv = MinecraftClient.getInstance().worldRenderer.getTranslucentFramebuffer();
        if (lv != null) {
            lv.beginWrite(false);
        } else {
            MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
        }
    }, () -> MinecraftClient.getInstance().getFramebuffer().beginWrite(false));
    protected static final Target PARTICLES_TARGET = new Target("particles_target", () -> {
        Framebuffer lv = MinecraftClient.getInstance().worldRenderer.getParticlesFramebuffer();
        if (lv != null) {
            lv.beginWrite(false);
        } else {
            MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
        }
    }, () -> MinecraftClient.getInstance().getFramebuffer().beginWrite(false));
    protected static final Target WEATHER_TARGET = new Target("weather_target", () -> {
        Framebuffer lv = MinecraftClient.getInstance().worldRenderer.getWeatherFramebuffer();
        if (lv != null) {
            lv.beginWrite(false);
        } else {
            MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
        }
    }, () -> MinecraftClient.getInstance().getFramebuffer().beginWrite(false));
    protected static final Target CLOUDS_TARGET = new Target("clouds_target", () -> {
        Framebuffer lv = MinecraftClient.getInstance().worldRenderer.getCloudsFramebuffer();
        if (lv != null) {
            lv.beginWrite(false);
        } else {
            MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
        }
    }, () -> MinecraftClient.getInstance().getFramebuffer().beginWrite(false));
    protected static final Target ITEM_ENTITY_TARGET = new Target("item_entity_target", () -> {
        Framebuffer lv = MinecraftClient.getInstance().worldRenderer.getEntityFramebuffer();
        if (lv != null) {
            lv.beginWrite(false);
        } else {
            MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
        }
    }, () -> MinecraftClient.getInstance().getFramebuffer().beginWrite(false));
    protected static final LineWidth FULL_LINE_WIDTH = new LineWidth(OptionalDouble.of(1.0));
    protected static final ColorLogic NO_COLOR_LOGIC = new ColorLogic("no_color_logic", () -> RenderSystem.disableColorLogicOp(), () -> {});
    protected static final ColorLogic OR_REVERSE = new ColorLogic("or_reverse", () -> {
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
    }, () -> RenderSystem.disableColorLogicOp());

    public RenderPhase(String name, Runnable beginAction, Runnable endAction) {
        this.name = name;
        this.beginAction = beginAction;
        this.endAction = endAction;
    }

    public void startDrawing() {
        this.beginAction.run();
    }

    public void endDrawing() {
        this.endAction.run();
    }

    public String toString() {
        return this.name;
    }

    private static void setupGlintTexturing(float scale) {
        long l = (long)((double)Util.getMeasuringTimeMs() * MinecraftClient.getInstance().options.getGlintSpeed().getValue() * 8.0);
        float g = (float)(l % 110000L) / 110000.0f;
        float h = (float)(l % 30000L) / 30000.0f;
        Matrix4f matrix4f = new Matrix4f().translation(-g, h, 0.0f);
        matrix4f.rotateZ(0.17453292f).scale(scale);
        RenderSystem.setTextureMatrix(matrix4f);
    }

    @Environment(value=EnvType.CLIENT)
    protected static class Transparency
    extends RenderPhase {
        public Transparency(String string, Runnable runnable, Runnable runnable2) {
            super(string, runnable, runnable2);
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static class ShaderProgram
    extends RenderPhase {
        private final Optional<ShaderProgramKey> supplier;

        public ShaderProgram(ShaderProgramKey arg) {
            super("shader", () -> RenderSystem.setShader(arg), () -> {});
            this.supplier = Optional.of(arg);
        }

        public ShaderProgram() {
            super("shader", RenderSystem::clearShader, () -> {});
            this.supplier = Optional.empty();
        }

        @Override
        public String toString() {
            return this.name + "[" + String.valueOf(this.supplier) + "]";
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static class Texture
    extends TextureBase {
        private final Optional<Identifier> id;
        private final TriState blur;
        private final boolean mipmap;

        public Texture(Identifier id, TriState bilinear, boolean mipmap) {
            super(() -> {
                TextureManager lv = MinecraftClient.getInstance().getTextureManager();
                AbstractTexture lv2 = lv.getTexture(id);
                lv2.setFilter(bilinear, mipmap);
                RenderSystem.setShaderTexture(0, lv2.getGlId());
            }, () -> {});
            this.id = Optional.of(id);
            this.blur = bilinear;
            this.mipmap = mipmap;
        }

        @Override
        public String toString() {
            return this.name + "[" + String.valueOf(this.id) + "(blur=" + String.valueOf((Object)this.blur) + ", mipmap=" + this.mipmap + ")]";
        }

        @Override
        protected Optional<Identifier> getId() {
            return this.id;
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static class TextureBase
    extends RenderPhase {
        public TextureBase(Runnable apply, Runnable unapply) {
            super("texture", apply, unapply);
        }

        TextureBase() {
            super("texture", () -> {}, () -> {});
        }

        protected Optional<Identifier> getId() {
            return Optional.empty();
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static class Texturing
    extends RenderPhase {
        public Texturing(String string, Runnable runnable, Runnable runnable2) {
            super(string, runnable, runnable2);
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static class Lightmap
    extends Toggleable {
        public Lightmap(boolean lightmap) {
            super("lightmap", () -> {
                if (lightmap) {
                    MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager().enable();
                }
            }, () -> {
                if (lightmap) {
                    MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager().disable();
                }
            }, lightmap);
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static class Overlay
    extends Toggleable {
        public Overlay(boolean overlayColor) {
            super("overlay", () -> {
                if (overlayColor) {
                    MinecraftClient.getInstance().gameRenderer.getOverlayTexture().setupOverlayColor();
                }
            }, () -> {
                if (overlayColor) {
                    MinecraftClient.getInstance().gameRenderer.getOverlayTexture().teardownOverlayColor();
                }
            }, overlayColor);
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static class Cull
    extends Toggleable {
        public Cull(boolean culling) {
            super("cull", () -> {
                if (!culling) {
                    RenderSystem.disableCull();
                }
            }, () -> {
                if (!culling) {
                    RenderSystem.enableCull();
                }
            }, culling);
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static class DepthTest
    extends RenderPhase {
        private final String depthFunctionName;

        public DepthTest(String depthFunctionName, int depthFunction) {
            super("depth_test", () -> {
                if (depthFunction != 519) {
                    RenderSystem.enableDepthTest();
                    RenderSystem.depthFunc(depthFunction);
                }
            }, () -> {
                if (depthFunction != 519) {
                    RenderSystem.disableDepthTest();
                    RenderSystem.depthFunc(515);
                }
            });
            this.depthFunctionName = depthFunctionName;
        }

        @Override
        public String toString() {
            return this.name + "[" + this.depthFunctionName + "]";
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static class WriteMaskState
    extends RenderPhase {
        private final boolean color;
        private final boolean depth;

        public WriteMaskState(boolean color, boolean depth) {
            super("write_mask_state", () -> {
                if (!depth) {
                    RenderSystem.depthMask(depth);
                }
                if (!color) {
                    RenderSystem.colorMask(color, color, color, color);
                }
            }, () -> {
                if (!depth) {
                    RenderSystem.depthMask(true);
                }
                if (!color) {
                    RenderSystem.colorMask(true, true, true, true);
                }
            });
            this.color = color;
            this.depth = depth;
        }

        @Override
        public String toString() {
            return this.name + "[writeColor=" + this.color + ", writeDepth=" + this.depth + "]";
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static class Layering
    extends RenderPhase {
        public Layering(String string, Runnable runnable, Runnable runnable2) {
            super(string, runnable, runnable2);
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static class Target
    extends RenderPhase {
        public Target(String string, Runnable runnable, Runnable runnable2) {
            super(string, runnable, runnable2);
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static class LineWidth
    extends RenderPhase {
        private final OptionalDouble width;

        public LineWidth(OptionalDouble width) {
            super("line_width", () -> {
                if (!Objects.equals(width, OptionalDouble.of(1.0))) {
                    if (width.isPresent()) {
                        RenderSystem.lineWidth((float)width.getAsDouble());
                    } else {
                        RenderSystem.lineWidth(Math.max(2.5f, (float)MinecraftClient.getInstance().getWindow().getFramebufferWidth() / 1920.0f * 2.5f));
                    }
                }
            }, () -> {
                if (!Objects.equals(width, OptionalDouble.of(1.0))) {
                    RenderSystem.lineWidth(1.0f);
                }
            });
            this.width = width;
        }

        @Override
        public String toString() {
            return this.name + "[" + String.valueOf(this.width.isPresent() ? Double.valueOf(this.width.getAsDouble()) : "window_scale") + "]";
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static class ColorLogic
    extends RenderPhase {
        public ColorLogic(String string, Runnable runnable, Runnable runnable2) {
            super(string, runnable, runnable2);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Toggleable
    extends RenderPhase {
        private final boolean enabled;

        public Toggleable(String name, Runnable apply, Runnable unapply, boolean enabled) {
            super(name, apply, unapply);
            this.enabled = enabled;
        }

        @Override
        public String toString() {
            return this.name + "[" + this.enabled + "]";
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static final class OffsetTexturing
    extends Texturing {
        public OffsetTexturing(float x, float y) {
            super("offset_texturing", () -> RenderSystem.setTextureMatrix(new Matrix4f().translation(x, y, 0.0f)), () -> RenderSystem.resetTextureMatrix());
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static class Textures
    extends TextureBase {
        private final Optional<Identifier> id;

        Textures(List<TextureEntry> textures) {
            super(() -> {
                for (int i = 0; i < textures.size(); ++i) {
                    TextureEntry lv = (TextureEntry)textures.get(i);
                    TextureManager lv2 = MinecraftClient.getInstance().getTextureManager();
                    AbstractTexture lv3 = lv2.getTexture(lv.id);
                    lv3.setFilter(lv.blur, lv.mipmap);
                    RenderSystem.setShaderTexture(i, lv3.getGlId());
                }
            }, () -> {});
            this.id = textures.isEmpty() ? Optional.empty() : Optional.of(textures.getFirst().id);
        }

        @Override
        protected Optional<Identifier> getId() {
            return this.id;
        }

        public static Builder create() {
            return new Builder();
        }

        @Environment(value=EnvType.CLIENT)
        record TextureEntry(Identifier id, boolean blur, boolean mipmap) {
        }

        @Environment(value=EnvType.CLIENT)
        public static final class Builder {
            private final ImmutableList.Builder<TextureEntry> textures = new ImmutableList.Builder();

            public Builder add(Identifier id, boolean blur, boolean mipmap) {
                this.textures.add((Object)new TextureEntry(id, blur, mipmap));
                return this;
            }

            public Textures build() {
                return new Textures((List<TextureEntry>)((Object)this.textures.build()));
            }
        }
    }
}

