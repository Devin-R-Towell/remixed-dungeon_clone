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
package com.watabou.pixeldungeon.items.potions;

import com.nyrds.pixeldungeon.mechanics.buffs.BuffFactory;
import com.nyrds.pixeldungeon.ml.R;
import com.nyrds.platform.audio.Sample;
import com.nyrds.platform.game.Game;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.blobs.Blob;
import com.watabou.pixeldungeon.actors.blobs.ConfusionGas;
import com.watabou.pixeldungeon.actors.blobs.ParalyticGas;
import com.watabou.pixeldungeon.actors.blobs.ToxicGas;
import com.watabou.pixeldungeon.actors.buffs.Buff;
import com.watabou.pixeldungeon.effects.CellEmitter;
import com.watabou.pixeldungeon.effects.Speck;
import com.watabou.pixeldungeon.items.food.RottenFood;
import com.watabou.pixeldungeon.items.weapon.missiles.Arrow;
import com.watabou.pixeldungeon.items.weapon.missiles.CommonArrow;
import com.watabou.pixeldungeon.utils.BArray;
import com.watabou.pixeldungeon.utils.GLog;
import com.watabou.utils.PathFinder;

public class PotionOfPurity extends UpgradablePotion{

	{
		labelIndex = 4;
	}

	private static final int DISTANCE = 2;

	@Override
	public void shatter(int cell) {

		PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.losBlocking, null), (int) (DISTANCE * qualityFactor()));

		boolean procd = false;

		Blob[] blobs = {
				Dungeon.level.blobs.get(ToxicGas.class),
				Dungeon.level.blobs.get(ParalyticGas.class),
				Dungeon.level.blobs.get(ConfusionGas.class)
		};

		for (Blob blob : blobs) {

			if (blob == null) {
				continue;
			}

			for (int i = 0; i < Dungeon.level.getLength(); i++) {
				if (PathFinder.distance[i] < Integer.MAX_VALUE) {

					int value = blob.cur[i];
					if (value > 0) {

						blob.cur[i] = 0;
						blob.volume -= value;
						procd = true;

						CellEmitter.get(i).burst(Speck.factory(Speck.DISCOVER), 1);
					}

				}
			}
		}

		boolean heroAffected = PathFinder.distance[Dungeon.hero.getPos()] < Integer.MAX_VALUE;

		if (procd) {

			splash(cell);
			Sample.INSTANCE.play(Assets.SND_SHATTER);

			setKnown();

			if (heroAffected) {
				GLog.p(Game.getVar(R.string.PotionOfPurity_Freshness));
			}

		} else {

			super.shatter(cell);

			if (heroAffected) {
				GLog.i(Game.getVar(R.string.PotionOfPurity_Freshness));
				setKnown();
			}

		}
	}

	@Override
	protected void apply(Char hero) {
		GLog.w(Game.getVar(R.string.PotionOfPurity_NoSmell));

		Buff.prolong(hero, BuffFactory.GASES_IMMUNITY, (float) (5 * qualityFactor()));

		setKnown();
	}

	@Override
	public String desc() {
		return Game.getVar(R.string.PotionOfPurity_Info);
	}

	@Override
	public int basePrice() {
		return 50;
	}

	@Override
	protected void moistenRottenFood(RottenFood rfood, Char owner) {
		detachMoistenItems(rfood, (int) (1*qualityFactor()));
		moistenEffective();
		GLog.i(Game.getVar(R.string.Potion_FoodRefreshed), rfood.name());

		owner.collect(rfood.purify());
	}

	@Override
	protected void moistenArrow(Arrow arrow, Char owner) {
		int quantity = reallyMoistArrows(arrow);

		CommonArrow moistenArrows = new CommonArrow(quantity);
		owner.collect(moistenArrows);
	}
}
