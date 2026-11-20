package data.hullmods;

import com.fs.starfarer.api.Global;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShieldAPI.ShieldType;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.UGH_MD;
import java.awt.Color;

public class UGH_AntiterminalBreakers extends BaseHullMod {
        private static final String ugh_antiterminal_id = "UGH_AntiterminalBreakers";
        public boolean BREAKER_USED = false;
        public static final float BASE_SHIELD = 10f;
	public static final float REPAIR_BONUS = 50f;
	public static final float ARMOR_DAMAGE_MULT = 50f;
	public static final float MINIMUM_ARMOR_MULT = 2f;
	public static final Color JITTER_COLOR = new Color(220,40,190,125);
        
        public static final float SMODIFIER = 10f;
        public static final float SMODIFIER2 = 2f;
        public static final float SMODIFIER3 = 67f;
	
        
        @Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
            boolean sMod = isSMod(stats);
            
            stats.getShieldDamageTakenMult().modifyMult(id, 1f + (sMod ? 0 : BASE_SHIELD) * 0.01f);
            stats.getOverloadTimeMod().modifyMult(id, 1f - (sMod ? SMODIFIER : 99) * 0.01f);
	}
        
        @Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
            if (ship.getVariant().hasHullMod("safetyoverrides")) {
                ship.getVariant().removeMod("ugh_antiterminalbreakers");
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
                
                LabelAPI bullet;
                
		tooltip.addSectionHeading(UGH_MD.base("improv"), Alignment.MID, opad);
                tooltip.setBulletedListMode("");
                bullet = tooltip.addPara(UGH_MD.atb("ov_start"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), h,
                    "5/6/7/9");
                tooltip.setBulletedListMode(" ^ ");
                bullet = tooltip.addPara(UGH_MD.atb("ov_arm"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "+" + (int) ARMOR_DAMAGE_MULT + "%");
                bullet = tooltip.addPara(UGH_MD.atb("ov_min"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "" + (int) MINIMUM_ARMOR_MULT + "x");
                bullet = tooltip.addPara(UGH_MD.atb("ov_repair"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "-" + (int) REPAIR_BONUS + "%");
                
                tooltip.setBulletedListMode(" • ");
		tooltip.addSectionHeading(UGH_MD.base("bad"), Alignment.MID, opad);
                bullet = tooltip.addPara(UGH_MD.atb("bad_shield"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), bad,
                    "+" + (int) BASE_SHIELD + "%");
                
		tooltip.addSectionHeading(UGH_MD.base("compat"), Alignment.MID, opad);
                bullet = tooltip.addPara(UGH_MD.atb("comp_shield"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "" + "" + "");
                bullet = tooltip.addPara(UGH_MD.atb("comp_safe"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "" + "" + "");
		bullet.setHighlight(UGH_MD.atb("comp_safety_h"));
		bullet.setHighlightColors(h);
                
            tooltip.setBulletedListMode(null);
	}
        
        @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
            /*if (index == 0) return "" + (int) ((BASE_SHIELD - 1f) * 100) + "%";
            if (index == 1) return "Should the ship overload";
            if (index == 2) return "50%";
            if (index == 3) return "" + (int) REPAIR_BONUS + "%";
            if (index == 4) return "2/3/4/5";
            if (index == 5) return "Safety Overrides";*/
            return null;
        }
        
        @Override
	public String getSModDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "" + (int) SMODIFIER + "";
            if (index == 1) return "" + (int) SMODIFIER2 + "";
            if (index == 2) return "-" + (int) SMODIFIER3 + "%";
            return null;
	}

        @Override
	public String getUnapplicableReason(ShipAPI ship) {
		if (ship == null || ship.getShield() == null) return "Ship has no shields";
                if ((ship.getVariant().hasHullMod("safetyoverrides"))) {
                    return "Safety Overrides is installed";
                }
		return null;
	}
        
        @Override
	public boolean isApplicableToShip(ShipAPI ship) {
            return ship != null && (ship.getShield() != null) &&
                    !ship.getVariant().hasHullMod("safetyoverrides");
	}
	
        @Override
        public void advanceInCombat(ShipAPI ship, float amount) {
            if (!ship.isAlive()) return;
            MutableShipStatsAPI stats = ship.getMutableStats();
            boolean sMod = isSMod(stats);
            ShipAPI playerShip = Global.getCombatEngine().getPlayerShip();
            //ship.getMutableStats().getDynamic().getStat("ugh_ATB_log").modifyFlat("ugh_antiterminal_nan" , -1f);
            ship.getMutableStats().getDynamic().getStat("ugh_ATB_clock").modifyFlat("ugh_antiterminal_table" , -1f);
            float ATB_clock = ship.getMutableStats().getDynamic().getStat("ugh_ATB_clock").getModifiedValue();
            
            float TimeSize;
            if (ship.getHullSpec().getHullSize() == HullSize.FRIGATE) TimeSize = 5f + (sMod ? SMODIFIER2 : 0);
            else if (ship.getHullSpec().getHullSize() == HullSize.DESTROYER) TimeSize = 6f + (sMod ? SMODIFIER2 : 0);
            else if (ship.getHullSpec().getHullSize() == HullSize.CRUISER) TimeSize = 7f + (sMod ? SMODIFIER2 : 0);
            else if (ship.getHullSpec().getHullSize() == HullSize.CAPITAL_SHIP) TimeSize = 9f + (sMod ? SMODIFIER2 + 1f : 0);
            else TimeSize = 7f;
			
            float maxRangeBonus = 1f;
            
            if (BREAKER_USED == false){
                if (ship.getFluxTracker().isOverloaded() == true || ATB_clock > 0f) {
                    ship.getMutableStats().getDynamic().getStat("ugh_ATB_clock").modifyFlat("ugh_ATB_cc" , 
                        ATB_clock + amount);
                }
                if (ATB_clock > TimeSize){
                    BREAKER_USED = true;
                    ship.getMutableStats().getDynamic().getStat("ugh_ATB_clock").modifyFlat("ugh_ATB_cc" , 0f);
                }
            } else {
                if (ship.getFluxTracker().isOverloaded() == false) {
                    BREAKER_USED = false;
                }
            }
            if (ATB_clock > 0f){
                stats.getArmorDamageTakenMult().modifyMult(ugh_antiterminal_id, ARMOR_DAMAGE_MULT * 0.01f);
                stats.getMinArmorFraction().modifyMult(ugh_antiterminal_id, MINIMUM_ARMOR_MULT);
                stats.getCombatEngineRepairTimeMult().modifyMult(ugh_antiterminal_id, 1f - (sMod ? SMODIFIER3 : REPAIR_BONUS) * 0.01f);
                stats.getCombatWeaponRepairTimeMult().modifyMult(ugh_antiterminal_id, 1f - (sMod ? SMODIFIER3 : REPAIR_BONUS) * 0.01f);

                ship.setJitter(this, JITTER_COLOR, 1, 3, 0f, 0 + maxRangeBonus);
                if (ship == playerShip) {
                    Global.getCombatEngine().maintainStatusForPlayerShip("ugh_antiterminal_ui1", "graphics/icons/hullsys/phase_cloak.png",
                    "Anti-Terminal - Timer: " + Math.round(TimeSize - ATB_clock) + " second(s)", "50% Armor damage reduction",false);
                }
            } else {
                stats.getArmorDamageTakenMult().unmodify(ugh_antiterminal_id);
                stats.getMinArmorFraction().unmodify(ugh_antiterminal_id);
                stats.getCombatEngineRepairTimeMult().unmodify(ugh_antiterminal_id);
                stats.getCombatWeaponRepairTimeMult().unmodify(ugh_antiterminal_id);
            }
        }

	/*@Override
	public boolean canBeAddedOrRemovedNow(ShipAPI ship, MarketAPI marketOrNull, CampaignUIAPI.CoreUITradeMode mode) {
		if (ship == null || ship.getVariant() == null) return true; // autofit
		if (!ship.getVariant().hasHullMod("ugh_antiterminalbreakers")) return true; // can always add

		return true;
	}*/
}
		