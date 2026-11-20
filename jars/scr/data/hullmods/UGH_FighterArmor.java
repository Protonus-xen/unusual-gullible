package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.UGH_MD;
import java.awt.Color;

public class UGH_FighterArmor extends BaseHullMod {

	public static final float MANEUVER_PENALTY = 20f;
	public static final float REFIT_PENALTY = 5f;
        
	public static final float FIGHTER_HULL_FLAT = 100f;
	public static final float FIGHTER_ARMOR_FLAT = 25f;
	public static final float BOMBER_HULL_FLAT = 75f;
	public static final float BOMBER_ARMOR_FLAT = 50f;
        
	public static final float SMODIFIER = 10f;
        
        @Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		float reedi = (int) (stats.getNumFighterBays().getModifiedValue() * REFIT_PENALTY);
		stats.getFighterRefitTimeMult().modifyMult(id, 1f + (reedi * 0.01f));
	}
	

        @Override
        public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {
            MutableShipStatsAPI stats = fighter.getMutableStats();
            MutableShipStatsAPI based_stats = ship.getMutableStats();
            boolean sMod = isSMod(based_stats);
            
            if (fighter.getWing().getSpec().isBomber()){
                stats.getHullBonus().modifyPercent(id, 10f);
                stats.getHullBonus().modifyFlat(id, FIGHTER_HULL_FLAT);
                stats.getArmorBonus().modifyPercent(id, 10f);
                stats.getArmorBonus().modifyFlat(id, FIGHTER_ARMOR_FLAT);
            } else {
                stats.getHullBonus().modifyPercent(id, 10f);
                stats.getHullBonus().modifyFlat(id, BOMBER_HULL_FLAT);
                stats.getArmorBonus().modifyPercent(id, 10f);
                stats.getArmorBonus().modifyFlat(id, BOMBER_ARMOR_FLAT);
            }
            
		if (!sMod) {
			stats.getAcceleration().modifyMult(id, 1f - MANEUVER_PENALTY * 0.01f);
			stats.getDeceleration().modifyMult(id, 1f - MANEUVER_PENALTY * 0.01f);
			stats.getTurnAcceleration().modifyMult(id, 1f - MANEUVER_PENALTY * 0.01f);
			stats.getMaxTurnRate().modifyMult(id, 1f - MANEUVER_PENALTY * 0.01f);
                }
		if (sMod) {
			stats.getHullDamageTakenMult().modifyMult(id, 1f - SMODIFIER / 100f);
			stats.getArmorDamageTakenMult().modifyMult(id, 1f - SMODIFIER / 100f);
                }
        }

        protected static final float LOAD_OF_BULL = 3f;
	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		float opad = 10f;
		float pad = 3f;
		Color h = Misc.getHighlightColor();
                Color bad = Misc.getNegativeHighlightColor();
                Color good = Misc.getPositiveHighlightColor();
                
                /*if (!(isForModSpec || ship == null)) {
                    if (ship.getVariant().hasHullMod(OCUA_HullMods.OCUA_OCULUS_MOD) || 
                                    ship.getVariant().hasHullMod(OCUA_HullMods.OCUA_OCUTEK_MOD)) {
                        beans_range = OCUA_BEAM_RANGE_BONUS;
                    }
                }*/
		
                LabelAPI bullet;
                tooltip.setBulletedListMode(" • ");
                
		tooltip.addSectionHeading(UGH_MD.base("improv"), Alignment.MID, opad);
                bullet = tooltip.addPara(UGH_MD.bfa("im_fighter"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "" + (int) FIGHTER_HULL_FLAT + " + 10%", "" + (int) FIGHTER_ARMOR_FLAT + " + 10%" );
		bullet.setHighlight(UGH_MD.bfa("im_fighter_h"), "" + (int) FIGHTER_HULL_FLAT + " + 10%", "" + (int) FIGHTER_ARMOR_FLAT + " + 10%");
		bullet.setHighlightColors(h, good, good);
                bullet = tooltip.addPara(UGH_MD.bfa("im_bomber"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "" + (int) BOMBER_HULL_FLAT + " + 10%", "" + (int) BOMBER_ARMOR_FLAT + " + 10%" );
		bullet.setHighlight(UGH_MD.bfa("im_bomber_h"), "" + (int) BOMBER_HULL_FLAT + " + 10%", "" + (int) BOMBER_ARMOR_FLAT + " + 10%");
		bullet.setHighlightColors(h, good, good);
                
		tooltip.addSectionHeading(UGH_MD.base("bad"), Alignment.MID, opad);
                bullet = tooltip.addPara(UGH_MD.bfa("bad_refit"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), h,
                    "+" + (int) REFIT_PENALTY + "%", UGH_MD.base("decks"));
		bullet.setHighlight("+" + (int) REFIT_PENALTY + "%", UGH_MD.base("decks"));
		bullet.setHighlightColors(bad, h);
                bullet = tooltip.addPara(UGH_MD.bfa("bad_manuever"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), bad,
                    "-" + (int) MANEUVER_PENALTY + "%" );
                
            tooltip.setBulletedListMode(null);
	}
	
        @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
		/*if (index == 0) return "75";
		if (index == 1) return "50";
		if (index == 2) return "100";
		if (index == 3) return "25";
		if (index == 4) return "" + (int) MANEUVER_PENALTY + "%";
		if (index == 5) return "20%";*/
		return null;
	}
        
        @Override
	public String getSModDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "" + (int) (SMODIFIER) + "%";
            return null;
	}

        @Override
	public boolean isApplicableToShip(ShipAPI ship) {
                int bays = (int) ship.getMutableStats().getNumFighterBays().getModifiedValue();
                return bays > 0;
	}
	
        @Override
	public String getUnapplicableReason(ShipAPI ship) {
		return UGH_MD.base("no_bays");
	}
        
}
