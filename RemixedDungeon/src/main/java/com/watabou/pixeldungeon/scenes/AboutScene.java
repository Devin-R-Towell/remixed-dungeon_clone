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

import android.content.Intent;

import com.nyrds.pixeldungeon.ml.R;
import com.nyrds.platform.game.Game;
import com.nyrds.platform.game.RemixedDungeon;
import com.nyrds.platform.input.Touchscreen.Touch;
import com.nyrds.util.GuiProperties;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.Scene;
import com.watabou.noosa.Text;
import com.watabou.noosa.TouchArea;
import com.watabou.pixeldungeon.effects.Flare;
import com.watabou.pixeldungeon.ui.Archs;
import com.watabou.pixeldungeon.ui.ExitButton;
import com.watabou.pixeldungeon.ui.Icons;
import com.watabou.pixeldungeon.ui.Window;

public class AboutScene extends PixelScene {

	private static String getTXT() {
		return Game.getVar(R.string.AboutScene_Txt_Intro) + "\n\n"
				+ Game.getVar(R.string.AboutScene_Code) + " " + Game.getVar(R.string.AboutScene_Code_Names) + "\n\n"
				+ Game.getVar(R.string.AboutScene_Graphics) + " " + Game.getVar(R.string.AboutScene_Graphics_Names) + "\n\n"
				+ Game.getVar(R.string.AboutScene_Music) + " " + Game.getVar(R.string.AboutScene_Music_Names) + "\n\n"
				+ Game.getVar(R.string.AboutScene_Sound) + " " + Game.getVar(R.string.AboutScene_Sound_Names) + "\n\n"
				+ Game.getVar(R.string.AboutScene_Thanks) + " " + Game.getVar(R.string.AboutScene_Thanks_Names) + "\n\n"
				+ Game.getVar(R.string.AboutScene_Email_Us);
	}

	private static String getTRN() {
		return Game.getVar(R.string.AboutScene_Translation) + "\n\t" + Game.getVar(R.string.AboutScene_Translation_Names);
	}

	private Text createTouchEmail(final String address, Text text2)
	{
		Text text = createText(address, text2);
		text.hardlight( Window.TITLE_COLOR );
		
		TouchArea area = new TouchArea( text ) {
			@Override
			protected void onClick( Touch touch ) {
				Intent intent = new Intent( Intent.ACTION_SEND);
				intent.setType("message/rfc822");
				intent.putExtra(Intent.EXTRA_EMAIL, new String[]{address} );
				intent.putExtra(Intent.EXTRA_SUBJECT, Game.getVar(R.string.app_name) );

				Game.instance().startActivity( Intent.createChooser(intent, Game.getVar(R.string.AboutScene_Snd)) );
			}
		};
		add(area);
		return text;
	}
	
	private Text createTouchLink(final String address, Text visit)
	{
		Text text = createText(address, visit);
		text.hardlight( Window.TITLE_COLOR );
		
		TouchArea area = new TouchArea( text ) {
			@Override
			protected void onClick( Touch touch ) {
				Game.instance().openUrl(Game.getVar(R.string.AboutScene_OurSite), address);
			}
		};
		add(area);
		return text;
	}
	
	private void placeBellow(Text elem, Text upper)
	{
		elem.x = upper.x;
		elem.y = upper.y + upper.height();
	}

	private Text createText(String text, Text upper)
	{
		Text multiline = createMultiline( text, GuiProperties.regularFontSize() );
		multiline.maxWidth(Camera.main.width * 5 / 6);
		add( multiline );
		if(upper!=null){
			placeBellow(multiline, upper);
		}
		return multiline;
	}
	
	@Override
	public void create() {
		super.create();

		Text text = createText(getTXT(), null );
		
		text.camera = uiCamera;
		
		text.x = align( (Camera.main.width - text.width()) / 2 );
		text.y = align( (Camera.main.height - text.height()) / 3 );
		

		Text email = createTouchEmail(Game.getVar(R.string.AboutScene_Mail), text);

		Text visit = createText(Game.getVar(R.string.AboutScene_OurSite), email);
		Text site  = createTouchLink(Game.getVar(R.string.AboutScene_Lnk), visit);
		
		createText("\n"+ getTRN(), site);
		
		Image nyrdie = Icons.NYRDIE.get();
		nyrdie.x = align( text.x + (text.width() - nyrdie.width) / 2 );
		nyrdie.y = text.y - nyrdie.height - 8;
		add( nyrdie );

		TouchArea area = new TouchArea( nyrdie ) {
			private int clickCounter = 0;

			@Override
			protected void onClick( Touch touch ) {
				clickCounter++;

				if(clickCounter > 11) {
					return;
				}

				if(clickCounter>10) {
					Game.toast("Levels test mode enabled");
					Scene.setMode("levelsTest");
					return;
				}

				if(clickCounter>7) {
					Game.toast("Are you sure?");
					return;
				}

				if(clickCounter>3) {
					Game.toast("%d", clickCounter);
				}
			}
		};
		add(area);

		new Flare( 7, 64 ).color( 0x332211, true ).show( nyrdie, 0 ).angularSpeed = -20;
		
		Archs archs = new Archs();
		archs.setSize( Camera.main.width, Camera.main.height );
		addToBack( archs );
		
		ExitButton btnExit = new ExitButton();
		btnExit.setPos( Camera.main.width - btnExit.width(), 0 );
		add( btnExit );
		
		fadeIn();
	}
	
	@Override
	protected void onBackPressed() {
		RemixedDungeon.switchNoFade( TitleScene.class );
	}
}
