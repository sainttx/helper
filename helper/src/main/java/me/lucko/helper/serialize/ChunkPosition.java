/*
 * This file is part of helper, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.lucko.helper.serialize;

import com.flowpowered.math.vector.Vector2i;
import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.lucko.helper.Helper;
import me.lucko.helper.gson.GsonSerializable;
import me.lucko.helper.gson.JsonBuilder;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

import javax.annotation.Nonnull;

/**
 * An immutable and serializable chuck location object
 */
public final class ChunkPosition implements GsonSerializable {
    public static ChunkPosition deserialize(JsonElement element) {
        Preconditions.checkArgument(element.isJsonObject());
        JsonObject object = element.getAsJsonObject();

        Preconditions.checkArgument(object.has("x"));
        Preconditions.checkArgument(object.has("z"));
        Preconditions.checkArgument(object.has("world"));

        int x = object.get("x").getAsInt();
        int z = object.get("z").getAsInt();
        String world = object.get("world").getAsString();

        return of(x, z, world);
    }

    public static ChunkPosition of(int x, int z, String world) {
        Preconditions.checkNotNull(world, "world");
        return new ChunkPosition(x, z, world);
    }

    public static ChunkPosition of(int x, int z, World world) {
        Preconditions.checkNotNull(world, "world");
        return of(x, z, world.getName());
    }

    public static ChunkPosition of(Vector2i vector, String world) {
        Preconditions.checkNotNull(vector, "vector");
        Preconditions.checkNotNull(world, "world");
        return of(vector.getX(), vector.getY(), world);
    }

    public static ChunkPosition of(Vector2i vector, World world) {
        Preconditions.checkNotNull(vector, "vector");
        Preconditions.checkNotNull(world, "world");
        return of(vector.getX(), vector.getY(), world);
    }

    public static ChunkPosition of(Chunk location) {
        Preconditions.checkNotNull(location, "location");
        return of(location.getX(), location.getZ(), location.getWorld().getName());
    }

    private final int x;
    private final int z;
    private final String world;

    private ChunkPosition(int x, int z, String world) {
        this.x = x;
        this.z = z;
        this.world = world;
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public String getWorld() {
        return this.world;
    }

    public Vector2i toVector() {
        return new Vector2i(x, z);
    }

    public synchronized Chunk toChunk() {
        return Helper.world(world).get().getChunkAt(x, z);
    }

    public BlockPosition getBlock(int x, int y, int z) {
        return BlockPosition.of((this.x << 4) | (x & 0xF), y, (this.z << 4) | (z & 0xF), world);
    }

    public ChunkPosition getRelative(BlockFace face) {
        Preconditions.checkArgument(face != BlockFace.UP && face != BlockFace.DOWN, "invalid face");
        return ChunkPosition.of(x + face.getModX(), z + face.getModZ(), world);
    }

    public ChunkPosition getRelative(BlockFace face, int distance) {
        Preconditions.checkArgument(face != BlockFace.UP && face != BlockFace.DOWN, "invalid face");
        return ChunkPosition.of(x + (face.getModX() * distance), z + (face.getModZ() * distance), world);
    }

    public ChunkPosition add(Vector2i vector2i) {
        return add(vector2i.getX(), vector2i.getY());
    }

    public ChunkPosition add(int x, int z) {
        return ChunkPosition.of(this.x + x, this.z + z, world);
    }

    public ChunkPosition subtract(Vector2i vector2i) {
        return subtract(vector2i.getX(), vector2i.getY());
    }

    public ChunkPosition subtract(int x, int z) {
        return add(-x, -z);
    }

    @Nonnull
    @Override
    public JsonObject serialize() {
        return JsonBuilder.object()
                .add("x", x)
                .add("z", z)
                .add("world", world)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof ChunkPosition)) return false;
        final ChunkPosition other = (ChunkPosition) o;
        return this.getX() == other.getX() &&
                this.getZ() == other.getZ() &&
                (this.getWorld() == null ? other.getWorld() == null : this.getWorld().equals(other.getWorld()));
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getX();
        result = result * PRIME + this.getZ();
        result = result * PRIME + (this.getWorld() == null ? 43 : this.getWorld().hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "ChunkPosition(x=" + this.getX() + ", z=" + this.getZ() + ", world=" + this.getWorld() + ")";
    }

}