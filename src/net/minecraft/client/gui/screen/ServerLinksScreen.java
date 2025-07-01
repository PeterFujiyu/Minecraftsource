/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.ServerLinks;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ServerLinksScreen
extends Screen {
    private static final int LIST_WIDTH = 310;
    private static final int ENTRY_HEIGHT = 25;
    private static final Text TITLE = Text.translatable("menu.server_links.title");
    private final Screen parent;
    @Nullable
    private LinksListWidget list;
    final ThreePartsLayoutWidget layoutWidget = new ThreePartsLayoutWidget(this);
    final ServerLinks serverLinks;

    public ServerLinksScreen(Screen parent, ServerLinks serverLinks) {
        super(TITLE);
        this.parent = parent;
        this.serverLinks = serverLinks;
    }

    @Override
    protected void init() {
        this.layoutWidget.addHeader(this.title, this.textRenderer);
        this.list = this.layoutWidget.addBody(new LinksListWidget(this.client, this.width, this));
        this.layoutWidget.addFooter(ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).width(200).build());
        this.layoutWidget.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    @Override
    protected void refreshWidgetPositions() {
        this.layoutWidget.refreshPositions();
        if (this.list != null) {
            this.list.position(this.width, this.layoutWidget);
        }
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Environment(value=EnvType.CLIENT)
    static class LinksListWidget
    extends ElementListWidget<LinksListEntry> {
        public LinksListWidget(MinecraftClient client, int width, ServerLinksScreen screen) {
            super(client, width, screen.layoutWidget.getContentHeight(), screen.layoutWidget.getHeaderHeight(), 25);
            screen.serverLinks.entries().forEach(entry -> this.addEntry(new LinksListEntry(screen, (ServerLinks.Entry)entry)));
        }

        @Override
        public int getRowWidth() {
            return 310;
        }

        @Override
        public void position(int width, ThreePartsLayoutWidget layout) {
            super.position(width, layout);
            int j = width / 2 - 155;
            this.children().forEach(child -> child.button.setX(j));
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class LinksListEntry
    extends ElementListWidget.Entry<LinksListEntry> {
        final ClickableWidget button;

        LinksListEntry(Screen screen, ServerLinks.Entry link) {
            this.button = ButtonWidget.builder(link.getText(), ConfirmLinkScreen.opening(screen, link.link(), false)).width(310).build();
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.button.setY(y);
            this.button.render(context, mouseX, mouseY, tickDelta);
        }

        @Override
        public List<? extends Element> children() {
            return List.of(this.button);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of(this.button);
        }
    }
}

