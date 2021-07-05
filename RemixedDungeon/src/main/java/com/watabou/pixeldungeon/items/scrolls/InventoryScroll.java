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

import com.nyrds.pixeldungeon.ml.R;
import com.nyrds.platform.game.Game;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.windows.WndBag;
import com.watabou.pixeldungeon.windows.WndOptions;

import org.jetbrains.annotations.NotNull;

public abstract class InventoryScroll extends Scroll {

	protected String inventoryTitle = Game.getVar(R.string.InventoryScroll_Title);
	protected WndBag.Mode mode = WndBag.Mode.ALL;

	private Char reader;

	@Override
	protected void doRead(@NotNull Char reader) {
		
		if (!isKnown()) {
			setKnown();
			identifiedByUse = true;
		} else {
			identifiedByUse = false;
		}

		this.reader = reader;

		GameScene.selectItem(reader, new ScrollUse(this).invoke(), mode, inventoryTitle);
	}
	
	void confirmCancellation() {
		GameScene.show( new WndOptions( name(),
										Game.getVar(R.string.InventoryScroll_Warning),
										Game.getVar(R.string.InventoryScroll_Yes),
										Game.getVar(R.string.InventoryScroll_No) ) {
			@Override
			public void onSelect(int index) {
				switch (index) {
				case 0:
					getOwner().spendAndNext( TIME_TO_READ );
					identifiedByUse = false;
					break;
				case 1:
					GameScene.selectItem(reader, new ScrollUse(InventoryScroll.this).invoke(), mode, inventoryTitle);
					break;
				}
			}
			public void onBackPressed() {}
		} );
	}

	protected abstract void onItemSelected(Item item, Char selector);

	protected static boolean identifiedByUse = false;

}
