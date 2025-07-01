/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderPass;
import net.minecraft.client.util.ClosableFactory;
import net.minecraft.client.util.ObjectAllocator;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class FrameGraphBuilder {
    private final List<ResourceNode<?>> resourceNodes = new ArrayList();
    private final List<ObjectNode<?>> objectNodes = new ArrayList();
    private final List<FramePass> passes = new ArrayList<FramePass>();

    public RenderPass createPass(String name) {
        FramePass lv = new FramePass(this.passes.size(), name);
        this.passes.add(lv);
        return lv;
    }

    public <T> net.minecraft.client.util.Handle<T> createObjectNode(String name, T object) {
        ObjectNode<T> lv = new ObjectNode<T>(name, null, object);
        this.objectNodes.add(lv);
        return lv.handle;
    }

    public <T> net.minecraft.client.util.Handle<T> createResourceHandle(String name, ClosableFactory<T> factory) {
        return this.createResourceNode((String)name, factory, null).handle;
    }

    <T> ResourceNode<T> createResourceNode(String name, ClosableFactory<T> factory, @Nullable FramePass stageNode) {
        int i = this.resourceNodes.size();
        ResourceNode<T> lv = new ResourceNode<T>(i, name, stageNode, factory);
        this.resourceNodes.add(lv);
        return lv;
    }

    public void run(ObjectAllocator allocator) {
        this.run(allocator, Profiler.NONE);
    }

    public void run(ObjectAllocator allocator, Profiler profiler) {
        BitSet bitSet = this.collectPassesToVisit();
        ArrayList<FramePass> list = new ArrayList<FramePass>(bitSet.cardinality());
        BitSet bitSet2 = new BitSet(this.passes.size());
        for (FramePass lv : this.passes) {
            this.visit(lv, bitSet, bitSet2, list);
        }
        this.checkResources(list);
        for (FramePass lv : list) {
            for (ResourceNode<?> lv2 : lv.resourcesToAcquire) {
                profiler.acquire(lv2.name);
                lv2.acquire(allocator);
            }
            profiler.push(lv.name);
            lv.renderer.run();
            profiler.pop(lv.name);
            int i = lv.resourcesToRelease.nextSetBit(0);
            while (i >= 0) {
                ResourceNode<?> lv2;
                lv2 = this.resourceNodes.get(i);
                profiler.release(lv2.name);
                lv2.release(allocator);
                i = lv.resourcesToRelease.nextSetBit(i + 1);
            }
        }
    }

    private BitSet collectPassesToVisit() {
        ArrayDeque<FramePass> deque = new ArrayDeque<FramePass>(this.passes.size());
        BitSet bitSet = new BitSet(this.passes.size());
        for (Node node : this.objectNodes) {
            FramePass lv2 = node.handle.from;
            if (lv2 == null) continue;
            this.markForVisit(lv2, bitSet, deque);
        }
        for (FramePass framePass : this.passes) {
            if (!framePass.toBeVisited) continue;
            this.markForVisit(framePass, bitSet, deque);
        }
        return bitSet;
    }

    private void markForVisit(FramePass pass, BitSet result, Deque<FramePass> deque) {
        deque.add(pass);
        while (!deque.isEmpty()) {
            FramePass lv = deque.poll();
            if (result.get(lv.id)) continue;
            result.set(lv.id);
            int i = lv.requiredPassIds.nextSetBit(0);
            while (i >= 0) {
                deque.add(this.passes.get(i));
                i = lv.requiredPassIds.nextSetBit(i + 1);
            }
        }
    }

    private void visit(FramePass node, BitSet unvisited, BitSet visiting, List<FramePass> topologicalOrderOut) {
        if (visiting.get(node.id)) {
            String string = visiting.stream().mapToObj(id -> this.passes.get((int)id).name).collect(Collectors.joining(", "));
            throw new IllegalStateException("Frame graph cycle detected between " + string);
        }
        if (!unvisited.get(node.id)) {
            return;
        }
        visiting.set(node.id);
        unvisited.clear(node.id);
        int i = node.requiredPassIds.nextSetBit(0);
        while (i >= 0) {
            this.visit(this.passes.get(i), unvisited, visiting, topologicalOrderOut);
            i = node.requiredPassIds.nextSetBit(i + 1);
        }
        for (Handle<?> lv : node.transferredHandles) {
            int j = lv.dependents.nextSetBit(0);
            while (j >= 0) {
                if (j != node.id) {
                    this.visit(this.passes.get(j), unvisited, visiting, topologicalOrderOut);
                }
                j = lv.dependents.nextSetBit(j + 1);
            }
        }
        topologicalOrderOut.add(node);
        visiting.clear(node.id);
    }

    private void checkResources(Collection<FramePass> passes) {
        FramePass[] lvs = new FramePass[this.resourceNodes.size()];
        for (FramePass lv : passes) {
            int i = lv.requiredResourceIds.nextSetBit(0);
            while (i >= 0) {
                ResourceNode<?> lv2 = this.resourceNodes.get(i);
                FramePass lv3 = lvs[i];
                lvs[i] = lv;
                if (lv3 == null) {
                    lv.resourcesToAcquire.add(lv2);
                } else {
                    lv3.resourcesToRelease.clear(i);
                }
                lv.resourcesToRelease.set(i);
                i = lv.requiredResourceIds.nextSetBit(i + 1);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class FramePass
    implements RenderPass {
        final int id;
        final String name;
        final List<Handle<?>> transferredHandles = new ArrayList();
        final BitSet requiredResourceIds = new BitSet();
        final BitSet requiredPassIds = new BitSet();
        Runnable renderer = () -> {};
        final List<ResourceNode<?>> resourcesToAcquire = new ArrayList();
        final BitSet resourcesToRelease = new BitSet();
        boolean toBeVisited;

        public FramePass(int id, String name) {
            this.id = id;
            this.name = name;
        }

        private <T> void addRequired(Handle<T> handle) {
            Node node = handle.parent;
            if (node instanceof ResourceNode) {
                ResourceNode lv = (ResourceNode)node;
                this.requiredResourceIds.set(lv.id);
            }
        }

        private void addRequired(FramePass child) {
            this.requiredPassIds.set(child.id);
        }

        @Override
        public <T> net.minecraft.client.util.Handle<T> addRequiredResource(String name, ClosableFactory<T> factory) {
            ResourceNode<T> lv = FrameGraphBuilder.this.createResourceNode(name, factory, this);
            this.requiredResourceIds.set(lv.id);
            return lv.handle;
        }

        @Override
        public <T> void dependsOn(net.minecraft.client.util.Handle<T> handle) {
            this.dependsOn((Handle)handle);
        }

        private <T> void dependsOn(Handle<T> handle) {
            this.addRequired(handle);
            if (handle.from != null) {
                this.addRequired(handle.from);
            }
            handle.dependents.set(this.id);
        }

        @Override
        public <T> net.minecraft.client.util.Handle<T> transfer(net.minecraft.client.util.Handle<T> handle) {
            return this.transfer((Handle)handle);
        }

        @Override
        public void addRequired(RenderPass pass) {
            this.requiredPassIds.set(((FramePass)pass).id);
        }

        @Override
        public void markToBeVisited() {
            this.toBeVisited = true;
        }

        private <T> Handle<T> transfer(Handle<T> handle) {
            this.transferredHandles.add(handle);
            this.dependsOn(handle);
            return handle.moveTo(this);
        }

        @Override
        public void setRenderer(Runnable renderer) {
            this.renderer = renderer;
        }

        public String toString() {
            return this.name;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ObjectNode<T>
    extends Node<T> {
        private final T value;

        public ObjectNode(String name, @Nullable FramePass parent, T value) {
            super(name, parent);
            this.value = value;
        }

        @Override
        public T get() {
            return this.value;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Handle<T>
    implements net.minecraft.client.util.Handle<T> {
        final Node<T> parent;
        private final int id;
        @Nullable
        final FramePass from;
        final BitSet dependents = new BitSet();
        @Nullable
        private Handle<T> movedTo;

        Handle(Node<T> parent, int id, @Nullable FramePass from) {
            this.parent = parent;
            this.id = id;
            this.from = from;
        }

        @Override
        public T get() {
            return this.parent.get();
        }

        Handle<T> moveTo(FramePass pass) {
            if (this.parent.handle != this) {
                throw new IllegalStateException("Handle " + String.valueOf(this) + " is no longer valid, as its contents were moved into " + String.valueOf(this.movedTo));
            }
            Handle<T> lv = new Handle<T>(this.parent, this.id + 1, pass);
            this.parent.handle = lv;
            this.movedTo = lv;
            return lv;
        }

        public String toString() {
            if (this.from != null) {
                return String.valueOf(this.parent) + "#" + this.id + " (from " + String.valueOf(this.from) + ")";
            }
            return String.valueOf(this.parent) + "#" + this.id;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ResourceNode<T>
    extends Node<T> {
        final int id;
        private final ClosableFactory<T> factory;
        @Nullable
        private T resource;

        public ResourceNode(int id, String name, @Nullable FramePass from, ClosableFactory<T> factory) {
            super(name, from);
            this.id = id;
            this.factory = factory;
        }

        @Override
        public T get() {
            return Objects.requireNonNull(this.resource, "Resource is not currently available");
        }

        public void acquire(ObjectAllocator allocator) {
            if (this.resource != null) {
                throw new IllegalStateException("Tried to acquire physical resource, but it was already assigned");
            }
            this.resource = allocator.acquire(this.factory);
        }

        public void release(ObjectAllocator allocator) {
            if (this.resource == null) {
                throw new IllegalStateException("Tried to release physical resource that was not allocated");
            }
            allocator.release(this.factory, this.resource);
            this.resource = null;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Profiler {
        public static final Profiler NONE = new Profiler(){};

        default public void acquire(String name) {
        }

        default public void release(String name) {
        }

        default public void push(String location) {
        }

        default public void pop(String location) {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static abstract class Node<T> {
        public final String name;
        public Handle<T> handle;

        public Node(String name, @Nullable FramePass from) {
            this.name = name;
            this.handle = new Handle(this, 0, from);
        }

        public abstract T get();

        public String toString() {
            return this.name;
        }
    }
}

