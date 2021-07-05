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
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.sprites.CharSprite;
import com.watabou.pixeldungeon.ui.BuffIndicator;
import com.watabou.pixeldungeon.utils.GLog;

public class MindVision extends FlavourBuff {

	public static final float DURATION = 20f;


	static public void reportMindVisionEffect() {
		Dungeon.observe();
		if (Dungeon.level.mobs.size() > 0) {
			GLog.i(Game.getVar(R.string.PotionOfMindVision_Apply1));
		} else {
			GLog.i(Game.getVar(R.string.PotionOfMindVision_Apply2));
		}
	}

	@Override
	public int icon() {
		return BuffIndicator.MIND_VISION;
	}
	
	@Override
	public String name() {
		return Game.getVar(R.string.MindVisionBuff_Name);
	}

	@Override
	public String desc() {
		return Game.getVar(R.string.MindVisionBuff_Info);
	}

	@Override
	public void detach() {
		super.detach();
		Dungeon.observe();
	}

	@Override
	public void attachVisual() {
		target.getSprite().showStatus(CharSprite.POSITIVE, Game.getVar(R.string.Char_StaMind));
		target.getSprite().showStatus(CharSprite.POSITIVE, Game.getVar(R.string.Char_StaVision));
	}
}
