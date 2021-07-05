package com.watabou.pixeldungeon.scenes;

import com.nyrds.pixeldungeon.game.GameLoop;
import com.nyrds.pixeldungeon.game.GamePreferences;
import com.nyrds.pixeldungeon.ml.R;
import com.nyrds.platform.game.Game;
import com.nyrds.platform.storage.Preferences;
import com.nyrds.util.GuiProperties;
import com.watabou.noosa.Camera;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.Text;
import com.watabou.noosa.ui.Component;
import com.watabou.pixeldungeon.Chrome;
import com.watabou.pixeldungeon.ui.Archs;
import com.watabou.pixeldungeon.ui.RedButton;
import com.watabou.pixeldungeon.ui.ScrollPane;

public class WelcomeScene extends PixelScene {

	private static final int GAP = 4;

	@Override
	public void create() {
		super.create();

		String[] upds = {
				Game.getVar(R.string.Welcome_Text_29_4),
				Game.getVar(R.string.Welcome_Text_29_5),
				Game.getVar(R.string.Welcome_Text_29_6),
				Game.getVar(R.string.Welcome_Text_30),
				Game.getVar(R.string.Welcome_Text_30_1)

		};

		int displayUpdates = Math.min(upds.length, 5);

		Text[] updTexts = new Text[displayUpdates];

		for (int i = 0; i < displayUpdates; i++) {
			updTexts[i] = createMultiline(GuiProperties.regularFontSize());
		}

		Text title = createMultiline(Game.getVar(R.string.Welcome_Title), GuiProperties.bigTitleFontSize());

		int w = Camera.main.width;
		int h = Camera.main.height;

		int pw = w - 10;

		title.maxWidth(pw);

		title.x = align((w - title.width()) / 2);
		title.y = align(8);
		add(title);

		NinePatch panel = Chrome.get(Chrome.Type.WINDOW);

		panel.x = (w - pw) / 2;
		panel.y = title.y + title.height() + GAP * 2;
		int ph = (int) (h - panel.y - 22);

		panel.size(pw, ph);

		add(panel);

		ScrollPane list = new ScrollPane(new Component());
		add(list);
		list.setRect(panel.x + panel.marginLeft(), panel.y + panel.marginTop(), panel.innerWidth(),
				panel.innerHeight());
		list.scrollTo(0, 0);

		Component content = list.content();
		content.clear();

		float yPos = 0;
		for (int i = 0; i < displayUpdates; i++) {
			updTexts[i].maxWidth((int) panel.innerWidth());
			updTexts[i].text(upds[upds.length - i - 1]);

			updTexts[i].setPos(0, yPos);
			yPos += updTexts[i].height() + GAP;
			content.add(updTexts[i]);
		}

		content.setSize(panel.innerWidth(), yPos);

		RedButton okay = new RedButton(Game.getVar(R.string.Welcome_Ok)) {
			@Override
			protected void onClick() {
				GamePreferences.version(Game.versionCode);
				GamePreferences.versionString(Game.version);

				if (Preferences.INSTANCE.getInt(Preferences.KEY_COLLECT_STATS, 1) == 0) {
					GameLoop.switchScene(AllowStatisticsCollectionScene.class);
				} else {
					GameLoop.switchScene(TitleScene.class);
				}
			}
		};

		okay.setRect((w - pw) / 2, h - 22, pw, 18);
		add(okay);

		Archs archs = new Archs();
		archs.setSize(Camera.main.width, Camera.main.height);
		addToBack(archs);

		fadeIn();
	}
}
