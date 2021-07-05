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
package com.watabou.pixeldungeon.plants;

import com.nyrds.pixeldungeon.levels.objects.Presser;
import com.nyrds.pixeldungeon.mechanics.CommonActions;
import com.nyrds.pixeldungeon.ml.R;
import com.nyrds.platform.game.Game;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.CharUtils;
import com.watabou.pixeldungeon.actors.buffs.Buff;
import com.watabou.pixeldungeon.actors.buffs.Vertigo;
import com.watabou.pixeldungeon.effects.CellEmitter;
import com.watabou.pixeldungeon.effects.Speck;
import com.watabou.pixeldungeon.items.potions.PotionOfMindVision;
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.pixeldungeon.utils.Utils;

import org.jetbrains.annotations.NotNull;

public class Fadeleaf extends Plant {

	public Fadeleaf () {
		imageIndex = 6;
	}
	
	public void effect(int pos, Presser ch) {

		if(ch instanceof Char) {
			CharUtils.teleportRandom((Char) ch);
		}

		if (Dungeon.visible[pos]) {
			CellEmitter.get( pos ).start( Speck.factory( Speck.LIGHT ), 0.2f, 3 );
		}		
	}

	@Override
	public String desc() {
		return Game.getVar(R.string.Fadeleaf_Desc);
	}
	
	public static class Seed extends com.watabou.pixeldungeon.plants.Seed {
		{
			plantName = Game.getVar(R.string.Fadeleaf_Name);
			
			name = Utils.format(Game.getVar(R.string.Plant_Seed), plantName);
			image = ItemSpriteSheet.SEED_FADELEAF;
			
			plantClass = Fadeleaf.class;
			alchemyClass = PotionOfMindVision.class;
		}
		
		@Override
		public String desc() {
			return Game.getVar(R.string.Fadeleaf_Desc);
		}
		
		@Override
		public void _execute(@NotNull Char chr, @NotNull String action ) {
			
			super._execute(chr, action );
			
			if (action.equals( CommonActions.AC_EAT )) {
				CharUtils.teleportRandom(chr);
				chr.spendAndNext(1);
				chr.curAction = null;
				Buff.affect(chr, Vertigo.class, Vertigo.DURATION * 2);
			}
		}
	}
}
