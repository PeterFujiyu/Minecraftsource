/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client;

import com.mojang.logging.LogUtils;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.navigation.GuiNavigationType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.Scroller;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Smoother;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWDropCallback;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class Mouse {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final MinecraftClient client;
    private boolean leftButtonClicked;
    private boolean middleButtonClicked;
    private boolean rightButtonClicked;
    private double x;
    private double y;
    private int controlLeftClicks;
    private int activeButton = -1;
    private boolean hasResolutionChanged = true;
    private int field_1796;
    private double glfwTime;
    private final Smoother cursorXSmoother = new Smoother();
    private final Smoother cursorYSmoother = new Smoother();
    private double cursorDeltaX;
    private double cursorDeltaY;
    private final Scroller scroller;
    private double lastTickTime = Double.MIN_VALUE;
    private boolean cursorLocked;

    public Mouse(MinecraftClient client) {
        this.client = client;
        this.scroller = new Scroller();
    }

    private void onMouseButton(long window, int button, int action, int mods) {
        int m;
        boolean bl;
        block32: {
            if (window != this.client.getWindow().getHandle()) {
                return;
            }
            this.client.getInactivityFpsLimiter().onInput();
            if (this.client.currentScreen != null) {
                this.client.setNavigationType(GuiNavigationType.MOUSE);
            }
            boolean bl2 = bl = action == 1;
            if (MinecraftClient.IS_SYSTEM_MAC && button == 0) {
                if (bl) {
                    if ((mods & 2) == 2) {
                        button = 1;
                        ++this.controlLeftClicks;
                    }
                } else if (this.controlLeftClicks > 0) {
                    button = 1;
                    --this.controlLeftClicks;
                }
            }
            m = button;
            if (bl) {
                if (this.client.options.getTouchscreen().getValue().booleanValue() && this.field_1796++ > 0) {
                    return;
                }
                this.activeButton = m;
                this.glfwTime = GlfwUtil.getTime();
            } else if (this.activeButton != -1) {
                if (this.client.options.getTouchscreen().getValue().booleanValue() && --this.field_1796 > 0) {
                    return;
                }
                this.activeButton = -1;
            }
            if (this.client.getOverlay() == null) {
                if (this.client.currentScreen == null) {
                    if (!this.cursorLocked && bl) {
                        this.lockCursor();
                    }
                } else {
                    double d = this.x * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth();
                    double e = this.y * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight();
                    Screen lv = this.client.currentScreen;
                    if (bl) {
                        lv.applyMousePressScrollNarratorDelay();
                        try {
                            if (lv.mouseClicked(d, e, m)) {
                                return;
                            }
                            break block32;
                        } catch (Throwable throwable) {
                            CrashReport lv2 = CrashReport.create(throwable, "mouseClicked event handler");
                            lv.addCrashReportSection(lv2);
                            CrashReportSection lv3 = lv2.addElement("Mouse");
                            lv3.add("Scaled X", d);
                            lv3.add("Scaled Y", e);
                            lv3.add("Button", m);
                            throw new CrashException(lv2);
                        }
                    }
                    try {
                        if (lv.mouseReleased(d, e, m)) {
                            return;
                        }
                    } catch (Throwable throwable) {
                        CrashReport lv2 = CrashReport.create(throwable, "mouseReleased event handler");
                        lv.addCrashReportSection(lv2);
                        CrashReportSection lv3 = lv2.addElement("Mouse");
                        lv3.add("Scaled X", d);
                        lv3.add("Scaled Y", e);
                        lv3.add("Button", m);
                        throw new CrashException(lv2);
                    }
                }
            }
        }
        if (this.client.currentScreen == null && this.client.getOverlay() == null) {
            if (m == 0) {
                this.leftButtonClicked = bl;
            } else if (m == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
                this.middleButtonClicked = bl;
            } else if (m == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                this.rightButtonClicked = bl;
            }
            KeyBinding.setKeyPressed(InputUtil.Type.MOUSE.createFromCode(m), bl);
            if (bl) {
                if (this.client.player.isSpectator() && m == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
                    this.client.inGameHud.getSpectatorHud().useSelectedCommand();
                } else {
                    KeyBinding.onKeyPressed(InputUtil.Type.MOUSE.createFromCode(m));
                }
            }
        }
    }

    private void onMouseScroll(long window, double horizontal, double vertical) {
        if (window == MinecraftClient.getInstance().getWindow().getHandle()) {
            this.client.getInactivityFpsLimiter().onInput();
            boolean bl = this.client.options.getDiscreteMouseScroll().getValue();
            double f = this.client.options.getMouseWheelSensitivity().getValue();
            double g = (bl ? Math.signum(horizontal) : horizontal) * f;
            double h = (bl ? Math.signum(vertical) : vertical) * f;
            if (this.client.getOverlay() == null) {
                if (this.client.currentScreen != null) {
                    double i = this.x * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth();
                    double j = this.y * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight();
                    this.client.currentScreen.mouseScrolled(i, j, g, h);
                    this.client.currentScreen.applyMousePressScrollNarratorDelay();
                } else if (this.client.player != null) {
                    int k;
                    Vector2i vector2i = this.scroller.update(g, h);
                    if (vector2i.x == 0 && vector2i.y == 0) {
                        return;
                    }
                    int n = k = vector2i.y == 0 ? -vector2i.x : vector2i.y;
                    if (this.client.player.isSpectator()) {
                        if (this.client.inGameHud.getSpectatorHud().isOpen()) {
                            this.client.inGameHud.getSpectatorHud().cycleSlot(-k);
                        } else {
                            float m = MathHelper.clamp(this.client.player.getAbilities().getFlySpeed() + (float)vector2i.y * 0.005f, 0.0f, 0.2f);
                            this.client.player.getAbilities().setFlySpeed(m);
                        }
                    } else {
                        PlayerInventory lv = this.client.player.getInventory();
                        lv.setSelectedSlot(Scroller.scrollCycling(k, lv.selectedSlot, PlayerInventory.getHotbarSize()));
                    }
                }
            }
        }
    }

    private void onFilesDropped(long window, List<Path> paths, int invalidFilesCount) {
        this.client.getInactivityFpsLimiter().onInput();
        if (this.client.currentScreen != null) {
            this.client.currentScreen.onFilesDropped(paths);
        }
        if (invalidFilesCount > 0) {
            SystemToast.addFileDropFailure(this.client, invalidFilesCount);
        }
    }

    public void setup(long window2) {
        InputUtil.setMouseCallbacks(window2, (window, x, y) -> this.client.execute(() -> this.onCursorPos(window, x, y)), (window, button, action, modifiers) -> this.client.execute(() -> this.onMouseButton(window, button, action, modifiers)), (window, offsetX, offsetY) -> this.client.execute(() -> this.onMouseScroll(window, offsetX, offsetY)), (window, count, names) -> {
            int k;
            ArrayList<Path> list = new ArrayList<Path>(count);
            int j = 0;
            for (k = 0; k < count; ++k) {
                String string = GLFWDropCallback.getName(names, k);
                try {
                    list.add(Paths.get(string, new String[0]));
                    continue;
                } catch (InvalidPathException invalidPathException) {
                    ++j;
                    LOGGER.error("Failed to parse path '{}'", (Object)string, (Object)invalidPathException);
                }
            }
            if (!list.isEmpty()) {
                k = j;
                this.client.execute(() -> this.onFilesDropped(window, list, k));
            }
        });
    }

    private void onCursorPos(long window, double x, double y) {
        if (window != MinecraftClient.getInstance().getWindow().getHandle()) {
            return;
        }
        if (this.hasResolutionChanged) {
            this.x = x;
            this.y = y;
            this.hasResolutionChanged = false;
            return;
        }
        if (this.client.isWindowFocused()) {
            this.cursorDeltaX += x - this.x;
            this.cursorDeltaY += y - this.y;
        }
        this.x = x;
        this.y = y;
    }

    public void tick() {
        double d = GlfwUtil.getTime();
        double e = d - this.lastTickTime;
        this.lastTickTime = d;
        if (this.client.isWindowFocused()) {
            boolean bl;
            Screen lv = this.client.currentScreen;
            boolean bl2 = bl = this.cursorDeltaX != 0.0 || this.cursorDeltaY != 0.0;
            if (bl) {
                this.client.getInactivityFpsLimiter().onInput();
            }
            if (lv != null && this.client.getOverlay() == null && bl) {
                double f = this.x * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth();
                double g = this.y * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight();
                try {
                    lv.mouseMoved(f, g);
                } catch (Throwable throwable) {
                    CrashReport lv2 = CrashReport.create(throwable, "mouseMoved event handler");
                    lv.addCrashReportSection(lv2);
                    CrashReportSection lv3 = lv2.addElement("Mouse");
                    lv3.add("Scaled X", f);
                    lv3.add("Scaled Y", g);
                    throw new CrashException(lv2);
                }
                if (this.activeButton != -1 && this.glfwTime > 0.0) {
                    double h = this.cursorDeltaX * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth();
                    double i = this.cursorDeltaY * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight();
                    try {
                        lv.mouseDragged(f, g, this.activeButton, h, i);
                    } catch (Throwable throwable2) {
                        CrashReport lv4 = CrashReport.create(throwable2, "mouseDragged event handler");
                        lv.addCrashReportSection(lv4);
                        CrashReportSection lv5 = lv4.addElement("Mouse");
                        lv5.add("Scaled X", f);
                        lv5.add("Scaled Y", g);
                        throw new CrashException(lv4);
                    }
                }
                lv.applyMouseMoveNarratorDelay();
            }
            if (this.isCursorLocked() && this.client.player != null) {
                this.updateMouse(e);
            }
        }
        this.cursorDeltaX = 0.0;
        this.cursorDeltaY = 0.0;
    }

    private void updateMouse(double timeDelta) {
        double k;
        double j;
        double e = this.client.options.getMouseSensitivity().getValue() * (double)0.6f + (double)0.2f;
        double f = e * e * e;
        double g = f * 8.0;
        if (this.client.options.smoothCameraEnabled) {
            double h = this.cursorXSmoother.smooth(this.cursorDeltaX * g, timeDelta * g);
            double i = this.cursorYSmoother.smooth(this.cursorDeltaY * g, timeDelta * g);
            j = h;
            k = i;
        } else if (this.client.options.getPerspective().isFirstPerson() && this.client.player.isUsingSpyglass()) {
            this.cursorXSmoother.clear();
            this.cursorYSmoother.clear();
            j = this.cursorDeltaX * f;
            k = this.cursorDeltaY * f;
        } else {
            this.cursorXSmoother.clear();
            this.cursorYSmoother.clear();
            j = this.cursorDeltaX * g;
            k = this.cursorDeltaY * g;
        }
        int l = 1;
        if (this.client.options.getInvertYMouse().getValue().booleanValue()) {
            l = -1;
        }
        this.client.getTutorialManager().onUpdateMouse(j, k);
        if (this.client.player != null) {
            this.client.player.changeLookDirection(j, k * (double)l);
        }
    }

    public boolean wasLeftButtonClicked() {
        return this.leftButtonClicked;
    }

    public boolean wasMiddleButtonClicked() {
        return this.middleButtonClicked;
    }

    public boolean wasRightButtonClicked() {
        return this.rightButtonClicked;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public void onResolutionChanged() {
        this.hasResolutionChanged = true;
    }

    public boolean isCursorLocked() {
        return this.cursorLocked;
    }

    public void lockCursor() {
        if (!this.client.isWindowFocused()) {
            return;
        }
        if (this.cursorLocked) {
            return;
        }
        if (!MinecraftClient.IS_SYSTEM_MAC) {
            KeyBinding.updatePressedStates();
        }
        this.cursorLocked = true;
        this.x = this.client.getWindow().getWidth() / 2;
        this.y = this.client.getWindow().getHeight() / 2;
        InputUtil.setCursorParameters(this.client.getWindow().getHandle(), InputUtil.GLFW_CURSOR_DISABLED, this.x, this.y);
        this.client.setScreen(null);
        this.client.attackCooldown = 10000;
        this.hasResolutionChanged = true;
    }

    public void unlockCursor() {
        if (!this.cursorLocked) {
            return;
        }
        this.cursorLocked = false;
        this.x = this.client.getWindow().getWidth() / 2;
        this.y = this.client.getWindow().getHeight() / 2;
        InputUtil.setCursorParameters(this.client.getWindow().getHandle(), InputUtil.GLFW_CURSOR_NORMAL, this.x, this.y);
    }

    public void setResolutionChanged() {
        this.hasResolutionChanged = true;
    }
}

