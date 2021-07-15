package com.nyrds.pixeldungeon.items.common.armor;

import com.nyrds.pixeldungeon.ml.R;
import com.nyrds.platform.game.Game;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.hero.HeroClass;
import com.watabou.pixeldungeon.items.armor.Armor;
import com.watabou.pixeldungeon.utils.GLog;

import org.jetbrains.annotations.NotNull;

public class NecromancerRobe extends Armor {

	public String desc() {
		return info2;
	}

	public NecromancerRobe() {
		super( 1 );
		image = 23;
	}

	@Override
	public boolean doEquip(@NotNull Char hero ) {
		if (hero.getHeroClass() == HeroClass.NECROMANCER) {
			return super.doEquip( hero );
		} else {
			GLog.w( Game.getVar(R.string.NecromancerArmor_NotNecromancer) );
			return false;
		}
	}

}