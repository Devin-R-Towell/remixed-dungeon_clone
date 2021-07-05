/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014  Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.watabou.pixeldungeon.items.scrolls;

import com.nyrds.platform.audio.Sample;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.CharUtils;
import com.watabou.pixeldungeon.actors.buffs.Invisibility;

import org.jetbrains.annotations.NotNull;

public class ScrollOfTeleportation extends Scroll {

	@Override
	protected void doRead(@NotNull Char reader) {

		Sample.INSTANCE.play( Assets.SND_READ );
		Invisibility.dispel(reader);
		
		CharUtils.teleportRandom( reader);
		setKnown();

		reader.spendAndNext( TIME_TO_READ );
	}

	@Override
	public int price() {
		return isKnown() ? 40 * quantity() : super.price();
	}
}
