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
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.buffs.Invisibility;
import com.watabou.pixeldungeon.items.wands.WandOfBlink;
import com.watabou.pixeldungeon.levels.Level;

import org.jetbrains.annotations.NotNull;

public class ScrollOfMirrorImage extends Scroll {

	private static final int NIMAGES	= 3;

	@Override
	protected void doRead(@NotNull Char reader) {

		Level level = Dungeon.level;

		int nImages = NIMAGES;
		while (nImages > 0 ) {
			int cell = level.getEmptyCellNextTo(reader.getPos());

			if(!level.cellValid(cell))
				break;

			Char image  = reader.makeClone();
			WandOfBlink.appear( image, cell );
			nImages--;
		}
		
		if (nImages < NIMAGES) {
			setKnown();
		}
		
		Sample.INSTANCE.play( Assets.SND_READ );
		Invisibility.dispel(reader);
		
		reader.spendAndNext( TIME_TO_READ );
	}
}
