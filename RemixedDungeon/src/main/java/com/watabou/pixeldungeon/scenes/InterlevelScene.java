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
package com.watabou.pixeldungeon.scenes;

import com.nyrds.pixeldungeon.game.GameLoop;
import com.nyrds.pixeldungeon.ml.R;
import com.nyrds.pixeldungeon.utils.CharsList;
import com.nyrds.pixeldungeon.utils.DungeonGenerator;
import com.nyrds.pixeldungeon.utils.Position;
import com.nyrds.platform.EventCollector;
import com.nyrds.platform.audio.Music;
import com.nyrds.platform.game.Game;
import com.nyrds.util.GuiProperties;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Text;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.SaveUtils;
import com.watabou.pixeldungeon.actors.Actor;
import com.watabou.pixeldungeon.actors.mobs.Mob;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.pixeldungeon.utils.Utils;
import com.watabou.pixeldungeon.windows.WndError;
import com.watabou.pixeldungeon.windows.WndStory;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.FutureTask;

import lombok.SneakyThrows;

public class InterlevelScene extends PixelScene {

    private static final float TIME_TO_FADE = 0.3f;
    private static boolean rescueMode = false;

    public enum Mode {
        DESCEND, ASCEND, CONTINUE, RESURRECT, RETURN, FALL
    }

    public static Mode mode;

    public static Position returnTo;

    public static boolean noStory = false;

    public static boolean fallIntoPit;

    private enum Phase {
        FADE_IN, STATIC, FADE_OUT, ERROR
    }

    private Phase phase;
    private float timeLeft;

    private Text message;

    private FutureTask<Boolean> levelChanger;

    volatile private String error = null;

    class LevelChanger implements Runnable {

        @SneakyThrows
        @Override
        public void run() {
            switch (mode) {
                case DESCEND:
                    descend();
                    break;
                case ASCEND:
                    ascend();
                    break;
                case CONTINUE:
                    restoreAtPosition(null);
                    break;
                case RESURRECT:
                    resurrect();
                    break;
                case RETURN:
                    returnTo();
                    break;
                case FALL:
                    fall();
                    break;
            }
       }
    }

    static public void Do(InterlevelScene.Mode mode) {

        if(GameLoop.scene() instanceof GameScene && Dungeon.level!=null && Dungeon.hero!=null) { // not game start
            Dungeon.hero.getSprite().completeForce();
            for(Mob mob:Dungeon.level.getCopyOfMobsArray()) {
                mob.getSprite().completeForce();
            }
        }

        InterlevelScene.mode = mode;
        GameLoop.switchScene(InterlevelScene.class);
    }

    @Override
    public void create() {
        super.create();

        String text = Utils.EMPTY_STRING;
        switch (mode) {
            case DESCEND:
                text = Game.getVar(R.string.InterLevelScene_Descending);
                break;
            case ASCEND:
                text = Game.getVar(R.string.InterLevelScene_Ascending);
                break;
            case CONTINUE:
                text = Game.getVar(R.string.InterLevelScene_Loading);
                break;
            case RESURRECT:
                text = Game.getVar(R.string.InterLevelScene_Resurrecting);
                break;
            case RETURN:
                text = Game.getVar(R.string.InterLevelScene_Returning);
                break;
            case FALL:
                text = Game.getVar(R.string.InterLevelScene_Falling);
                break;
        }

        message = PixelScene.createText(text, GuiProperties.titleFontSize());
        message.x = (Camera.main.width - message.width()) / 2;
        message.y = (Camera.main.height - message.height()) / 2;
        add(message);

        phase = Phase.FADE_IN;
        timeLeft = TIME_TO_FADE;

        levelChanger = new FutureTask<>(new LevelChanger(), true);
        GameLoop.execute(levelChanger);
    }

    @Override
    public void update() {
        super.update();

        if(phase == Phase.ERROR) {
            return;
        }

        float p = timeLeft / TIME_TO_FADE;

        if (error != null) {
            add(new WndError(error) {
                public void onBackPressed() {
                    super.onBackPressed();
                    GameLoop.switchScene(TitleScene.class);
                }
            });
            phase = Phase.ERROR;
            error = null;
        }

        switch (phase) {

            case FADE_IN:
                message.alpha(1 - p);
                if ((timeLeft -= GameLoop.elapsed) <= 0) {
                    if (error == null && levelChanger.isDone()) {
                        phase = Phase.FADE_OUT;
                        timeLeft = TIME_TO_FADE;
                    } else {
                        phase = Phase.STATIC;
                    }
                }
                break;

            case FADE_OUT:
                message.alpha(p);

                if (mode == Mode.CONTINUE
                        || (mode == Mode.DESCEND && Dungeon.depth == 1)) {
                    Music.INSTANCE.volume(p);
                }
                if ((timeLeft -= GameLoop.elapsed) <= 0) {
                    GameLoop.switchScene(GameScene.class);
                }
                break;

            case STATIC:
                if (levelChanger.isDone()) {
                    phase = Phase.FADE_OUT;
                }
                break;
        }
    }

    private void descend() {
        Actor.fixTime();

        Collection<Mob> followers = CharsList.emptyMobList;

        if (Dungeon.hero == null) {
            Dungeon.level = null;
            Dungeon.init();
            if (noStory) {
                Dungeon.chapters.add(WndStory.ID_SEWERS);
                noStory = false;
            }
        } else {
            Dungeon.onHeroLeaveLevel();
            followers = Level.mobsFollowLevelChange(Mode.DESCEND);
            Dungeon.save(false);
        }

        Position next;
        Position thisPosition = Dungeon.currentPosition();
        Level newLevel;

        next = DungeonGenerator.descend(thisPosition);

        if(next == null) {
            restoreAtPosition(thisPosition);
            return;
        }

        Dungeon.depth = DungeonGenerator.getLevelDepth(next.levelId);
        newLevel = Dungeon.loadLevel(next);

        Dungeon.switchLevel(newLevel,
                newLevel.entrance,
                followers);
    }

    private void fall() {

        Actor.fixTime();

        Dungeon.hero._stepBack();
        Dungeon.onHeroLeaveLevel();
        Dungeon.save(false); // for auto save

        Collection<Mob> followers = Level.mobsFollowLevelChange(Mode.FALL);
        Dungeon.saveCurrentLevel();

        Position next = DungeonGenerator.descend(Dungeon.currentPosition());
        Dungeon.depth = DungeonGenerator.getLevelDepth(next.levelId);

        Level level = Dungeon.loadLevel(next);

        Dungeon.switchLevel(level,
                fallIntoPit ? level.pitCell() : level.randomRespawnCell(),
                followers);
    }

    private void ascend() {
        Actor.fixTime();

        Collection<Mob> followers = Level.mobsFollowLevelChange(Mode.ASCEND);
        Dungeon.onHeroLeaveLevel();
        Dungeon.save(false);

        Position next = DungeonGenerator.ascend(Dungeon.currentPosition());

        Dungeon.depth = DungeonGenerator.getLevelDepth(next.levelId);

        Level level = Dungeon.loadLevel(next);

        int exitIndex = -(next.cellId + 1);

        Dungeon.switchLevel(level,
                level.getExit(exitIndex),
                followers);
    }

    private void returnTo() {

        Actor.fixTime();

        Collection<Mob> followers = Level.mobsFollowLevelChange(Mode.RETURN);
        Dungeon.onHeroLeaveLevel();
        Dungeon.save(false);

        Dungeon.depth = DungeonGenerator.getLevelDepth(returnTo.levelId);

        Level level = Dungeon.loadLevel(returnTo);

        returnTo.computeCell(level);

        Dungeon.switchLevel(level,
                returnTo.cellId,
                followers);
    }

    private void rescue(Exception cause) {
        if (!rescueMode) {
            rescueMode = true;

            EventCollector.logException(cause, "enter rescue mode");

            if(SaveUtils.slotUsed(SaveUtils.getPrevSave(), Dungeon.heroClass)) {
                SaveUtils.loadGame(SaveUtils.getPrevSave(), Dungeon.heroClass);
            } else {
                GameLoop.switchScene(TitleScene.class);
            }
            return;
        }
        EventCollector.logException(cause,"rescue failed");
        error = Utils.format("Sorry, but something terrible happens with backup save for %s\n",Dungeon.heroClass.name());
    }


    private void restoreAtPosition(@Nullable Position restorePos) {
        Actor.fixTime();

        try {
            Dungeon.loadGame();

            if(restorePos == null) {
                restorePos = Dungeon.currentPosition();
            }

            Level level = Dungeon.loadLevel(restorePos);

            Dungeon.switchLevel(level, Dungeon.hero.getPos(), CharsList.emptyMobList);

        } catch (Exception e) {
            rescue(e);
            return;
        }
        rescueMode = false;
    }

    private void resurrect() {

        Actor.fixTime();

        if (Dungeon.bossLevel()) {
            Dungeon.hero.resurrect(Dungeon.depth);
            Level level = Dungeon.newLevel(Dungeon.currentPosition());
            Dungeon.switchLevel(level, level.entrance, CharsList.emptyMobList);
        } else {
            Dungeon.hero.resurrect(-1);
            Dungeon.resetLevel();
        }
    }


    @Override
    protected void onBackPressed() {
    }
}
