
package com.nyrds.pixeldungeon.windows;

import com.nyrds.pixeldungeon.game.GamePreferences;
import com.nyrds.pixeldungeon.ml.R;
import com.nyrds.platform.game.Game;
import com.nyrds.platform.game.RemixedDungeon;
import com.watabou.pixeldungeon.utils.Utils;
import com.watabou.pixeldungeon.windows.Selector;
import com.watabou.pixeldungeon.windows.WndMenuCommon;

public class WndUiSettings extends WndMenuCommon {

	@Override
	protected void createItems() {

		if (android.os.Build.VERSION.SDK_INT >= 19) {
			menuItems.add( new MenuCheckBox(Game.getVar(R.string.WndSettings_Immersive), GamePreferences.immersed()) {
				@Override
				protected void onClick() {
					super.onClick();
					GamePreferences.immerse(checked());
				}
			});
		}

		if(!GamePreferences.classicFont()){
			menuItems.add(createTextScaleButtons());
		}

		menuItems.add(new MenuButton(orientationText()) {
			@Override
			protected void onClick() {
				RemixedDungeon.landscape(!RemixedDungeon.landscape());
			}
		});

		final String[] texts = {Game.getVar(R.string.WndSettings_ExperementalFont),
				Game.getVar(R.string.WndSettings_ClassicFont)
		};

		if (Utils.canUseClassicFont(GamePreferences.uiLanguage())) {
			final int index = GamePreferences.classicFont() ? 0 : 1;

			menuItems.add( new MenuButton(texts[index]) {
				@Override
				protected void onClick() {
					GamePreferences.classicFont(!GamePreferences.classicFont());
					WndUiSettings.this.getParent().add(new WndUiSettings());
					hide();
				}
			});
		}
	}

	private Selector createTextScaleButtons() {
		return new Selector(WIDTH,BUTTON_HEIGHT,Game
				.getVar(R.string.WndSettings_TextScaleDefault), new Selector.PlusMinusDefault() {
			@Override
			public void onPlus(Selector s) {
				GamePreferences.fontScale(GamePreferences.fontScale() + 1);
				s.regen();
			}

			@Override
			public void onMinus(Selector s) {
				GamePreferences.fontScale(GamePreferences.fontScale() - 1);
				s.regen();
			}

			@Override
			public void onDefault(Selector s) {
				GamePreferences.fontScale(0);
				s.regen();
			}
		});
	}

	private String orientationText() {
		return RemixedDungeon.landscape() ? Game
				.getVar(R.string.WndSettings_SwitchPort) : Game
				.getVar(R.string.WndSettings_SwitchLand);
	}
}