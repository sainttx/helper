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

package me.lucko.helper.menu.scheme;

import com.google.common.collect.ImmutableList;

import me.lucko.helper.menu.Gui;
import me.lucko.helper.utils.annotation.NonnullByDefault;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

/**
 * Helps to populate a menu with border items
 */
@NonnullByDefault
public class MenuScheme {
    private final SchemeMapping mapping;
    private final List<boolean[]> maskRows;
    private final List<int[]> schemeRows;

    public MenuScheme(@Nullable SchemeMapping mapping) {
        this.mapping = mapping == null ? StandardSchemeMappings.EMPTY : mapping;
        this.maskRows = new ArrayList<>();
        this.schemeRows = new ArrayList<>();
    }

    public MenuScheme() {
        this((SchemeMapping) null);
    }

    private MenuScheme(MenuScheme other) {
        this.mapping = other.mapping.copy();
        this.maskRows = new ArrayList<>();
        for (boolean[] arr : other.maskRows) {
            maskRows.add(Arrays.copyOf(arr, arr.length));
        }
        this.schemeRows = new ArrayList<>();
        for (int[] arr : other.schemeRows) {
            schemeRows.add(Arrays.copyOf(arr, arr.length));
        }
    }

    public MenuScheme mask(String s) {
        char[] chars = s.replace(" ", "").toCharArray();
        if (chars.length != 9) {
            throw new IllegalArgumentException("invalid mask: " + s);
        }
        boolean[] ret = new boolean[9];
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '1' || c == 't') {
                ret[i] = true;
            } else if (c == '0' || c == 'f' || c == 'x') {
                ret[i] = false;
            } else {
                throw new IllegalArgumentException("invalid mask character: " + c);
            }
        }
        maskRows.add(ret);
        return this;
    }

    public MenuScheme masks(String... strings) {
        for (String s : strings) {
            mask(s);
        }
        return this;
    }

    public MenuScheme maskEmpty(int lines) {
        for (int i = 0; i < lines; i++) {
            maskRows.add(new boolean[]{false, false, false, false, false, false, false, false, false});
            schemeRows.add(new int[]{});
        }
        return this;
    }

    public MenuScheme scheme(int... schemeIds) {
        for (int schemeId : schemeIds) {
            if (!mapping.hasMappingFor(schemeId)) {
                throw new IllegalArgumentException("mapping does not contain value for id: " + schemeId);
            }
        }
        schemeRows.add(schemeIds);
        return this;
    }

    public void apply(Gui gui) {
        try {
            // the index of the item slot in the inventory
            AtomicInteger invIndex = new AtomicInteger(-1);

            // iterate all of the loaded masks
            for (int i = 0; i < maskRows.size(); i++) {
                boolean[] mask = maskRows.get(i);
                int[] scheme = schemeRows.get(i);

                AtomicInteger schemeIndex = new AtomicInteger(-1);

                // iterate the values in the mask (0 --> 8)
                for (boolean b : mask) {

                    // increment the index in the gui. we're handling a new item.
                    int index = invIndex.incrementAndGet();

                    // if this index is masked.
                    if (b) {
                        // the index of the mapping from schemeRows
                        int schemeId = schemeIndex.incrementAndGet();

                        // this is the value from the scheme map for this slot.
                        int schemeMappingId = scheme[schemeId];

                        // lookup the value for this location, and apply it to the gui
                        gui.setItem(index, mapping.getNullable(schemeMappingId));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Integer> getMaskedIndexes() {
        List<Integer> ret = new ArrayList<>();
        try {
            // the index of the item slot in the inventory
            AtomicInteger invIndex = new AtomicInteger(-1);

            // iterate all of the loaded masks
            for (boolean[] mask : maskRows) {
                // iterate the values in the mask (0 --> 8)
                for (boolean b : mask) {

                    // increment the index in the gui. we're handling a new item.
                    int index = invIndex.incrementAndGet();

                    // if this index is masked.
                    if (b) {
                        ret.add(index);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public ImmutableList<Integer> getMaskedIndexesImmutable() {
        return ImmutableList.copyOf(getMaskedIndexes());
    }

    public MenuPopulator newPopulator(Gui gui) {
        return new MenuPopulator(gui, this);
    }

    public MenuScheme copy() {
        return new MenuScheme(this);
    }
}
