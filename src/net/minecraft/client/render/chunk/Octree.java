/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.chunk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class Octree {
    private final Branch root;
    final BlockPos centerPos;

    public Octree(ChunkSectionPos sectionPos, int viewDistance, int sizeY, int bottomY) {
        int l = viewDistance * 2 + 1;
        int m = MathHelper.smallestEncompassingPowerOfTwo(l);
        int n = viewDistance * 16;
        BlockPos lv = sectionPos.getMinPos();
        this.centerPos = sectionPos.getCenterPos();
        int o = lv.getX() - n;
        int p = o + m * 16 - 1;
        int q = m >= sizeY ? bottomY : lv.getY() - n;
        int r = q + m * 16 - 1;
        int s = lv.getZ() - n;
        int t = s + m * 16 - 1;
        this.root = new Branch(new BlockBox(o, q, s, p, r, t));
    }

    public boolean add(ChunkBuilder.BuiltChunk chunk) {
        return this.root.add(chunk);
    }

    public void visit(Visitor visitor, Frustum frustum, int margin) {
        this.root.visit(visitor, false, frustum, 0, margin, true);
    }

    boolean isCenterWithin(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, int margin) {
        int k = this.centerPos.getX();
        int l = this.centerPos.getY();
        int m = this.centerPos.getZ();
        return (double)k > minX - (double)margin && (double)k < maxX + (double)margin && (double)l > minY - (double)margin && (double)l < maxY + (double)margin && (double)m > minZ - (double)margin && (double)m < maxZ + (double)margin;
    }

    @Environment(value=EnvType.CLIENT)
    class Branch
    implements Node {
        private final Node[] children = new Node[8];
        private final BlockBox box;
        private final int centerX;
        private final int centerY;
        private final int centerZ;
        private final AxisOrder axisOrder;
        private final boolean easternSide;
        private final boolean topSide;
        private final boolean southernSide;

        public Branch(BlockBox box) {
            this.box = box;
            this.centerX = this.box.getMinX() + this.box.getBlockCountX() / 2;
            this.centerY = this.box.getMinY() + this.box.getBlockCountY() / 2;
            this.centerZ = this.box.getMinZ() + this.box.getBlockCountZ() / 2;
            int i = Octree.this.centerPos.getX() - this.centerX;
            int j = Octree.this.centerPos.getY() - this.centerY;
            int k = Octree.this.centerPos.getZ() - this.centerZ;
            this.axisOrder = AxisOrder.fromPos(Math.abs(i), Math.abs(j), Math.abs(k));
            this.easternSide = i < 0;
            this.topSide = j < 0;
            this.southernSide = k < 0;
        }

        public boolean add(ChunkBuilder.BuiltChunk chunk) {
            boolean bl = chunk.getOrigin().getX() - this.centerX < 0;
            boolean bl2 = chunk.getOrigin().getY() - this.centerY < 0;
            boolean bl3 = chunk.getOrigin().getZ() - this.centerZ < 0;
            boolean bl4 = bl != this.easternSide;
            boolean bl5 = bl2 != this.topSide;
            boolean bl6 = bl3 != this.southernSide;
            int i = Branch.getIndex(this.axisOrder, bl4, bl5, bl6);
            if (this.areChildrenLeaves()) {
                boolean bl7 = this.children[i] != null;
                this.children[i] = new Leaf(chunk);
                return !bl7;
            }
            if (this.children[i] != null) {
                Branch lv = (Branch)this.children[i];
                return lv.add(chunk);
            }
            BlockBox lv2 = this.getChildBox(bl, bl2, bl3);
            Branch lv3 = new Branch(lv2);
            this.children[i] = lv3;
            return lv3.add(chunk);
        }

        private static int getIndex(AxisOrder axisOrder, boolean sameRelativeSideX, boolean sameRelativeSideY, boolean sameRelativeSideZ) {
            int i = 0;
            if (sameRelativeSideX) {
                i += axisOrder.x;
            }
            if (sameRelativeSideY) {
                i += axisOrder.y;
            }
            if (sameRelativeSideZ) {
                i += axisOrder.z;
            }
            return i;
        }

        private boolean areChildrenLeaves() {
            return this.box.getBlockCountX() == 32;
        }

        private BlockBox getChildBox(boolean western, boolean bottom, boolean northern) {
            int n;
            int m;
            int l;
            int k;
            int j;
            int i;
            if (western) {
                i = this.box.getMinX();
                j = this.centerX - 1;
            } else {
                i = this.centerX;
                j = this.box.getMaxX();
            }
            if (bottom) {
                k = this.box.getMinY();
                l = this.centerY - 1;
            } else {
                k = this.centerY;
                l = this.box.getMaxY();
            }
            if (northern) {
                m = this.box.getMinZ();
                n = this.centerZ - 1;
            } else {
                m = this.centerZ;
                n = this.box.getMaxZ();
            }
            return new BlockBox(i, k, m, j, l, n);
        }

        @Override
        public void visit(Visitor visitor, boolean skipVisibilityCheck, Frustum frustum, int depth, int margin, boolean nearCenter) {
            boolean bl3 = skipVisibilityCheck;
            if (!skipVisibilityCheck) {
                int k = frustum.intersectAab(this.box);
                skipVisibilityCheck = k == -2;
                boolean bl = bl3 = k == -2 || k == -1;
            }
            if (bl3) {
                nearCenter = nearCenter && Octree.this.isCenterWithin(this.box.getMinX(), this.box.getMinY(), this.box.getMinZ(), this.box.getMaxX(), this.box.getMaxY(), this.box.getMaxZ(), margin);
                visitor.visit(this, skipVisibilityCheck, depth, nearCenter);
                for (Node lv : this.children) {
                    if (lv == null) continue;
                    lv.visit(visitor, skipVisibilityCheck, frustum, depth + 1, margin, nearCenter);
                }
            }
        }

        @Override
        @Nullable
        public ChunkBuilder.BuiltChunk getBuiltChunk() {
            return null;
        }

        @Override
        public Box getBoundingBox() {
            return new Box(this.box.getMinX(), this.box.getMinY(), this.box.getMinZ(), this.box.getMaxX() + 1, this.box.getMaxY() + 1, this.box.getMaxZ() + 1);
        }
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface Visitor {
        public void visit(Node var1, boolean var2, int var3, boolean var4);
    }

    @Environment(value=EnvType.CLIENT)
    static enum AxisOrder {
        XYZ(4, 2, 1),
        XZY(4, 1, 2),
        YXZ(2, 4, 1),
        YZX(1, 4, 2),
        ZXY(2, 1, 4),
        ZYX(1, 2, 4);

        final int x;
        final int y;
        final int z;

        private AxisOrder(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public static AxisOrder fromPos(int x, int y, int z) {
            if (x > y && x > z) {
                if (y > z) {
                    return XYZ;
                }
                return XZY;
            }
            if (y > x && y > z) {
                if (x > z) {
                    return YXZ;
                }
                return YZX;
            }
            if (x > y) {
                return ZXY;
            }
            return ZYX;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Node {
        public void visit(Visitor var1, boolean var2, Frustum var3, int var4, int var5, boolean var6);

        @Nullable
        public ChunkBuilder.BuiltChunk getBuiltChunk();

        public Box getBoundingBox();
    }

    @Environment(value=EnvType.CLIENT)
    final class Leaf
    implements Node {
        private final ChunkBuilder.BuiltChunk chunk;

        Leaf(ChunkBuilder.BuiltChunk chunk) {
            this.chunk = chunk;
        }

        @Override
        public void visit(Visitor visitor, boolean skipVisibilityCheck, Frustum frustum, int depth, int margin, boolean nearCenter) {
            Box lv = this.chunk.getBoundingBox();
            if (skipVisibilityCheck || frustum.isVisible(this.getBuiltChunk().getBoundingBox())) {
                nearCenter = nearCenter && Octree.this.isCenterWithin(lv.minX, lv.minY, lv.minZ, lv.maxX, lv.maxY, lv.maxZ, margin);
                visitor.visit(this, skipVisibilityCheck, depth, nearCenter);
            }
        }

        @Override
        public ChunkBuilder.BuiltChunk getBuiltChunk() {
            return this.chunk;
        }

        @Override
        public Box getBoundingBox() {
            return this.chunk.getBoundingBox();
        }
    }
}

