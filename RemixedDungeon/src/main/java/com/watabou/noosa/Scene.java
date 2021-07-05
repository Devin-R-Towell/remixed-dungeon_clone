/*
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

package com.watabou.noosa;

import com.nyrds.LuaInterface;
import com.nyrds.pixeldungeon.mechanics.LuaScript;
import com.nyrds.platform.game.Game;
import com.nyrds.platform.input.Keys;
import com.watabou.pixeldungeon.ui.Window;
import com.watabou.utils.Signal;

import java.util.ArrayList;

public class Scene extends Group {
	
	private Signal.Listener<Keys.Key> keyListener;
	private ArrayList<Window> activeWindows = new ArrayList<>();

	static protected LuaScript script = new LuaScript("scripts/services/scene", null);

	public void create() {
		Keys.event.add( keyListener = key -> {
			if (Game.instance() != null && key.pressed) {
				switch (key.code) {
				case Keys.BACK:
					onBackPressed();
					break;
				case Keys.MENU:
					onMenuPressed();
					break;
				}
			}
		});
	}

	@LuaInterface
	public Window getWindow(int i) {
		if(i < activeWindows.size()) {
			return activeWindows.get(i);
		}

		return null;
	}

	@Override
	public void update() {
		activeWindows.clear();
		int windowIndex = -1;
		while((windowIndex = findByClass(Window.class, windowIndex+1))>0) {
			activeWindows.add((Window)getMember(windowIndex));
		}
		script.runOptionalNoRet("onStep", this.getClass().getSimpleName());

		super.update();
	}


	@Override
	public void destroy() {
		Keys.event.remove( keyListener );
		super.destroy();
	}
	
	public void pause() {
	}
	
	public void resume() {	
	}
	
	@Override
	public Camera camera() {
		return Camera.main;
	}
	
	protected void onBackPressed() {
		Game.shutdown();
	}
	
	protected void onMenuPressed() {
	}

	public static void setMode(String mode) {
		script.run("setMode", mode);
	}
}
