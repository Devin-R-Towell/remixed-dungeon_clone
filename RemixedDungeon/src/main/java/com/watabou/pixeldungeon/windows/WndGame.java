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
package com.watabou.pixeldungeon.windows;

import com.nyrds.pixeldungeon.game.GameLoop;
import com.nyrds.pixeldungeon.ml.R;
import com.nyrds.pixeldungeon.utils.GameControl;
import com.nyrds.platform.game.Game;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.actors.hero.HeroClass;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.scenes.RankingsScene;
import com.watabou.pixeldungeon.scenes.TitleScene;
import com.watabou.pixeldungeon.ui.Icons;

public class WndGame extends WndMenuCommon {

	@Override
	protected void createItems() {

		Hero hero = Dungeon.hero;

		if (hero == null) {
			GameLoop.switchScene(TitleScene.class);
			return;
		}

		menuItems.add( new MenuButton(Game
                .getVar(R.string.WndGame_Settings)) {
			@Override
			protected void onClick() {
				hide();
				GameScene.show( new WndSettingsInGame() );
			}
		} );


		final int difficulty = hero.getDifficulty();

		if(difficulty < 2 && hero.isAlive()) {
			menuItems.add( new MenuButton( Game.getVar(R.string.WndGame_Save) ) {
				@Override
				protected void onClick() {
					GameScene.show(new WndSaveSlotSelect(true,Game.getVar(R.string.WndSaveSlotSelect_SelectSlot)));
				}
			} );
		}

		if(difficulty < 2) {
			menuItems.add( new MenuButton( Game.getVar(R.string.WndGame_Load) ) {
				@Override
				protected void onClick() {
					GameScene.show(new WndSaveSlotSelect(false,Game.getVar(R.string.WndSaveSlotSelect_SelectSlot)));
				}
			} );
		}

		final int challenges = Dungeon.getChallenges();

		if (challenges > 0) {
			menuItems.add( new MenuButton(Game
                    .getVar(R.string.WndGame_Challenges)) {
				@Override
				protected void onClick() {
					hide();
					GameScene.show( new WndChallenges(challenges, false ) );
				}
			} );
		}

		if (!hero.isAlive()) {

			final HeroClass heroClass = hero.getHeroClass();

			menuItems.add(new MenuButton(Game.getVar(R.string.WndGame_Start),Icons.get(heroClass) ) {
				@Override
				protected void onClick() {
					GameControl.startNewGame(heroClass.name(), difficulty, false);
				}
			});

			menuItems.add( new MenuButton(Game
                    .getVar(R.string.WndGame_Ranking)) {
				@Override
				protected void onClick() {
					GameLoop.switchScene( RankingsScene.class );
				}
			} );
		}

		menuItems.add( new MenuButton(Game.getVar(R.string.WndGame_menu)) {
			@Override
			protected void onClick() {
				Dungeon.save(false);
				GameLoop.switchScene( TitleScene.class );
			}
		} );

		menuItems.add( new MenuButton(Game.getVar(R.string.WndGame_Exit)) {
			@Override
			protected void onClick() {
				Game.shutdown();
			}
		} );

		menuItems.add( new MenuButton(Game
                .getVar(R.string.WndGame_Return)) {
			@Override
			protected void onClick() {
				hide();
			}
		} );
	}
}
