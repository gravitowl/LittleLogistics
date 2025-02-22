package dev.murad.shipping.item.container;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import dev.murad.shipping.network.SetTag;
import dev.murad.shipping.network.TugRoutePacketHandler;
import dev.murad.shipping.util.TugRoute;
import dev.murad.shipping.util.TugRouteNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.list.ExtendedList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Optional;

public class TugRouteClientHandler {
    private static final Logger LOGGER = LogManager.getLogger(TugRouteClientHandler.class);

    private TugList widget;
    private TugRoute route;
    private boolean isOffHand;
    private Minecraft minecraft;
    private TugRouteScreen screen;

    public TugRouteClientHandler(TugRouteScreen screen, Minecraft minecraft, TugRoute route, boolean isOffHand) {
        this.route = route;
        this.isOffHand = isOffHand;
        this.screen = screen;
        this.minecraft = minecraft;
    }

    public TugList initializeWidget(int width, int height, int y0, int y1, int itemHeight) {
        this.widget = new TugRouteClientHandler.TugList(minecraft, width, height,
                y0, y1, itemHeight);
        for (int i = 0; i < route.size(); i++) {
            this.widget.add(route.get(i), i);
        }

        return this.widget;
    }

    public void deleteSelected() {
        TugList.Entry selected = widget.getSelected();
        if (selected != null) {
            int index = selected.index;
            route.remove(index);
            this.widget.children().remove(index);
            this.widget.setSelected(null);
            markDirty();
        }
    }

    public void moveSelectedUp() {
        TugList.Entry selected = widget.getSelected();
        if (selected != null) {
            int index = selected.index;
            if (index > 0) {
                TugRouteNode node = route.remove(selected.index);
                this.widget.children().remove(index);
                route.add(index - 1, node);
                this.widget.children().add(index - 1, selected);
                markDirty();
            }
        }
    }

    public void moveSelectedDown() {
        TugList.Entry selected = widget.getSelected();
        if (selected != null) {
            int index = selected.index;
            if (index < this.route.size() - 1) {
                TugRouteNode node = route.remove(selected.index);
                this.widget.children().remove(index);
                route.add(index + 1, node);
                this.widget.children().add(index + 1, selected);
                markDirty();
            }
        }
    }

    public void renameSelected(@Nullable String name) {
        TugList.Entry selected = widget.getSelected();
        if (selected != null) {
            int index = selected.index;
            this.route.get(index).setName(name);
            markDirty();
        }
    }

    public Optional<Pair<Integer, TugRouteNode>> getSelected() {
        TugList.Entry selected = widget.getSelected();
        if (selected != null) {
            int index = selected.index;
            return Optional.of(new Pair<>(index, route.get(index)));
        }

        return Optional.empty();
    }

    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.widget.render(stack, mouseX, mouseY, partialTicks);
    }

    public final class TugList extends ExtendedList<TugList.Entry> {
        public TugList(Minecraft minecraft, int width, int height, int y0, int y1, int itemHeight) {
            super(minecraft, width, height,
                    y0, y1, itemHeight);
            setRenderBackground(false);
            setRenderTopAndBottom(false);
        }

        @Override
        public Optional<IGuiEventListener> getChildAt(double p_212930_1_, double p_212930_3_) {
            return super.getChildAt(p_212930_1_, p_212930_3_);
        }

        @Override
        public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
            super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        }

        public void add(TugRouteNode node, int index) {
            this.addEntry(new Entry(node, index));
        }

        @Override
        public int getRowWidth() {
            return screen.getXSize() - 40;
        }

        @Override
        protected int getScrollbarPosition() {
            return (this.width + getRowWidth()) / 2 + 5;
        }

        public class Entry extends ExtendedList.AbstractListEntry<Entry> {
            private TugRouteNode node;
            private int index;
            public Entry(TugRouteNode node, int index) {
                this.node = node;
                this.index = index;
            }

            @Override
            public void render(MatrixStack matrixStack, int ind, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTicks) {
                String s = node.getDisplayName(index) + ": " + node.getDisplayCoords();

                screen.getMinecraft().getTextureManager().bind(TugRouteScreen.GUI);
                blit(matrixStack, rowLeft, rowTop, 0, hovered ? 216 : 236, width - 3, height);
                screen.getFont().draw(matrixStack, s, rowLeft + 3, (float) (rowTop + 4), 16777215);
            }

            public void setIndex(int index) {
                this.index = index;
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == 0) {
                    this.select();
                    return true;
                }
                return false;
            }

            private void select() {
                TugList.this.setSelected(this);
            }

        }
    }

    private void markDirty() {
        int i = 0;
        for (TugList.Entry entry : this.widget.children()) {
            entry.setIndex(i++);
        }

        TugRoutePacketHandler.INSTANCE.sendToServer(new SetTag(route.hashCode(), isOffHand, route.toNBT()));
    }
}
