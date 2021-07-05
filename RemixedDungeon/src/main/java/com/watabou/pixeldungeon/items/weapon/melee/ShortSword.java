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
package com.watabou.pixeldungeon.items.weapon.melee;

import com.nyrds.pixeldungeon.items.ItemUtils;
import com.nyrds.pixeldungeon.ml.R;
import com.nyrds.platform.audio.Sample;
import com.nyrds.platform.game.Game;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Badges;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.hero.Belongings.Slot;
import com.watabou.pixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.watabou.pixeldungeon.items.weapon.missiles.Boomerang;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.pixeldungeon.ui.QuickSlot;
import com.watabou.pixeldungeon.utils.GLog;
import com.watabou.pixeldungeon.windows.WndBag;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ShortSword extends MeleeWeapon {

	public static final String AC_REFORGE = "ShortSword_ACReforge";
	
	private static final float TIME_TO_REFORGE	= 2f;

	{
		image = ItemSpriteSheet.SHORT_SWORD;
	}
	
	public ShortSword() {
		super( 1, 1f, 1f );
		animation_class = SWORD_ATTACK;
		
		STR = 11;
		MAX = 12;
	}
	
	@Override
	public ArrayList<String> actions(Char hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (level() > 0) {
			actions.add( AC_REFORGE );
		}
		return actions;
	}
	
	@Override
	public void _execute(@NotNull Char chr, @NotNull String action ) {
		if (action.equals(AC_REFORGE)) {
			chr.getBelongings().setSelectedItem(this);
			GameScene.selectItem(chr, itemSelector, WndBag.Mode.WEAPON, Game.getVar(R.string.ShortSword_Select));
		} else {
			super._execute(chr, action );
		}
	}
	
	@Override
	public String desc() {
		return Game.getVar(R.string.ShortSword_Info);
	}
	
	private final WndBag.Listener itemSelector = (item, selector) -> {
		if (item != null && !(item instanceof Boomerang)) {
			selector.getBelongings().removeItem(this);
			Sample.INSTANCE.play( Assets.SND_EVOKE );
			ScrollOfUpgrade.upgrade( selector );
			ItemUtils.evoke( selector );

			GLog.w( Game.getVar(R.string.ShortSword_Reforged), item.name() );

			((MeleeWeapon)item).safeUpgrade();
			selector.spendAndNext( TIME_TO_REFORGE );

			Badges.validateItemLevelAcquired( item );

		} else {
			if (item instanceof Boomerang) {
				GLog.w( Game.getVar(R.string.ShortSword_NotBoomerang) );
			}

			if (equipedTo != Slot.NONE) {
				selector.getBelongings().equip(ShortSword.this, equipedTo);
			} else {
				collect( selector.getBelongings().backpack );
			}
		}
		selector.updateSprite();
		QuickSlot.refresh(selector);
	};
}
