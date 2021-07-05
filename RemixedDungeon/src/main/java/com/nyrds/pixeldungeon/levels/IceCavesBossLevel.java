package com.nyrds.pixeldungeon.levels;

import com.nyrds.pixeldungeon.ai.Hunting;
import com.nyrds.pixeldungeon.ai.MobAi;
import com.nyrds.pixeldungeon.ml.R;
import com.nyrds.pixeldungeon.mobs.icecaves.IceGuardian;
import com.nyrds.platform.game.Game;
import com.watabou.noosa.Scene;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.actors.mobs.Bestiary;
import com.watabou.pixeldungeon.actors.mobs.Mob;
import com.watabou.pixeldungeon.levels.BossLevel;
import com.watabou.pixeldungeon.levels.Terrain;
import com.watabou.pixeldungeon.levels.painters.Painter;
import com.watabou.pixeldungeon.utils.Utils;
import com.watabou.utils.Random;

public class IceCavesBossLevel extends BossLevel {
	
	{
		color1 = 0x4b6636;
		color2 = 0xf2f2f2;
	}
	
	private static final int TOP			= 2;
	private static final int HALL_WIDTH		= 11;
	private static final int HALL_HEIGHT	= 9;
	private static final int CHAMBER_HEIGHT	= 1;
	

	@Override
	public String tilesTex() {
		return Assets.TILES_ICE_CAVES_X;
	}
	
	@Override
	public String waterTex() {
		return Assets.WATER_ICE_CAVES;
	}

	@Override
	protected boolean build() {

		Painter.fill( this, _Left(), TOP, HALL_WIDTH, HALL_HEIGHT, Terrain.EMPTY_SP );
		Painter.fill( this, _Left(), TOP, HALL_WIDTH, TOP, Terrain.EMPTY );
		Painter.fill( this, _Left(), HALL_HEIGHT, HALL_WIDTH, TOP, Terrain.EMPTY );
		for (int i = 0; i < 10; i++) {
			map[getRandomTerrainCell(Terrain.EMPTY_SP)] = Terrain.STATUE_SP;
		}
		
		arenaDoor = (TOP + HALL_HEIGHT) * getWidth() + _Center();
		map[arenaDoor] = Terrain.DOOR;
		
		Painter.fill( this, _Left(), TOP + HALL_HEIGHT + 1, HALL_WIDTH, CHAMBER_HEIGHT, Terrain.EMPTY );
		
		entrance = (TOP + HALL_HEIGHT + 2 + Random.Int( CHAMBER_HEIGHT - 1 )) * getWidth() + _Left() + (/*1 +*/ Random.Int( HALL_WIDTH-2 )); 
		map[entrance] = Terrain.ENTRANCE;
		setExit((TOP - 1) * getWidth() + _Center(),0);
		map[getExit(0)] = Terrain.LOCKED_EXIT;
		return true;
	}
	
	@Override
	protected void decorate() {	
		
		for (int i=0; i < getLength(); i++) {
			if (map[i] == Terrain.EMPTY && Random.Int( 10 ) == 0) { 
				map[i] = Terrain.EMPTY_DECO;
			} else if (map[i] == Terrain.WALL && Random.Int( 8 ) == 0) { 
				map[i] = Terrain.WALL_DECO;
			}
		}
	}
	
	public int pedestal( boolean left ) {
		if (left) {
			return (TOP + HALL_HEIGHT / 2) * getWidth() + _Center() - 2;
		} else {
			return (TOP + HALL_HEIGHT / 2) * getWidth() + _Center() + 2;
		}
	}

	@Override
	public void pressHero(int cell, Hero hero ) {
		
		super.pressHero( cell, hero );
		
		if (!enteredArena && outsideEntranceRoom( cell ) ) {
			
			enteredArena = true;
			
			Mob boss = Bestiary.mob(this);
			Mob guard = new IceGuardian();

			Mob mob = boss;

			for (int i = 0; i < 2; i++){
				mob.setState(MobAi.getStateByClass(Hunting.class));
				do {
					mob.setPos(Random.Int( getLength() ));
				} while (
						!passable[mob.getPos()] ||
								!outsideEntranceRoom( mob.getPos() ) ||
								Dungeon.visible[mob.getPos()]);
				spawnMob(mob);
				mob = guard;
			}

			seal();
		}
	}

	private boolean outsideEntranceRoom(int cell ) {
		return cell / getWidth() < arenaDoor / getWidth();
	}

	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.GRASS:
				return Game.getVar(R.string.IceCaves_TileGrass);
			case Terrain.HIGH_GRASS:
				return Game.getVar(R.string.IceCaves_TileHighGrass);
			case Terrain.WATER:
				return Game.getVar(R.string.Caves_TileWater);
			case Terrain.STATUE:
			case Terrain.STATUE_SP:
				return Game.getVar(R.string.IceCaves_TileStatue);
			case Terrain.UNLOCKED_EXIT:
			case Terrain.LOCKED_EXIT:
				return Game.getVar(R.string.PortalGate_Name);
			default:
				return super.tileName( tile );
		}
	}

	@Override
	public String tileDesc( int tile ) {
		switch (tile) {
			case Terrain.ENTRANCE:
				return Game.getVar(R.string.Caves_TileDescEntrance);
			case Terrain.EXIT:
				return Game.getVar(R.string.Caves_TileDescExit);
			case Terrain.HIGH_GRASS:
				return Game.getVar(R.string.IceCaves_TileDescHighGrass);
			case Terrain.WALL_DECO:
				return Game.getVar(R.string.IceCaves_TileDescDeco);
			case Terrain.BOOKSHELF:
				return Game.getVar(R.string.Caves_TileDescBookshelf);
			case Terrain.STATUE:
			case Terrain.STATUE_SP:
				return Game.getVar(R.string.IceCaves_TileDescStatue);
			case Terrain.UNLOCKED_EXIT:
				return Utils.format(Game.getVar(R.string.PortalExit_Desc), Game.getVar(R.string.PortalExit_Desc_IceCaves));
			default:
				return super.tileDesc( tile );
		}
	}

	@Override
	public void addVisuals( Scene scene ) {
		IceCavesLevel.addVisuals( this, scene );
	}

	private int _Left() {
		return (getWidth() - HALL_WIDTH) / 2;
	}

	private int _Center() {
		return _Left() + HALL_WIDTH / 2;
	}
}
