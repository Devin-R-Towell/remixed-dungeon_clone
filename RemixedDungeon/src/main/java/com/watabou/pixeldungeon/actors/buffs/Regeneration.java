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
package com.watabou.pixeldungeon.actors.buffs;

import com.nyrds.pixeldungeon.ml.R;
import com.nyrds.platform.game.Game;
import com.watabou.pixeldungeon.actors.Char;

import org.jetbrains.annotations.NotNull;

public class Regeneration extends Buff {

    private static final float REGENERATION_DELAY = 10;

    @Override
    public boolean act() {
        if (target.isAlive()) {
            if (target.isStarving() || target.level().isSafe()) {
            } else {
                target.heal(1,this);
            }

            final int[] bonus = {0};

            target.forEachBuff(b-> bonus[0] +=b.regenerationBonus());

            spend((float) (REGENERATION_DELAY / Math.pow(1.2, bonus[0])));
        } else {
            deactivateActor();
        }
        return true;
    }

	@Override
	public boolean attachTo(@NotNull Char target ) {
        return target.hasBuff(Regeneration.class) || super.attachTo(target);
    }

    @Override
    public String name() {
        return Game.getVar(R.string.RegenerationBuff_Name);
    }

    @Override
    public String desc() {
        return Game.getVar(R.string.RegenerationBuff_Info);
    }
}
