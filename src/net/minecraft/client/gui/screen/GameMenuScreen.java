/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import java.net.URI;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.OpenToLanScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ServerLinksScreen;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.ServerLinks;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Urls;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class GameMenuScreen
extends Screen {
    private static final Identifier DRAFT_REPORT_ICON_TEXTURE = Identifier.ofVanilla("icon/draft_report");
    private static final int GRID_COLUMNS = 2;
    private static final int BUTTONS_TOP_MARGIN = 50;
    private static final int GRID_MARGIN = 4;
    private static final int WIDE_BUTTON_WIDTH = 204;
    private static final int NORMAL_BUTTON_WIDTH = 98;
    private static final Text RETURN_TO_GAME_TEXT = Text.translatable("menu.returnToGame");
    private static final Text ADVANCEMENTS_TEXT = Text.translatable("gui.advancements");
    private static final Text STATS_TEXT = Text.translatable("gui.stats");
    private static final Text SEND_FEEDBACK_TEXT = Text.translatable("menu.sendFeedback");
    private static final Text REPORT_BUGS_TEXT = Text.translatable("menu.reportBugs");
    private static final Text FEEDBACK_TEXT = Text.translatable("menu.feedback");
    private static final Text SERVER_LINKS_TEXT = Text.translatable("menu.server_links");
    private static final Text OPTIONS_TEXT = Text.translatable("menu.options");
    private static final Text SHARE_TO_LAN_TEXT = Text.translatable("menu.shareToLan");
    private static final Text PLAYER_REPORTING_TEXT = Text.translatable("menu.playerReporting");
    private static final Text RETURN_TO_MENU_TEXT = Text.translatable("menu.returnToMenu");
    private static final Text SAVING_LEVEL_TEXT = Text.translatable("menu.savingLevel");
    private static final Text GAME_TEXT = Text.translatable("menu.game");
    private static final Text PAUSED_TEXT = Text.translatable("menu.paused");
    private final boolean showMenu;
    @Nullable
    private ButtonWidget exitButton;

    public GameMenuScreen(boolean showMenu) {
        super(showMenu ? GAME_TEXT : PAUSED_TEXT);
        this.showMenu = showMenu;
    }

    public boolean shouldShowMenu() {
        return this.showMenu;
    }

    @Override
    protected void init() {
        if (this.showMenu) {
            this.initWidgets();
        }
        this.addDrawableChild(new TextWidget(0, this.showMenu ? 40 : 10, this.width, this.textRenderer.fontHeight, this.title, this.textRenderer));
    }

    private void initWidgets() {
        GridWidget lv = new GridWidget();
        lv.getMainPositioner().margin(4, 4, 4, 0);
        GridWidget.Adder lv2 = lv.createAdder(2);
        lv2.add(ButtonWidget.builder(RETURN_TO_GAME_TEXT, button -> {
            this.client.setScreen(null);
            this.client.mouse.lockCursor();
        }).width(204).build(), 2, lv.copyPositioner().marginTop(50));
        lv2.add(this.createButton(ADVANCEMENTS_TEXT, () -> new AdvancementsScreen(this.client.player.networkHandler.getAdvancementHandler(), this)));
        lv2.add(this.createButton(STATS_TEXT, () -> new StatsScreen(this, this.client.player.getStatHandler())));
        ServerLinks lv3 = this.client.player.networkHandler.getServerLinks();
        if (lv3.isEmpty()) {
            GameMenuScreen.addFeedbackAndBugsButtons(this, lv2);
        } else {
            lv2.add(this.createButton(FEEDBACK_TEXT, () -> new FeedbackScreen(this)));
            lv2.add(this.createButton(SERVER_LINKS_TEXT, () -> new ServerLinksScreen(this, lv3)));
        }
        lv2.add(this.createButton(OPTIONS_TEXT, () -> new OptionsScreen(this, this.client.options)));
        if (this.client.isIntegratedServerRunning() && !this.client.getServer().isRemote()) {
            lv2.add(this.createButton(SHARE_TO_LAN_TEXT, () -> new OpenToLanScreen(this)));
        } else {
            lv2.add(this.createButton(PLAYER_REPORTING_TEXT, () -> new SocialInteractionsScreen(this)));
        }
        Text lv4 = this.client.isInSingleplayer() ? RETURN_TO_MENU_TEXT : ScreenTexts.DISCONNECT;
        this.exitButton = lv2.add(ButtonWidget.builder(lv4, button -> {
            button.active = false;
            this.client.getAbuseReportContext().tryShowDraftScreen(this.client, this, this::disconnect, true);
        }).width(204).build(), 2);
        lv.refreshPositions();
        SimplePositioningWidget.setPos(lv, 0, 0, this.width, this.height, 0.5f, 0.25f);
        lv.forEachChild(this::addDrawableChild);
    }

    static void addFeedbackAndBugsButtons(Screen parentScreen, GridWidget.Adder gridAdder) {
        gridAdder.add(GameMenuScreen.createUrlButton(parentScreen, SEND_FEEDBACK_TEXT, SharedConstants.getGameVersion().isStable() ? Urls.JAVA_FEEDBACK : Urls.SNAPSHOT_FEEDBACK));
        gridAdder.add(GameMenuScreen.createUrlButton((Screen)parentScreen, (Text)GameMenuScreen.REPORT_BUGS_TEXT, (URI)Urls.SNAPSHOT_BUGS)).active = !SharedConstants.getGameVersion().getSaveVersion().isNotMainSeries();
    }

    private void disconnect() {
        boolean bl = this.client.isInSingleplayer();
        ServerInfo lv = this.client.getCurrentServerEntry();
        this.client.world.disconnect();
        if (bl) {
            this.client.disconnect(new MessageScreen(SAVING_LEVEL_TEXT));
        } else {
            this.client.disconnect();
        }
        TitleScreen lv2 = new TitleScreen();
        if (bl) {
            this.client.setScreen(lv2);
        } else if (lv != null && lv.isRealm()) {
            this.client.setScreen(new RealmsMainScreen(lv2));
        } else {
            this.client.setScreen(new MultiplayerScreen(lv2));
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        if (this.showMenu && this.client != null && this.client.getAbuseReportContext().hasDraft() && this.exitButton != null) {
            context.drawGuiTexture(RenderLayer::getGuiTextured, DRAFT_REPORT_ICON_TEXTURE, this.exitButton.getX() + this.exitButton.getWidth() - 17, this.exitButton.getY() + 3, 15, 15);
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.showMenu) {
            super.renderBackground(context, mouseX, mouseY, delta);
        }
    }

    private ButtonWidget createButton(Text text, Supplier<Screen> screenSupplier) {
        return ButtonWidget.builder(text, button -> this.client.setScreen((Screen)screenSupplier.get())).width(98).build();
    }

    private static ButtonWidget createUrlButton(Screen parent, Text text, URI uri) {
        return ButtonWidget.builder(text, ConfirmLinkScreen.opening(parent, uri)).width(98).build();
    }

    @Environment(value=EnvType.CLIENT)
    static class FeedbackScreen
    extends Screen {
        private static final Text TITLE = Text.translatable("menu.feedback.title");
        public final Screen parent;
        private final ThreePartsLayoutWidget layoutWidget = new ThreePartsLayoutWidget(this);

        protected FeedbackScreen(Screen parent) {
            super(TITLE);
            this.parent = parent;
        }

        @Override
        protected void init() {
            this.layoutWidget.addHeader(TITLE, this.textRenderer);
            GridWidget lv = this.layoutWidget.addBody(new GridWidget());
            lv.getMainPositioner().margin(4, 4, 4, 0);
            GridWidget.Adder lv2 = lv.createAdder(2);
            GameMenuScreen.addFeedbackAndBugsButtons(this, lv2);
            this.layoutWidget.addFooter(ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).width(200).build());
            this.layoutWidget.forEachChild(this::addDrawableChild);
            this.refreshWidgetPositions();
        }

        @Override
        protected void refreshWidgetPositions() {
            this.layoutWidget.refreshPositions();
        }

        @Override
        public void close() {
            this.client.setScreen(this.parent);
        }
    }
}

