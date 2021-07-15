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
package com.watabou.pixeldungeon.levels;

import com.nyrds.pixeldungeon.ml.R;
import com.nyrds.platform.game.Game;
import com.watabou.noosa.Scene;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.levels.Room.Type;
import com.watabou.utils.Graph;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;

import lombok.var;

public class SewerBossLevel extends BossLevel {

	{
		color1 = 0x48763c;
		color2 = 0x59994a;
	}

    @Override
	public String tilesTex() {
		return Assets.TILES_SEWERS;
	}
	
	@Override
	public String tilesTexEx() {
		return Assets.TILES_SEWERS_X;
	}
	
	@Override
	public String waterTex() {
		return Assets.WATER_SEWERS;
	}
	
	@Override
	protected boolean build() {

		if(!initRooms()){
			return false;
		}
	
		int distance;
		int retry = 0;
		int minDistance = (int)Math.sqrt( rooms.size() );
		do {
			int innerRetry = 0;
			do {
				if (innerRetry++ > 10) {
					return false;
				}
				roomEntrance = Random.element( rooms );
			} while (roomEntrance.width() < 4 || roomEntrance.height() < 4);
			
			innerRetry = 0;
			do {
				if (innerRetry++ > 10) {
					return false;
				}
				setRoomExit(Random.element( rooms ));
			} while (getRoomExit() == roomEntrance || getRoomExit().width() < 6 || getRoomExit().height() < 6 || getRoomExit().top == 0);
	
			Graph.buildDistanceMap( rooms, getRoomExit());
			distance = roomEntrance.distance();
			
			if (retry++ > 10) {
				return false;
			}
			
		} while (distance < minDistance);
		
		roomEntrance.type = Type.ENTRANCE;
		getRoomExit().type = Type.SEWER_BOSS_EXIT;

		placeSecondaryExits();

		buildPath(roomEntrance, getRoomExit());

		var ignoredRooms = new HashSet<Room.Type>();
		ignoredRooms.add(Type.NULL);

		connectRooms(ignoredRooms);

		for (Room r : rooms) {
			if (r.type == Type.NULL && r.connected.size() > 0) {
				r.type = Type.TUNNEL;
			}
		}

		placeKingRoom();

		paint();
		
		paintWater();
		
		paintGrass();
		
		placeTraps();
		
		return true;
	}

	private void placeKingRoom() {
		ArrayList<Room> candidates = new ArrayList<>();
		for (Room r : getRoomExit().neighbours) {
			if (!getRoomExit().connected.containsKey( r ) &&
				(getRoomExit().left == r.right || getRoomExit().right == r.left || getRoomExit().bottom == r.top) &&
				!(r.type == Type.EXIT)) {
				candidates.add( r );
			}
		}

		if (!candidates.isEmpty()) {
			Room kingsRoom = Random.element( candidates );
			kingsRoom.connect(getRoomExit());
			kingsRoom.type = Type.RAT_KING;
		}
	}

	protected boolean[] water() {
		return Patch.generate(this, 0.5f, 5 );
	}
	
	protected boolean[] grass() {
		return Patch.generate(this, 0.40f, 4 );
	}
	
	@Override
	protected void decorate() {	
		int start = getRoomExit().top * getWidth() + getRoomExit().left + 1;
		int end = start + getRoomExit().width() - 1;
		for (int i=start; i < end; i++) {
			if (map[i]==Terrain.WALL) {
				map[i] = Terrain.WALL_DECO;
				map[i + getWidth()] = Terrain.WATER;
			} else {
				map[i + getWidth()] = Terrain.EMPTY;
			}
		}

		placeEntranceSign();
	}
	
	@Override
	public void addVisuals( Scene scene ) {
		SewerLevel.addVisuals( this, scene );
	}


	@Override
	protected void pressHero(int cell, Hero ch) {

		super.pressHero( cell, ch );

		if (!enteredArena && getRoomExit().inside( cell )) {
			enteredArena = true;
			spawnBoss(getEmptyCellFromRoom(getRoomExit()));
		}
	}

	@Override
	public String tileName( int tile ) {
		switch (tile) {
		case Terrain.WATER:
			return Game.getVar(R.string.Sewer_TileWater);
		default:
			return super.tileName( tile );
		}
	}
	
	@Override
	public String tileDesc(int tile) {
		switch (tile) {
		case Terrain.EMPTY_DECO:
			return Game.getVar(R.string.Sewer_TileDescDeco);
		default:
			return super.tileDesc( tile );
		}
	}
}
