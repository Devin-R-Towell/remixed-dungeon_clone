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
package com.watabou.pixeldungeon.actors.mobs;

import com.nyrds.pixeldungeon.mechanics.NamedEntityKind;
import com.nyrds.pixeldungeon.ml.R;
import com.nyrds.platform.game.Game;
import com.watabou.pixeldungeon.actors.CharUtils;
import com.watabou.pixeldungeon.actors.buffs.Terror;
import com.watabou.pixeldungeon.items.Gold;
import com.watabou.pixeldungeon.sprites.CharSprite;
import com.watabou.pixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import org.jetbrains.annotations.NotNull;

public class Brute extends Mob {

	public Brute() {

		hp(ht(40));
		baseDefenseSkill = 15;
		baseDefenseSkill = 20;

		exp = 8;
		maxLvl = 15;

		dr = 8;

		loot(Gold.class, 0.5f);
		
		addImmunity( Terror.class );
	}
	
	private boolean enraged = false;
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		enraged = hp() < ht() / 4;
	}
	
	@Override
	public int damageRoll() {
		return enraged ?
			Random.NormalIntRange( 10, 40 ) :	
			Random.NormalIntRange( 8, 18 );
	}

	@Override
	public void damage(int dmg, @NotNull NamedEntityKind src ) {
		super.damage( dmg, src );
		
		if (isAlive() && !enraged && hp() < ht() / 4) {
			enraged = true;
			spend( TICK );
			if (CharUtils.isVisible(this)) {
				GLog.w( Game.getVar(R.string.Brute_Enraged), getName() );
				getSprite().showStatus( CharSprite.NEGATIVE, Game.getVar(R.string.Brute_StaEnraged));
			}
		}
	}
}
