/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
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
import com.nyrds.pixeldungeon.game.GamePreferences;
import com.nyrds.pixeldungeon.ml.R;
import com.nyrds.pixeldungeon.mobs.npc.ServiceManNPC;
import com.nyrds.pixeldungeon.support.EuConsent;
import com.nyrds.pixeldungeon.utils.GameControl;
import com.nyrds.pixeldungeon.windows.WndEuConsent;
import com.nyrds.platform.audio.Sample;
import com.nyrds.platform.game.Game;
import com.nyrds.platform.game.RemixedDungeon;
import com.nyrds.util.GuiProperties;
import com.nyrds.util.Util;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.Text;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.ui.Button;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Badges;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.GamesInProgress;
import com.watabou.pixeldungeon.Logbook;
import com.watabou.pixeldungeon.actors.hero.HeroClass;
import com.watabou.pixeldungeon.effects.BannerSprites;
import com.watabou.pixeldungeon.effects.BannerSprites.Type;
import com.watabou.pixeldungeon.effects.Speck;
import com.watabou.pixeldungeon.ui.Archs;
import com.watabou.pixeldungeon.ui.ChallengeButton;
import com.watabou.pixeldungeon.ui.ExitButton;
import com.watabou.pixeldungeon.ui.GameButton;
import com.watabou.pixeldungeon.utils.Utils;
import com.watabou.pixeldungeon.windows.WndClass;
import com.watabou.pixeldungeon.windows.WndOptions;

import java.util.ArrayList;
import java.util.Locale;

public class StartScene extends PixelScene {

    private static final float BUTTON_HEIGHT = 24;
    private static final float GAP = 2;

    private static final float WIDTH_P = 116;
    private static final float HEIGHT_P = 220;

    private static final float WIDTH_L = 224;
    private static final float HEIGHT_L = 124;

    private final ArrayList<ClassShield> shields = new ArrayList<>();

    private float buttonX;
    private float buttonY;

    private GameButton btnLoad;
    private GameButton btnNewGame;

    private boolean huntressUnlocked;
    private boolean elfUnlocked;
    private boolean gnollUnlocked;

    private float width, height, bottom;

    private Text unlock;

    private ClassShield curShield;

    @Override
    public void create() {
        super.create();

        Badges.loadGlobal();

        uiCamera.setVisible(false);

        int w = Camera.main.width;
        int h = Camera.main.height;

        if (RemixedDungeon.landscape()) {
            width = WIDTH_L;
            height = HEIGHT_L;
        } else {
            width = WIDTH_P;
            height = HEIGHT_P;
        }

        float left = (w - width) / 2;
        float top = (h - height) / 2;

        bottom = h - top;

        Archs archs = new Archs();
        archs.setSize(w, h);
        add(archs);

        Image title = BannerSprites.get(Type.SELECT_YOUR_HERO);

        title.setScale(0.85f, 0.85f);
        title.x = align((w - title.width()) / 2);
        title.y = align(top);

        add(title);

        buttonX = left;
        buttonY = bottom - BUTTON_HEIGHT;

        btnNewGame = new GameButton(Game.getVar(R.string.StartScene_New)) {
            @Override
            protected void onClick() {
                if (GamesInProgress.check(curShield.cl) != null) {
                    StartScene.this.add(new WndOptions(Game
                            .getVar(R.string.StartScene_Really), Game
                            .getVar(R.string.StartScene_Warning),
                            Game.getVar(R.string.StartScene_Yes), Game.getVar(R.string.StartScene_No)) {
                        @Override
                        public void onSelect(int index) {
                            if (index == 0) {
                                selectDifficulty();
                            }
                        }
                    });

                } else {
                    selectDifficulty();
                }
            }
        };
        add(btnNewGame);

        btnLoad = new GameButton(Game
                .getVar(R.string.StartScene_Load)) {
            @Override
            protected void onClick() {
                Dungeon.hero = null;
                Dungeon.heroClass = curShield.cl;
                InterlevelScene.Do(InterlevelScene.Mode.CONTINUE);
            }
        };
        add(btnLoad);

        float centralHeight = buttonY - title.y - title.height();

        int usableClasses = 0;

        shields.clear();
        for (HeroClass cl : HeroClass.values()) {
            if (cl.allowed()) {
                usableClasses++;
                ClassShield shield = new ClassShield(cl);
                shields.add(shield);
                add(shield);
            }
        }

        if (RemixedDungeon.landscape()) {
            float shieldW = width / usableClasses;
            float shieldH = Math.min(centralHeight, shieldW);
            top = title.y + title.height + (centralHeight - shieldH) / 2;
            int i = 0;
            for (ClassShield shield : shields) {
                shield.setRect(left + i * shieldW, top, shieldW, shieldH);
                i++;
            }

            ChallengeButton challenge = new ChallengeButton(this);
            challenge.setPos(w / 2 - challenge.width() / 2, 0);
            add(challenge);

        } else {
            int classesPerRow = 4;
            float shieldW = width / (classesPerRow);
            float shieldH = shieldW * 1.2f;
            top = title.y + title.height() + shieldH * 0.25f;
            int i = 0;
            int j = 0;
            for (ClassShield shield : shields) {

                if (j == 0 && i == 1) {
                    i += 2;
                }

                shield.setRect(left + i * shieldW, top + (shieldH * 1.5f) * j,
                        shieldW, shieldH);
                i++;
                if (i == classesPerRow) {
                    j++;
                    i = 0;
                }
            }

            ChallengeButton challenge = new ChallengeButton(this);
            challenge.setPos(w / 2 - challenge.width() / 2, top + shieldH * 0.5f
                    - challenge.height() / 2);
            add(challenge);
        }

        unlock = PixelScene.createMultiline(GuiProperties.titleFontSize());
        add(unlock);

        huntressUnlocked = Badges.isUnlocked(Badges.Badge.BOSS_SLAIN_3) || (GamePreferences.donated() >= 1);
        elfUnlocked = Badges.isUnlocked(Badges.Badge.BOSS_SLAIN_4) || (GamePreferences.donated() >= 2);
        gnollUnlocked = Badges.isUnlocked(Badges.Badge.GNOLL_UNLOCKED) || (GamePreferences.donated() >= 3);

        ExitButton btnExit = new ExitButton();
        btnExit.setPos(Camera.main.width - btnExit.width(), 0);
        add(btnExit);

        for (ClassShield shield : shields) {
            if (shield.cl.classIndex() == GamePreferences.lastClass()) {
                updateShield(shield);
                return;
            }
        }

        if (curShield == null) {
            updateShield(shields.get(0));
        }

        Logbook.logbookEntries.clear();    // Clear the log book before starting a new game
        ServiceManNPC.resetLimit();

        fadeIn();
    }

    private void updateUnlockLabel(String text) {
        unlock.maxWidth((int) width);
        unlock.text(text);

        float pos = (bottom - BUTTON_HEIGHT)
                + (BUTTON_HEIGHT - unlock.height()) / 2;

        unlock.hardlight(0xFFFF00);
        unlock.x = PixelScene.align(Camera.main.width / 2 - unlock.width() / 2);
        unlock.y = PixelScene.align(pos);

        unlock.setVisible(true);
        btnLoad.setVisible(false);
        btnNewGame.setVisible(false);
    }

    @Override
    public void destroy() {

        Badges.saveGlobal();

        super.destroy();
    }

    private void updateShield(ClassShield shield) {
        if (curShield == shield) {
            add(new WndClass(shield.cl));
            return;
        }

        if (curShield != null) {
            curShield.highlight(false);
        }

        curShield = shield;
        curShield.highlight(true);


        if (!Util.isDebug()) {
            if (curShield.cl == HeroClass.HUNTRESS && !huntressUnlocked) {
                updateUnlockLabel(Game.getVar(R.string.StartScene_Unlock));
                return;
            }

            if (curShield.cl == HeroClass.ELF && !elfUnlocked) {
                updateUnlockLabel(Game.getVar(R.string.StartScene_UnlockElf));
                return;
            }

            if (curShield.cl == HeroClass.GNOLL && !gnollUnlocked) {
                updateUnlockLabel(Game.getVar(R.string.StartScene_UnlockGnoll));
                return;
            }

        }

        unlock.setVisible(false);

        GamesInProgress.Info info = GamesInProgress.check(curShield.cl);
        if (info != null) {

            btnLoad.setVisible(true);
            btnLoad.secondary(Utils.format(Game
                            .getVar(R.string.StartScene_Depth), info.depth,
                    info.level));

            btnNewGame.setVisible(true);
            btnNewGame.secondary(Game
                    .getVar(R.string.StartScene_Erase));

            float w = (Camera.main.width - GAP) / 2 - buttonX;

            btnLoad.setRect(buttonX, buttonY, w, BUTTON_HEIGHT);
            btnNewGame.setRect(btnLoad.right() + GAP, buttonY, w,
                    BUTTON_HEIGHT);

        } else {
            btnLoad.setVisible(false);

            btnNewGame.setVisible(true);
            btnNewGame.secondary(Utils.EMPTY_STRING);
            btnNewGame.setRect(buttonX, buttonY, Camera.main.width
                    - buttonX * 2, BUTTON_HEIGHT);
        }

    }

    private void selectDifficulty() {

        WndOptions difficultyOptions = new WndOptions(Game.getVar(R.string.StartScene_DifficultySelect), Utils.EMPTY_STRING,
                Game.getVar(GamePreferences.donated() > 0 ? R.string.StartScene_DifficultyEasyNoAds : R.string.StartScene_DifficultyEasy),
                Game.getVar(GamePreferences.donated() > 0 ? R.string.StartScene_DifficultyNormalWithSavesNoAds : R.string.StartScene_DifficultyNormalWithSaves),
                Game.getVar(R.string.StartScene_DifficultyNormal),
                Game.getVar(R.string.StartScene_DifficultyExpert)) {
            @Override
            public void onSelect(final int index) {

                if (index < 2 && EuConsent.getConsentLevel() < EuConsent.NON_PERSONALIZED) {
                    GameLoop.addToScene(new WndEuConsent() {
                        @Override
                        public void done() {
                            startNewGame(index);
                        }
                    });
                    return;
                }

                startNewGame(index);
            }
        };

        add(difficultyOptions);
    }

    private void startNewGame(int difficulty) {
        GameControl.startNewGame(curShield.cl.name(), difficulty, false);
    }

    @Override
    protected void onBackPressed() {
        RemixedDungeon.switchNoFade(TitleScene.class);
    }

    private class ClassShield extends Button {

        private static final float MIN_BRIGHTNESS = 0.6f;

        private static final int BASIC_NORMAL = 0x444444;
        private static final int BASIC_HIGHLIGHTED = 0xCACFC2;

        private static final int MASTERY_NORMAL = 0x7711AA;
        private static final int MASTERY_HIGHLIGHTED = 0xCC33FF;

        private static final int WIDTH = 24;
        private static final int HEIGHT = 28;
        private static final float SCALE = 1.5f;

        private final HeroClass cl;

        private Image avatar;
        private Text name;
        private Emitter emitter;

        private float brightness;

        private final int normal;
        private final int highlighted;

        public ClassShield(HeroClass cl) {
            super();

            this.cl = cl;

            avatar.frame(cl.classIndex() * WIDTH, 0, WIDTH, HEIGHT);
            avatar.Scale().set(SCALE);

            if (Badges.isUnlocked(cl.masteryBadge())) {
                normal = MASTERY_NORMAL;
                highlighted = MASTERY_HIGHLIGHTED;
            } else {
                normal = BASIC_NORMAL;
                highlighted = BASIC_HIGHLIGHTED;
            }

            name.text(cl.title().toUpperCase(Locale.getDefault()));
            name.hardlight(normal);

            brightness = MIN_BRIGHTNESS;
            updateBrightness();
        }

        @Override
        protected void createChildren() {

            super.createChildren();

            avatar = new Image(Assets.AVATARS);
            add(avatar);

            name = PixelScene.createText(GuiProperties.titleFontSize());
            add(name);

            emitter = new Emitter();
            add(emitter);
        }

        @Override
        protected void layout() {

            super.layout();

            avatar.x = align(x + (width - avatar.width()) / 2);
            avatar.y = align(y + (height - avatar.height() - name.height()) / 2);

            name.x = align(x + (width - name.width()) / 2);
            name.y = avatar.y + avatar.height() + SCALE;

            emitter.pos(avatar.x, avatar.y, avatar.width(), avatar.height());
        }

        @Override
        protected void onTouchDown() {

            emitter.revive();
            emitter.start(Speck.factory(Speck.LIGHT), 0.05f, 7);

            Sample.INSTANCE.play(Assets.SND_CLICK, 1, 1, 1.2f);
            updateShield(this);
        }

        @Override
        public void update() {
            super.update();

            if (brightness < 1.0f && brightness > MIN_BRIGHTNESS) {
                if ((brightness -= GameLoop.elapsed) <= MIN_BRIGHTNESS) {
                    brightness = MIN_BRIGHTNESS;
                }
                updateBrightness();
            }
        }

        public void highlight(boolean value) {
            if (value) {
                brightness = 1.0f;
                name.hardlight(highlighted);

            } else {
                brightness = 0.999f;
                name.hardlight(normal);
            }

            updateBrightness();
        }

        private void updateBrightness() {
            avatar.gm = avatar.bm = avatar.rm = avatar.am = brightness;
        }
    }

}
