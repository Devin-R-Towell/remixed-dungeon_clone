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

import com.nyrds.pixeldungeon.items.chaos.ChaosCrystal;
import com.nyrds.pixeldungeon.mechanics.NamedEntityKind;
import com.nyrds.pixeldungeon.ml.R;
import com.nyrds.platform.audio.Sample;
import com.nyrds.platform.game.Game;
import com.watabou.noosa.Camera;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Badges;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.Actor;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.CharUtils;
import com.watabou.pixeldungeon.actors.blobs.Blob;
import com.watabou.pixeldungeon.actors.blobs.ToxicGas;
import com.watabou.pixeldungeon.actors.buffs.Bleeding;
import com.watabou.pixeldungeon.actors.buffs.Buff;
import com.watabou.pixeldungeon.actors.buffs.Stun;
import com.watabou.pixeldungeon.effects.CellEmitter;
import com.watabou.pixeldungeon.effects.Speck;
import com.watabou.pixeldungeon.effects.particles.ElmoParticle;
import com.watabou.pixeldungeon.items.keys.SkeletonKey;
import com.watabou.pixeldungeon.items.rings.RingOfThorns;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.pixeldungeon.levels.Terrain;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.sprites.DM300Sprite;
import com.watabou.pixeldungeon.utils.GLog;
import com.watabou.utils.Random;

public class DM300 extends Boss {
	
	public DM300() {
		spriteClass = DM300Sprite.class;
		
		hp(ht(200));
		exp = 30;
		baseDefenseSkill = 18;
		baseAttackSkill  = 28;
		dmgMin = 18;
		dmgMax = 24;
		dr = 10;

		float dice = Random.Float();
		if( dice < 0.5 ) {
			loot(new ChaosCrystal(), 0.333f);
		} else {
			loot(new RingOfThorns().random(), 0.333f);
		}

		addImmunity( ToxicGas.class );
		addImmunity( Bleeding.class );

		collect(new SkeletonKey());
	}

	@Override
	public boolean act() {

		GameScene.add( Blob.seed( getPos(), 30, ToxicGas.class ) );
		
		return super.act();
	}
	
	@Override
	public void move( int step ) {
		super.move( step );

		if (level().map[step] == Terrain.INACTIVE_TRAP && hp() < ht()) {

			heal(Random.Int( 1, ht() - hp() ), this, true);

			getSprite().emitter().burst( ElmoParticle.FACTORY, 5 );
			
			if (CharUtils.isVisible(this) && Dungeon.hero.isAlive()) {
				GLog.n(Game.getVar(R.string.DM300_Info1));
			}
		}
		
		int cell = step + Level.NEIGHBOURS8[Random.Int( Level.NEIGHBOURS8.length )];
		
		if (CharUtils.isVisible(this)) {
			CellEmitter.get( cell ).start( Speck.factory( Speck.ROCK ), 0.07f, 10 );
			Camera.main.shake( 3, 0.7f );
			Sample.INSTANCE.play( Assets.SND_ROCKS );

			if (level().water[cell]) {
				GameScene.ripple( cell );
			} else if (level().map[cell] == Terrain.EMPTY) {
				level().set( cell, Terrain.EMPTY_DECO );
				GameScene.updateMap( cell );
			}
		}

		Char ch = Actor.findChar( cell );
		if (ch != null && ch != this) {
			Buff.prolong( ch, Stun.class, 2 );
		}
	}
	
	@Override
	public void die(NamedEntityKind cause) {
		super.die( cause );

		Badges.validateBossSlain(Badges.Badge.BOSS_SLAIN_3);
		
		yell(Game.getVar(R.string.DM300_Info2));
	}
	
	@Override
	public void notice() {
		super.notice();
		yell(Game.getVar(R.string.DM300_Info3));
	}
}
