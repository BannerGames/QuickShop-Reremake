/*
 * This file is a part of project QuickShop, the name is WorldGuardFlags.java
 * Copyright (C) Ghost_chu <https://github.com/Ghost-chu>
 * Copyright (C) Bukkit Commons Studio and contributors
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.maxgamer.quickshop.integration.worldguard;

import java.util.ArrayList;
import java.util.List;

public enum WorldGuardFlags {
    FLAG,
    BUILD,
    CHEST_ACCESS,
    INTERACT;

    public static List<WorldGuardFlags> deserialize(List<String> list) {
        List<WorldGuardFlags> result = new ArrayList<>();
        list.forEach(v -> result.add(WorldGuardFlags.valueOf(v.toUpperCase())));
        return result;
    }

    public static List<String> serialize(List<WorldGuardFlags> list) {
        List<String> result = new ArrayList<>();
        list.forEach(v -> result.add(v.name()));
        return result;
    }
}
