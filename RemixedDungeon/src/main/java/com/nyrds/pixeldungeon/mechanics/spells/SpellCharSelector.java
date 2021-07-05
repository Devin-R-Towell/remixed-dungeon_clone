package com.nyrds.pixeldungeon.mechanics.spells;

import com.nyrds.pixeldungeon.ml.R;
import com.nyrds.platform.game.Game;
import com.watabou.noosa.Image;
import com.watabou.pixeldungeon.DungeonTilemap;
import com.watabou.pixeldungeon.actors.Actor;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.pixeldungeon.scenes.CellSelector;
import com.watabou.pixeldungeon.ui.Icons;
import com.watabou.pixeldungeon.utils.GLog;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

import lombok.var;

class SpellCharSelector implements CellSelector.Listener {
    private final Spell spell;
    private @NotNull
    final Char caster;

    private final Set<Image> markers = new HashSet<>();

    public SpellCharSelector(Spell spell, @NotNull Char caster, String mode) {
        this.spell = spell;
        this.caster = caster;

        Level level = caster.level();

        for(Char chr: Actor.chars.values()) {

            if(mode.equals(SpellHelper.TARGET_CHAR_NOT_SELF) && chr == caster) {
                continue;
            }

            int pos = chr.getPos();

            if(level.fieldOfView[pos]) {
                GLog.debug("%s: visible: %s", spell.getEntityKind(), chr.getEntityKind());

                var marker = Icons.TARGET.get();
                chr.getSprite().getParent().add(marker);

                marker.point(DungeonTilemap.tileToWorld(pos));
                markers.add(marker);
            }
        }
    }

    @Override
    public void onSelect(Integer cell, @NotNull Char selector) {
        if (cell != null) {
            Char target = Actor.findChar(cell);

            if(target!=null) {

                spell.cast(caster, target);

                caster.spend(spell.castTime);
                caster.busy();
                caster.getSprite().zap(target.getPos());
            }
        }

        for(var marker: markers) {
            marker.killAndErase();
        }
    }

    @Override
    public String prompt() {
        return Game.getVar(R.string.Spell_SelectAChar);
    }

    @Override
    public Image icon() {
        return spell.image(caster);
    }
}
