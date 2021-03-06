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

package me.lucko.helper.plugin;

import me.lucko.helper.Commands;
import me.lucko.helper.Helper;
import me.lucko.helper.utils.LoaderUtils;
import me.lucko.helper.utils.Players;

import org.bukkit.plugin.Plugin;

/**
 * Dummy plugin to make the server load this lib.
 * Really just an alternative to shading it into another project.
 */
public class DummyHelperPlugin extends ExtendedJavaPlugin {

    public DummyHelperPlugin() {
        getLogger().info("Initialized helper v" + getDescription().getVersion());
    }

    @Override
    protected void enable() {
        // cache the loader plugin
        LoaderUtils.getPlugin();

        // provide an info command
        Commands.create()
                .handler(c -> {
                    Players.msg(c.getSender(), "&7[&6helper&7] &7Running &6helper v" + getDescription().getVersion() + "&7.");
                    if (Helper.plugins().isPluginEnabled("helper-sql")) {
                        Plugin sqlPlugin = getPlugin("helper-sql", Plugin.class);
                        Players.msg(c.getSender(), "&7[&6helper&7] &7Running &6helper-sql v" + sqlPlugin.getDescription().getVersion() + "&7.");
                    }
                    if (Helper.plugins().isPluginEnabled("helper-redis")) {
                        Plugin sqlPlugin = getPlugin("helper-redis", Plugin.class);
                        Players.msg(c.getSender(), "&7[&6helper&7] &7Running &6helper-redis v" + sqlPlugin.getDescription().getVersion() + "&7.");
                    }
                })
                .register(this, "helper");
    }
}
