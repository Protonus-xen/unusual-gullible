package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignUIAPI;
import com.fs.starfarer.api.campaign.CampaignUIAPI.CoreUITradeMode;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.UGH_MD;
import java.awt.Color;

public class UGH_ShuntThrusters extends BaseHullMod {
	public static float SHIELD_DAM;
        public static final float SPEED_BONUS = 10f;
        public static final float MANEUVER_BONUS = 10f;
        public static final float ZERO_FLUX_PENALTY = 5f;
	
	public static final float SMODIFIER = 2f;
	public static final float SMODIFIER2 = 25f;
        
	public float getShieldPower(ShipHullSpecAPI hullSpec) {
		return (float) (hullSpec.getShieldSpec().getFluxPerDamageAbsorbed());
	}
	
        @Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
            SHIELD_DAM = getShieldPower(stats.getVariant().getHullSpec());
            boolean sMod = isSMod(stats);
            
            float manoov = MANEUVER_BONUS;
            float zluf = ZERO_FLUX_PENALTY;
            if (sMod) {
                manoov = MANEUVER_BONUS * SMODIFIER;
                zluf = ZERO_FLUX_PENALTY / SMODIFIER;
            }
            
            stats.getEngineDamageTakenMult().modifyMult(id, (sMod ? (1f - (SMODIFIER2 * 0.01f)) : 1));
            
            /* // RIP Shield Shunt builds
            if (stats.getVariant().getHullSpec().getShieldType() == ShieldAPI.ShieldType.NONE && 
				stats.getVariant().hasHullMod("frontshield")){
                stats.getMaxSpeed().modifyFlat(id, SPEED_BONUS * 2f);
                stats.getZeroFluxSpeedBoost().modifyFlat(id, -ZERO_FLUX_PENALTY * 2f);
		stats.getAcceleration().modifyPercent(id, manoov * 2f);
		stats.getDeceleration().modifyPercent(id, manoov * 2f);
		stats.getTurnAcceleration().modifyPercent(id, manoov * 2f);
		stats.getMaxTurnRate().modifyPercent(id, manoov * 2f);
            } else */
            if (SHIELD_DAM <= 0.4f){
                stats.getMaxSpeed().modifyFlat(id, SPEED_BONUS * 5f);
                stats.getZeroFluxSpeedBoost().modifyFlat(id, -zluf * 5f);
		stats.getAcceleration().modifyPercent(id, manoov * 5f);
		stats.getDeceleration().modifyPercent(id, manoov * 5f);
		stats.getTurnAcceleration().modifyPercent(id, manoov * 5f);
		stats.getMaxTurnRate().modifyPercent(id, manoov * 5f);
            } else if (SHIELD_DAM <= 0.6f){
                stats.getMaxSpeed().modifyFlat(id, SPEED_BONUS * 4f);
                stats.getZeroFluxSpeedBoost().modifyFlat(id, -zluf * 4f);
		stats.getAcceleration().modifyPercent(id, manoov * 4f);
		stats.getDeceleration().modifyPercent(id, manoov * 4f);
		stats.getTurnAcceleration().modifyPercent(id, manoov * 4f);
		stats.getMaxTurnRate().modifyPercent(id, manoov * 4f);
            } else if (SHIELD_DAM <= 0.8f){
                stats.getMaxSpeed().modifyFlat(id, SPEED_BONUS * 3f);
                stats.getZeroFluxSpeedBoost().modifyFlat(id, -zluf * 3f);
		stats.getAcceleration().modifyPercent(id, manoov * 3f);
		stats.getDeceleration().modifyPercent(id, manoov * 3f);
		stats.getTurnAcceleration().modifyPercent(id, manoov * 3f);
		stats.getMaxTurnRate().modifyPercent(id, manoov * 3f);
            } else if (SHIELD_DAM <= 1f){
                stats.getMaxSpeed().modifyFlat(id, SPEED_BONUS * 2f);
                stats.getZeroFluxSpeedBoost().modifyFlat(id, -zluf * 2f);
		stats.getAcceleration().modifyPercent(id, manoov * 2f);
		stats.getDeceleration().modifyPercent(id, manoov * 2f);
		stats.getTurnAcceleration().modifyPercent(id, manoov * 2f);
		stats.getMaxTurnRate().modifyPercent(id, manoov * 2f);
            } else {
                stats.getMaxSpeed().modifyFlat(id, SPEED_BONUS);
                stats.getZeroFluxSpeedBoost().modifyFlat(id, -zluf);
		stats.getAcceleration().modifyPercent(id, manoov);
		stats.getDeceleration().modifyPercent(id, manoov);
		stats.getTurnAcceleration().modifyPercent(id, manoov);
		stats.getMaxTurnRate().modifyPercent(id, manoov);
            }
            
	}
        
        @Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
            if (!(ship.getVariant().hasHullMod("shield_shunt"))){
                ship.getVariant().removeMod("ugh_shuntthrusters");
                ship.getVariant().getSMods().remove("ugh_shuntthrusters");
                ship.getVariant().getSModdedBuiltIns().remove("ugh_shuntthrusters");
                ship.getVariant().removePermaMod("ugh_shuntthrusters");
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
                Color gray = Misc.getGrayColor();
                
                LabelAPI bullet;
                tooltip.setBulletedListMode(" • ");
		tooltip.addSectionHeading(UGH_MD.base("feature"), Alignment.MID, opad);
                bullet = tooltip.addPara(UGH_MD.shunt_t("im_speed"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), h,
                    "" + (int) SPEED_BONUS + "", "" + (int) ZERO_FLUX_PENALTY + "" );
		bullet.setHighlight(UGH_MD.shunt_t("im_speed_h"), "" + (int) SPEED_BONUS + "", "" + (int) ZERO_FLUX_PENALTY + "");
		bullet.setHighlightColors(gray, good, bad);
                bullet = tooltip.addPara(UGH_MD.shunt_t("im_shield"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), h,
                    "-0.2", "" + "1.0" + "", "2x" );
                bullet = tooltip.addPara(UGH_MD.shunt_t("im_stack"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), h,
                    "5x", "" + "0.4" + "" );
                //Efficiency description is simplified now.
                /*bullet = tooltip.addPara("If original Shield efficiency is lower than %s, the effect amplified by %s.", LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), h,
                    "" + "0.7" + "", "3x" );
                bullet = tooltip.addPara("If original Shield efficiency is lower than %s, the effect amplified by %s.", LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), h,
                    "" + "0.5" + "", "4x" );
                bullet = tooltip.addPara("If original Shield efficiency is lower or equal to %s, the effect amplified by %s.", LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), h,
                    "" + "0.5" + "", "5x" );*/
                
		tooltip.addSectionHeading(UGH_MD.base("compat"), Alignment.MID, opad);
                bullet = tooltip.addPara(UGH_MD.shunt_t("comp_Shun"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), bad,
                    "" + "" + "");
		bullet.setHighlight(UGH_MD.shunt_t("comp_Shun_h"));
		bullet.setHighlightColors(h);
                bullet = tooltip.addPara(UGH_MD.shunt_t("comp_Warn"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), bad,
                    "" + "" + "");
		bullet.setHighlight(UGH_MD.shunt_t("comp_Warn_h"));
		bullet.setHighlightColors(bad);
                    tooltip.setBulletedListMode("   ");
                    tooltip.addPara("%s", 0f, Global.getSettings().getColor("standardTextColor"), gray, UGH_MD.base("shunt_warn"));
                tooltip.setBulletedListMode(" • ");
                
                /*
                bullet = tooltip.addPara(UGH_MD.str("Shunt_T_comp_Shield"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), h,
                    "2x");
		bullet.setHighlight(UGH_MD.str("Shunt_T_comp_Shield_h"), UGH_MD.str("Shunt_T_comp_Shield_h2"));
		bullet.setHighlightColors(h, h);
                */
                
            tooltip.setBulletedListMode(null);
	}
        
        @Override
        public String getDescriptionParam(int index, HullSize hullSize) {
            /*if (index == 0) return "10";
            if (index == 1) return "1";
            if (index == 2) return "0.7";
            if (index == 3) return "Shield Shunt";*/
            return null;
        }
	
	@Override
        public String getUnapplicableReason(ShipAPI ship) {
            if (!(ship.getVariant().hasHullMod("shield_shunt"))) {
                return "Shield Shunt is not installed";
            }
            return null;
        }
        
        @Override
	public String getSModDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "" + (int) (SMODIFIER) + "x";
            if (index == 1) return "" + (int) (SMODIFIER2) + "%";
            return null;
	}
        
	@Override
	public boolean canBeAddedOrRemovedNow(ShipAPI ship, MarketAPI marketOrNull, CoreUITradeMode mode) {
		if (ship == null || ship.getVariant() == null) return true; // autofit
		if (!ship.getVariant().hasHullMod("ugh_shuntthrusters")) return true; // can always add

                if (ship.getVariant().hasHullMod("shield_shunt")) {
                    return false;
                }
		return true;
	}

	@Override
	public String getCanNotBeInstalledNowReason(ShipAPI ship, MarketAPI marketOrNull, CampaignUIAPI.CoreUITradeMode mode) {
		return "Removing the Shield Shunt will remove the thruster augment";
	}

        @Override
	public boolean isApplicableToShip(ShipAPI ship) {
            return ship != null && ((ship.getVariant().hasHullMod("shield_shunt")));
	}
}