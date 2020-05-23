package com.watabou.pixeldungeon.sprites;

import com.nyrds.android.util.JsonHelper;
import com.nyrds.android.util.ModdingMode;
import com.nyrds.android.util.Util;
import com.nyrds.pixeldungeon.effects.CustomClipEffect;
import com.nyrds.pixeldungeon.items.accessories.Accessory;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Animation;
import com.watabou.noosa.TextureFilm;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.items.EquipableItem;
import com.watabou.pixeldungeon.items.armor.Armor;
import com.watabou.pixeldungeon.items.weapon.Weapon;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mike on 16.04.2016.
 */
public class ModernHeroSpriteDef extends HeroSpriteDef {

	private static final String HERO_EMPTY_PNG = "hero_modern/empty.png";
	private static final String WEAPON_ANIM = "weapon_anim";

	// body goes as main texture
	private static final String LAYER_ARMOR       = "armor";
	private static final String LAYER_HEAD        = "head";
	private static final String LAYER_HAIR        = "hair";
	private static final String LAYER_FACIAL_HAIR = "facial_hair";
	private static final String LAYER_HELMET      = "helmet";
	private static final String LAYER_BODY        = "body";
	private static final String LAYER_COLLAR      = "collar";
	private static final String LAYER_ACCESSORY   = "accessory";

	private static final String LAYER_LEFT_HAND   = "left_hand";
	private static final String LAYER_RIGHT_HAND  = "right_hand";

	private static final String LAYER_LEFT_ARMOR  = "left_hand_armor";
	private static final String LAYER_RIGHT_ARMOR = "right_hand_armor";

	private static final String LAYER_LEFT_ITEM   = "left_hand_item";
	private static final String LAYER_RIGHT_ITEM  = "right_hand_item";

	private static final String LAYER_LEFT_ITEM_BACK  = "left_back_item";
	private static final String LAYER_RIGHT_ITEM_BACK = "right_back_item";

	private static final String HERO_MODERN_SPRITES_DESC_HERO_JSON   = "hero_modern/spritesDesc/Hero.json";
	private static final String HERO_MODERN_SPRITES_DESC_STATUE_JSON = "hero_modern/spritesDesc/Statue.json";

	private static final String BODY_TYPE = "bodyType";

	private CustomClipEffect deathEffect;

	private Map<String, Animation> weapon_anims;
	private Map<String, String>    body_types;

	private static final String[] layersOrder = {
		LAYER_RIGHT_ITEM_BACK,
		LAYER_LEFT_ITEM_BACK,
		LAYER_BODY,
		LAYER_COLLAR,
		LAYER_HEAD,
		LAYER_HAIR,
		LAYER_ARMOR,
		LAYER_FACIAL_HAIR,
		LAYER_HELMET,
		LAYER_LEFT_ARMOR,
		LAYER_RIGHT_ARMOR,
		LAYER_LEFT_HAND,
		LAYER_RIGHT_HAND,
		LAYER_ACCESSORY,
		LAYER_LEFT_ITEM,
		LAYER_RIGHT_ITEM,
	};

	private Map<String,String> layerOverrides = new HashMap<>();
	private Map<String,String> layersDesc    = new HashMap<>();

	private String deathEffectDesc;

	public ModernHeroSpriteDef(String[] lookDesc, String deathEffectDesc){
		super(HERO_MODERN_SPRITES_DESC_HERO_JSON,0);
		this.deathEffectDesc = deathEffectDesc;
		applyLayersDesc(lookDesc);
	}

	public ModernHeroSpriteDef(Armor armor){
		super(HERO_MODERN_SPRITES_DESC_STATUE_JSON,0);
		createStatueSprite(armor);
		applyLayersDesc(getLayersDesc());
	}

	public ModernHeroSpriteDef(Hero hero) {
		super(HERO_MODERN_SPRITES_DESC_HERO_JSON,0);
		createLayersDesc(hero);
		applyLayersDesc(getLayersDesc());
	}

	public ModernHeroSpriteDef(Hero hero, Accessory accessory) {
		super(HERO_MODERN_SPRITES_DESC_HERO_JSON,0);
		createLayersDesc(hero, accessory);
		applyLayersDesc(getLayersDesc());
	}

	public ModernHeroSpriteDef(Weapon weapon) {
		super(HERO_MODERN_SPRITES_DESC_STATUE_JSON,0);
		createStatueSprite(weapon);
		applyLayersDesc(getLayersDesc());
	}

	private void createLayersDesc(Hero hero) {
		Accessory accessory = Accessory.equipped();
		createLayersDesc(hero, accessory);
	}

	private void createLayersDesc(Hero hero, Accessory accessory) {

		layerOverrides = hero.getLayersOverrides();

		layersDesc.clear();
		boolean drawHair = true;

		String accessoryDescriptor = HERO_EMPTY_PNG;
		String classDescriptor = hero.getHeroClass().toString()+"_"+ hero.getSubClass().toString();
		String deathDescriptor = classDescriptor.equals("MAGE_WARLOCK") ? "warlock" : "common";
		String facialHairDescriptor = HERO_EMPTY_PNG;
		String hairDescriptor = HERO_EMPTY_PNG;
		String helmetDescriptor = HERO_EMPTY_PNG;

		if(accessory == null || !accessory.isCoverFacialHair()) {
			if (classDescriptor.equals("MAGE_WARLOCK")
					|| classDescriptor.equals("MAGE_BATTLEMAGE")
					|| classDescriptor.equals("WARRIOR_BERSERKER")
					|| classDescriptor.equals("NECROMANCER_NONE")) {
				facialHairDescriptor = "hero_modern/head/facial_hair/" + classDescriptor + "_FACIAL_HAIR.png";
			}
		}

		if (accessory  == null){
			if(hero.getBelongings().armor.hasHelmet()) {
                helmetDescriptor = helmetDescriptor(hero.getBelongings().armor, hero);
            }
            if(hero.getBelongings().armor.isCoveringHair()){
                drawHair = false;
            }
		} else {
			accessoryDescriptor = accessory.getLayerFile();
			if(accessory.isCoveringHair()) {
				drawHair = false;
			}
		}

		if (drawHair){ hairDescriptor = "hero_modern/head/hair/" + classDescriptor + "_HAIR.png"; }

		String bodyType = bodyDescriptor(hero);

		layersDesc.put(LAYER_BODY, "hero_modern/body/" +bodyType+".png" );
		layersDesc.put(LAYER_COLLAR, collarDescriptor(hero.getBelongings().armor, hero));
		layersDesc.put(LAYER_HEAD, "hero_modern/head/" + classDescriptor + ".png");
		layersDesc.put(LAYER_HAIR, hairDescriptor);
		layersDesc.put(LAYER_ARMOR, armorDescriptor(hero.getBelongings().armor));
		layersDesc.put(LAYER_FACIAL_HAIR, facialHairDescriptor);
		layersDesc.put(LAYER_HELMET, helmetDescriptor);


		EquipableItem weapon = hero.getBelongings().weapon;
		String weaponAnimationClassLeft  = weapon.getAttackAnimationClass();

		EquipableItem leftHand = hero.getBelongings().leftHand;
		String weaponAnimationClassRight = leftHand.getAttackAnimationClass();


		layersDesc.put(LAYER_LEFT_HAND, "hero_modern/body/hands/" + bodyType + "_" + weaponAnimationClassLeft + "_left.png");
		layersDesc.put(LAYER_RIGHT_HAND, "hero_modern/body/hands/" + bodyType + "_" + weaponAnimationClassRight + "_right.png");

		layersDesc.put(LAYER_ACCESSORY, accessoryDescriptor);

		if(accessory==null || !accessory.isCoveringItems()) {
			layersDesc.put(LAYER_LEFT_ITEM_BACK,  itemBackDescriptor(leftHand,"left"));
			layersDesc.put(LAYER_RIGHT_ITEM_BACK, itemBackDescriptor(weapon, "right"));

			layersDesc.put(LAYER_LEFT_ITEM,  itemHandDescriptor(leftHand,"left"));
			layersDesc.put(LAYER_RIGHT_ITEM, itemHandDescriptor(weapon, "right"));
		}

		deathEffectDesc = "hero_modern/death/" +deathDescriptor+".png";
	}

	private void createStatueSprite(Weapon weapon) {
		layersDesc.put(LAYER_BODY, "hero_modern/body/statue.png");
		layersDesc.put(LAYER_HEAD, "hero_modern/head/statue.png");

		String weaponAnimationClassLeft  = EquipableItem.NO_ANIMATION;
		String weaponAnimationClassRight = EquipableItem.NO_ANIMATION;

		if(weapon !=null) {
			weaponAnimationClassRight = weapon.getAttackAnimationClass();
		}

		layersDesc.put(LAYER_LEFT_HAND,  "hero_modern/body/hands/statue_" +weaponAnimationClassLeft+"_left.png");
		layersDesc.put(LAYER_RIGHT_HAND, "hero_modern/body/hands/statue_" +weaponAnimationClassRight+"_right.png");

		//layersDesc.put(LAYER_LEFT_ITEM,  "hero_modern/empty.png");
		layersDesc.put(LAYER_RIGHT_ITEM, itemHandDescriptor(weapon, "right"));


		deathEffectDesc = "hero_modern/death/statue.png";
	}

	private void createStatueSprite(Armor armor) {
		layersDesc.put(LAYER_BODY, "hero_modern/body/statue.png");
		layersDesc.put(LAYER_HEAD, "hero_modern/head/statue.png");
		layersDesc.put(LAYER_ARMOR,       armorDescriptor(armor));


		layersDesc.put(LAYER_LEFT_HAND, "hero_modern/body/hands/statue_none_left.png");
		layersDesc.put(LAYER_RIGHT_HAND, "hero_modern/body/hands/statue_none_right.png");

		deathEffectDesc = "hero_modern/death/statue.png";
	}

	public void heroUpdated(Hero hero) {
		reset();
		createLayersDesc(hero);
		applyLayersDesc(getLayersDesc());

		avatar = null;

		zap = attack = weapon_anims.get(EquipableItem.NO_ANIMATION);

		Accessory accessory = Accessory.equipped();

		if(accessory != null && accessory.isCoveringItems()) { // no fancy attacks in costumes
			return;
		}

		if(!weapon_anims.isEmpty()) { //old mods compatibility
			EquipableItem weapon = hero.getBelongings().weapon;
			EquipableItem leftHand = hero.getBelongings().leftHand;

			boolean right = weapon.goodForMelee();
			boolean left  = leftHand.goodForMelee();

			if(right && left) {
				zap = attack = weapon_anims.get("dual");
				return;
			}

			if(right) {
				zap = attack = weapon_anims.get("right");
				return;
			}

			if(left) {
				zap = attack = weapon_anims.get("left");
			}
		}

	}

	public String[] getLayersDesc() {
		ArrayList<String> ret= new ArrayList<>();
		for (String layer : layersOrder) {
			if (layersDesc.containsKey(layer)) {
				String override = layerOverrides.get(layer);
				if(override==null) {
					ret.add(layersDesc.get(layer));
					continue;
				}

				if(override.equals("remove")) {
					continue;
				}

				ret.add(override);
			}
		}

		return ret.toArray(new String[0]);
	}

	private void applyLayersDesc(String[] lookDesc) {
		clearLayers();
		for(int i = 0;i<layersOrder.length && i<lookDesc.length;++i){
			addLayer(layersOrder[i], TextureCache.get(lookDesc[i]));
		}
		deathEffect = new CustomClipEffect(deathEffectDesc, (int)width, (int)height);
	}

	private String armorDescriptor(EquipableItem armor) {
		String visualName = armor.getVisualName();
		if(visualName.equals("none")) {
			return HERO_EMPTY_PNG;
		}
		return "hero_modern/armor/" +armor.getVisualName()+".png";
	}

	private String itemHandDescriptor(EquipableItem item, String hand) {
		String visualName = item.getVisualName();
		if(visualName.equals("none")) {
			return HERO_EMPTY_PNG;
		}

		return "hero_modern/items/" +item.getVisualName()+"_"+hand+".png";
	}

	private String itemBackDescriptor(EquipableItem item, String hand) {
		String defaultLayerFile = "hero_modern/empty.png";

		String itemLayerFile = "hero_modern/items/" +item.getVisualName()+"_back_"+hand+".png";
		if(ModdingMode.isResourceExist(itemLayerFile)) {
			return itemLayerFile;
		}
		return defaultLayerFile;
	}


	private String helmetDescriptor(EquipableItem armor, Hero hero) {
		if(hero.getBelongings().armor.hasHelmet()){
			return "hero_modern/armor/helmet/" +armor.getClass().getSimpleName()+".png";
		}
		return HERO_EMPTY_PNG;
	}

	private String collarDescriptor(EquipableItem armor, Hero hero) {
		if(hero.getBelongings().armor.hasCollar()){
			return "hero_modern/armor/collar/" +armor.getClass().getSimpleName()+".png";
		}
		return HERO_EMPTY_PNG;
	}

	private String bodyDescriptor(@NotNull Hero hero) {
		String key = hero.getSubClass().name();

		if(body_types.containsKey(key)) {
			return body_types.get(key);
		}

		key = hero.getHeroClass().name();

		if(body_types.containsKey(key)) {
			return body_types.get(key);
		}

		return "man";
	}

	@Override
	protected void loadAdditionalData(JSONObject json, TextureFilm film, int kind) throws JSONException {
		super.loadAdditionalData(json,film,kind);

		weapon_anims = new HashMap<>();
		if(json.has(WEAPON_ANIM)){
			JsonHelper.foreach(json.getJSONObject(WEAPON_ANIM),
					(root, key) -> weapon_anims.put(key,readAnimation(root,key,film)));
		}

		body_types = new HashMap<>();
		if(json.has(BODY_TYPE)) {
			JsonHelper.foreach(json.getJSONObject(BODY_TYPE),
					(root, key) -> body_types.put(key,root.getString(key)));
		}
	}

	@Override
	public void die() {
		ch.ifPresent(chr -> {
			deathEffect.place(chr.getPos());
			getParent().add(deathEffect);
			deathEffect.setVisible(true);

			if (chr instanceof Hero) {
				deathEffect.playAnim(die, Util.nullCallback);
			} else {
				deathEffect.playAnim(die, () -> deathEffect.killAndErase());
			}
		});
		killAndErase();

	}

	@NotNull
	public String getDeathEffect() {
		return deathEffectDesc;
	}
}
