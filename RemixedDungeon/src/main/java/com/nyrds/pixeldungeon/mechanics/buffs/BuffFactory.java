package com.nyrds.pixeldungeon.mechanics.buffs;

import com.nyrds.pixeldungeon.items.common.RatKingCrown;
import com.nyrds.pixeldungeon.items.common.rings.RingOfFrost;
import com.nyrds.pixeldungeon.items.guts.HeartOfDarkness;
import com.nyrds.util.ModError;
import com.nyrds.util.ModdingMode;
import com.watabou.pixeldungeon.actors.buffs.Amok;
import com.watabou.pixeldungeon.actors.buffs.Awareness;
import com.watabou.pixeldungeon.actors.buffs.Barkskin;
import com.watabou.pixeldungeon.actors.buffs.Bleeding;
import com.watabou.pixeldungeon.actors.buffs.Blessed;
import com.watabou.pixeldungeon.actors.buffs.Blindness;
import com.watabou.pixeldungeon.actors.buffs.Buff;
import com.watabou.pixeldungeon.actors.buffs.Burning;
import com.watabou.pixeldungeon.actors.buffs.CandleOfMindVisionBuff;
import com.watabou.pixeldungeon.actors.buffs.Charm;
import com.watabou.pixeldungeon.actors.buffs.Combo;
import com.watabou.pixeldungeon.actors.buffs.Cripple;
import com.watabou.pixeldungeon.actors.buffs.Frost;
import com.watabou.pixeldungeon.actors.buffs.Fury;
import com.watabou.pixeldungeon.actors.buffs.Hunger;
import com.watabou.pixeldungeon.actors.buffs.Invisibility;
import com.watabou.pixeldungeon.actors.buffs.Levitation;
import com.watabou.pixeldungeon.actors.buffs.Light;
import com.watabou.pixeldungeon.actors.buffs.MindVision;
import com.watabou.pixeldungeon.actors.buffs.Ooze;
import com.watabou.pixeldungeon.actors.buffs.Paralysis;
import com.watabou.pixeldungeon.actors.buffs.Poison;
import com.watabou.pixeldungeon.actors.buffs.Regeneration;
import com.watabou.pixeldungeon.actors.buffs.Roots;
import com.watabou.pixeldungeon.actors.buffs.Shadows;
import com.watabou.pixeldungeon.actors.buffs.Sleep;
import com.watabou.pixeldungeon.actors.buffs.Slow;
import com.watabou.pixeldungeon.actors.buffs.SnipersMark;
import com.watabou.pixeldungeon.actors.buffs.Speed;
import com.watabou.pixeldungeon.actors.buffs.Stun;
import com.watabou.pixeldungeon.actors.buffs.Terror;
import com.watabou.pixeldungeon.actors.buffs.Vertigo;
import com.watabou.pixeldungeon.actors.buffs.Weakness;
import com.watabou.pixeldungeon.items.armor.glyphs.Viscosity;
import com.watabou.pixeldungeon.items.quest.DriedRose;
import com.watabou.pixeldungeon.items.quest.RatSkull;
import com.watabou.pixeldungeon.items.rings.RingOfStoneWalking;
import com.watabou.pixeldungeon.plants.Earthroot;
import com.watabou.pixeldungeon.plants.Sungrass;
import com.watabou.pixeldungeon.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.SneakyThrows;
import lombok.var;

public class BuffFactory {
    static private final Map<String, Class<? extends Buff>> buffList = new HashMap<>();

    static private final Set<String> customBuffs = new HashSet<>();

    public static final String GASES_IMMUNITY = "GasesImmunity";

    static {
        initBuffsMap();

        for(String buffFile: ModdingMode.listResources("scripts/buffs", (dir, name) -> name.endsWith(".lua"))) {
            customBuffs.add(buffFile.replace(".lua", Utils.EMPTY_STRING));
        }
    }

    private static void registerBuffClass(Class<? extends Buff> buffClass) {
        buffList.put(buffClass.getSimpleName(), buffClass);
    }

    private static void initBuffsMap() {
        registerBuffClass(Burning.class);
        registerBuffClass(Viscosity.DeferedDamage.class);
        registerBuffClass(Barkskin.class);
        registerBuffClass(Earthroot.Armor.class);
        registerBuffClass(Poison.class);
        registerBuffClass(Fury.class);
        registerBuffClass(Combo.class);
        registerBuffClass(Ooze.class);
        registerBuffClass(Hunger.class);
        //registerBuffClass(Charger.class);
        registerBuffClass(Regeneration.class);
        registerBuffClass(Necrotism.class);
        registerBuffClass(Bleeding.class);
        registerBuffClass(Sungrass.Health.class);
        registerBuffClass(Charm.class);
        registerBuffClass(Frost.class);
        registerBuffClass(Sleep.class);
        registerBuffClass(Levitation.class);
        registerBuffClass(MindVision.class);
        registerBuffClass(CandleOfMindVisionBuff.class);
        registerBuffClass(Blindness.class);
        registerBuffClass(Vertigo.class);
        registerBuffClass(Stun.class);
		registerBuffClass(Paralysis.class);
        registerBuffClass(Terror.class);
        registerBuffClass(Weakness.class);
        registerBuffClass(Light.class);
        registerBuffClass(Invisibility.class);
        registerBuffClass(Shadows.class);
        registerBuffClass(Speed.class);
        registerBuffClass(Cripple.class);
        registerBuffClass(Awareness.class);
        registerBuffClass(SnipersMark.class);
        registerBuffClass(Blessed.class);
        registerBuffClass(Slow.class);
        registerBuffClass(Roots.class);
        registerBuffClass(Amok.class);
        registerBuffClass(HeartOfDarkness.HeartOfDarknessBuff.class);
        registerBuffClass(DriedRose.OneWayCursedLoveBuff.class);
        registerBuffClass(RatKingCrown.RatKingAuraBuff.class);
        registerBuffClass(RingOfStoneWalking.StoneWalking.class);
        registerBuffClass(RingOfFrost.FrostAura.class);
        registerBuffClass(RatSkull.RatterAura.class);
        registerBuffClass(RageBuff.class);
        registerBuffClass(DriedRose.OneWayCursedLoveBuff.class);
        //registerBuffClass(RingOfHaggler.Haggling.class);
        //registerBuffClass(RingOfPower.Power.class);
        //registerBuffClass(RingOfSatiety.Satiety.class);
        //registerBuffClass(RingOfHaste.Haste.class);
        //registerBuffClass(RingOfMending.Rejuvenation.class);
        //registerBuffClass(RingOfHerbalism.Herbalism.class);
        //registerBuffClass(RingOfEvasion.Evasion.class);
        //registerBuffClass(RingOfThorns.Thorns.class);
        //registerBuffClass(RingOfDetection.Detection.class);
        //registerBuffClass(RingOfElements.Resistance.class);
        //registerBuffClass(RingOfShadows.Shadows.class);


    }

    private static boolean hasBuffForName(String name) {
        if(customBuffs.contains(name)) {
            return true;
        }

        if (buffList.get(name) != null) {
            return true;
        }

        return false;
    }

    public static Set<String> getAllBuffsNames() {
        var ret = new HashSet<String>();

        ret.addAll(customBuffs);
        ret.addAll(buffList.keySet());

        return ret;
    }

    @NotNull
    @SneakyThrows
    public static Buff getBuffByName(String name) {
        if(hasBuffForName(name)) {
            Class<? extends Buff> buffClass = buffList.get(name);
            if (buffClass == null) {
                return new CustomBuff(name);
            }
            return buffClass.newInstance();
        }

        throw new ModError(name, new Exception("Unknown Buff:"+name));
    }
}
