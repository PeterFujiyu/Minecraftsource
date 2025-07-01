/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.world;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.world.WorldScreenOptionGrid;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ContainerWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ExperimentsScreen
extends Screen {
    private static final Text TITLE = Text.translatable("selectWorld.experiments");
    private static final Text INFO_TEXT = Text.translatable("selectWorld.experiments.info").formatted(Formatting.RED);
    private static final int EXPERIMENTS_LIST_WIDTH = 310;
    private static final int EXPERIMENTS_LIST_HEIGHT = 130;
    private final ThreePartsLayoutWidget experimentToggleList = new ThreePartsLayoutWidget(this);
    private final Screen parent;
    private final ResourcePackManager resourcePackManager;
    private final Consumer<ResourcePackManager> applier;
    private final Object2BooleanMap<ResourcePackProfile> experiments = new Object2BooleanLinkedOpenHashMap<ResourcePackProfile>();
    @Nullable
    private ExperimentsListWidget experimentsList;

    public ExperimentsScreen(Screen parent, ResourcePackManager resourcePackManager, Consumer<ResourcePackManager> applier) {
        super(TITLE);
        this.parent = parent;
        this.resourcePackManager = resourcePackManager;
        this.applier = applier;
        for (ResourcePackProfile lv : resourcePackManager.getProfiles()) {
            if (lv.getSource() != ResourcePackSource.FEATURE) continue;
            this.experiments.put(lv, resourcePackManager.getEnabledProfiles().contains(lv));
        }
    }

    @Override
    protected void init() {
        this.experimentToggleList.addHeader(TITLE, this.textRenderer);
        DirectionalLayoutWidget lv = this.experimentToggleList.addBody(DirectionalLayoutWidget.vertical());
        lv.add(new MultilineTextWidget(INFO_TEXT, this.textRenderer).setMaxWidth(310), positioner -> positioner.marginBottom(15));
        WorldScreenOptionGrid.Builder lv2 = WorldScreenOptionGrid.builder(299).withTooltipBox(2, true).setRowSpacing(4);
        this.experiments.forEach((pack, enabled2) -> lv2.add(ExperimentsScreen.getDataPackName(pack), () -> this.experiments.getBoolean(pack), enabled -> this.experiments.put((ResourcePackProfile)pack, (boolean)enabled)).tooltip(pack.getDescription()));
        LayoutWidget lv3 = lv2.build().getLayout();
        this.experimentsList = new ExperimentsListWidget(this, lv3, 310, 130);
        lv.add(this.experimentsList);
        DirectionalLayoutWidget lv4 = this.experimentToggleList.addFooter(DirectionalLayoutWidget.horizontal().spacing(8));
        lv4.add(ButtonWidget.builder(ScreenTexts.DONE, button -> this.applyAndClose()).build());
        lv4.add(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.close()).build());
        this.experimentToggleList.forEachChild(widget -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(widget);
        });
        this.refreshWidgetPositions();
    }

    private static Text getDataPackName(ResourcePackProfile packProfile) {
        String string = "dataPack." + packProfile.getId() + ".name";
        return I18n.hasTranslation(string) ? Text.translatable(string) : packProfile.getDisplayName();
    }

    @Override
    protected void refreshWidgetPositions() {
        this.experimentsList.setHeight(130);
        this.experimentToggleList.refreshPositions();
        int i = this.height - this.experimentToggleList.getFooterHeight() - this.experimentsList.getNavigationFocus().getBottom();
        this.experimentsList.setHeight(this.experimentsList.getHeight() + i);
        this.experimentsList.refreshScroll();
    }

    @Override
    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences(super.getNarratedTitle(), INFO_TEXT);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    private void applyAndClose() {
        ArrayList<ResourcePackProfile> list = new ArrayList<ResourcePackProfile>(this.resourcePackManager.getEnabledProfiles());
        ArrayList list2 = new ArrayList();
        this.experiments.forEach((pack, enabled) -> {
            list.remove(pack);
            if (enabled.booleanValue()) {
                list2.add(pack);
            }
        });
        list.addAll(Lists.reverse(list2));
        this.resourcePackManager.setEnabledProfiles(list.stream().map(ResourcePackProfile::getId).toList());
        this.applier.accept(this.resourcePackManager);
    }

    @Environment(value=EnvType.CLIENT)
    public class ExperimentsListWidget
    extends ContainerWidget {
        private final List<ClickableWidget> children = new ArrayList<ClickableWidget>();
        private final LayoutWidget layout;

        public ExperimentsListWidget(ExperimentsScreen arg, LayoutWidget layout, int width, int height) {
            super(0, 0, width, height, ScreenTexts.EMPTY);
            this.layout = layout;
            layout.forEachChild(this::add);
        }

        public void add(ClickableWidget child) {
            this.children.add(child);
        }

        @Override
        protected int getContentsHeightWithPadding() {
            return this.layout.getHeight();
        }

        @Override
        protected double getDeltaYPerScroll() {
            return 10.0;
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            context.enableScissor(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height);
            context.getMatrices().push();
            context.getMatrices().translate(0.0, -this.getScrollY(), 0.0);
            for (ClickableWidget lv : this.children) {
                lv.render(context, mouseX, mouseY, delta);
            }
            context.getMatrices().pop();
            context.disableScissor();
            this.drawScrollbar(context);
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        }

        @Override
        public ScreenRect getBorder(NavigationDirection direction) {
            return new ScreenRect(this.getX(), this.getY(), this.width, this.getContentsHeightWithPadding());
        }

        @Override
        public void setFocused(@Nullable Element focused) {
            super.setFocused(focused);
            if (focused == null) {
                return;
            }
            ScreenRect lv = this.getNavigationFocus();
            ScreenRect lv2 = focused.getNavigationFocus();
            int i = (int)((double)lv2.getTop() - this.getScrollY() - (double)lv.getTop());
            int j = (int)((double)lv2.getBottom() - this.getScrollY() - (double)lv.getBottom());
            if (i < 0) {
                this.setScrollY(this.getScrollY() + (double)i - 14.0);
            } else if (j > 0) {
                this.setScrollY(this.getScrollY() + (double)j + 14.0);
            }
        }

        @Override
        public List<? extends Element> children() {
            return this.children;
        }

        @Override
        public void setX(int x) {
            super.setX(x);
            this.layout.setX(x);
            this.layout.refreshPositions();
        }

        @Override
        public void setY(int y) {
            super.setY(y);
            this.layout.setY(y);
            this.layout.refreshPositions();
        }

        @Override
        public Collection<? extends Selectable> getNarratedParts() {
            return this.children;
        }
    }
}

