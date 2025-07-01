/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Urls;

@Environment(value=EnvType.CLIENT)
public class AccessibilityOptionsScreen
extends GameOptionsScreen {
    public static final Text TITLE_TEXT = Text.translatable("options.accessibility.title");

    private static SimpleOption<?>[] getOptions(GameOptions gameOptions) {
        return new SimpleOption[]{gameOptions.getNarrator(), gameOptions.getShowSubtitles(), gameOptions.getHighContrast(), gameOptions.getAutoJump(), gameOptions.getMenuBackgroundBlurriness(), gameOptions.getTextBackgroundOpacity(), gameOptions.getBackgroundForChatOnly(), gameOptions.getChatOpacity(), gameOptions.getChatLineSpacing(), gameOptions.getChatDelay(), gameOptions.getNotificationDisplayTime(), gameOptions.getBobView(), gameOptions.getSneakToggled(), gameOptions.getSprintToggled(), gameOptions.getDistortionEffectScale(), gameOptions.getFovEffectScale(), gameOptions.getDarknessEffectScale(), gameOptions.getDamageTiltStrength(), gameOptions.getGlintSpeed(), gameOptions.getGlintStrength(), gameOptions.getHideLightningFlashes(), gameOptions.getMonochromeLogo(), gameOptions.getPanoramaSpeed(), gameOptions.getHideSplashTexts(), gameOptions.getNarratorHotkey(), gameOptions.getRotateWithMinecart(), gameOptions.getHighContrastBlockOutline()};
    }

    public AccessibilityOptionsScreen(Screen parent, GameOptions gameOptions) {
        super(parent, gameOptions, TITLE_TEXT);
    }

    @Override
    protected void init() {
        ClickableWidget lv2;
        super.init();
        ClickableWidget lv = this.body.getWidgetFor(this.gameOptions.getHighContrast());
        if (lv != null && !this.client.getResourcePackManager().getIds().contains("high_contrast")) {
            lv.active = false;
            lv.setTooltip(Tooltip.of(Text.translatable("options.accessibility.high_contrast.error.tooltip")));
        }
        if ((lv2 = this.body.getWidgetFor(this.gameOptions.getRotateWithMinecart())) != null) {
            lv2.active = this.isMinecartImprovementsExperimentEnabled();
        }
    }

    @Override
    protected void addOptions() {
        this.body.addAll(AccessibilityOptionsScreen.getOptions(this.gameOptions));
    }

    @Override
    protected void initFooter() {
        DirectionalLayoutWidget lv = this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8));
        lv.add(ButtonWidget.builder(Text.translatable("options.accessibility.link"), ConfirmLinkScreen.opening((Screen)this, Urls.JAVA_ACCESSIBILITY)).build());
        lv.add(ButtonWidget.builder(ScreenTexts.DONE, button -> this.client.setScreen(this.parent)).build());
    }

    private boolean isMinecartImprovementsExperimentEnabled() {
        return this.client.world != null && this.client.world.getEnabledFeatures().contains(FeatureFlags.MINECART_IMPROVEMENTS);
    }
}

