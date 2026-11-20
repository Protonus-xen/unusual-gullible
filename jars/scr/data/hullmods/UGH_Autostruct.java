package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.UGH_MD;
import java.awt.Color;

public class UGH_Autostruct extends BaseHullMod {
        private static final String ugh_auto_repair = "UGH_Autostruct";

        protected static final float ENERGY_TAKE_MULT = 20f;
	
        @Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
            //stats.getEnergyDamageTakenMult().modifyMult(id, 1f + (ENERGY_TAKE_MULT * 0.01f));
	}

        @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
            /*
            if (index == 0) return "0.6%";
            if (index == 1) return "20%";
            if (index == 2) return "3%";
            if (index == 3) return "20%";
            if (index == 4) return "0.5%";
            if (index == 5) return "1%";
            if (index == 6) return "while Venting";
            */
            return null;
        }
	
        @Override
	public String getUnapplicableReason(ShipAPI ship) {
            return null;
	}
		
	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
            return ship != null;
	}

	
	@Override
        public void advanceInCombat(ShipAPI ship, float amount) {
            MutableShipStatsAPI stats = ship.getMutableStats();
            ShipAPI playerShip = Global.getCombatEngine().getPlayerShip();
		if (!ship.isAlive()) return;
                float Regen = 0.4f;
		float Hull = ship.getHitpoints();
		float MaxHull = ship.getMaxHitpoints();
		float MaxHullCap = Regen * (float) Math.min(Math.max(0, MaxHull - 12500f) / 50000f, 0.25f);
		//float MaxHullCap = Regen * (float) Math.max(0, Math.min(0.75, MaxHull - 12500f / 50000f));
		float HullPer = (Hull / MaxHull) * 100f;
		float HullPerSegment = (100f - HullPer ) / 20f;
                
                float noLossTime = ship.getMutableStats().getPeakCRDuration().computeEffective(ship.getHullSpec().getNoCRLossTime());
                float PeakTimer = ship.getTimeDeployedForCRReduction();
                float AutostructCRMult;
			
                if ((PeakTimer > (0.5 * noLossTime))) { //I unironically spent an entire day racking my brain on this calculation
                    AutostructCRMult = Math.min(Math.max((((noLossTime - PeakTimer / 1.165f) / (noLossTime * 0.5f))), 0.33f), 1f); 
                    // AutostructCRMult = 1f - (((noLossTime - PeakTimer) / (noLossTime * 0.5f)) * 0.67f); // Reference, DO NOT DELETE
                } else AutostructCRMult = 1f; 
            
                if (AutostructCRMult < 1f) {
                    if (ship == playerShip){
                        Global.getCombatEngine().maintainStatusForPlayerShip("ugh_auto_repair_ui1", "graphics/icons/hullsys/active_flare_launcher.png",
                            "Autostruct Effectiveness: ","" + Math.round(AutostructCRMult * 100) + "%",true);
                    }
                }
                
		if (Hull == MaxHull){
			stats.getMaxCombatHullRepairFraction().modifyFlat(ugh_auto_repair , 0f);
			stats.getHullCombatRepairRatePercentPerSecond().modifyFlat(ugh_auto_repair , 0f);
			stats.getFluxDissipation().modifyMult(ugh_auto_repair, 1f);
		}
                else {
			if (ship.getFluxTracker().isVenting() == true) {
				stats.getMaxCombatHullRepairFraction().modifyFlat(ugh_auto_repair , 0f);
				stats.getHullCombatRepairRatePercentPerSecond().modifyFlat(ugh_auto_repair , 0f);
				stats.getFluxDissipation().modifyMult(ugh_auto_repair, 1f);
			}
			else{
				stats.getMaxCombatHullRepairFraction().modifyFlat(ugh_auto_repair , 1f);
				stats.getHullCombatRepairRatePercentPerSecond().modifyFlat(ugh_auto_repair , (Regen - MaxHullCap) * HullPerSegment * AutostructCRMult);
				stats.getFluxDissipation().modifyMult(ugh_auto_repair, 1f - ((100f - HullPer) * 0.005f));
			}
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
            //if (isForModSpec || ship == null) return;
            
            LabelAPI bullet;
            tooltip.setBulletedListMode(" • ");
		tooltip.addSectionHeading(UGH_MD.base("improv"), Alignment.MID, opad);
                bullet = tooltip.addPara(UGH_MD.au_m("im_regen"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), h,
                    "" + "0.4" + "%", "" + "20" + "%", "" + "2" + "%");
		bullet.setHighlight("" + "0.4" + "%", "" + "20" + "%", "" + "2" + "%");
		bullet.setHighlightColors(good, h, h);
                
		tooltip.addSectionHeading(UGH_MD.base("bad"), Alignment.MID, opad);
                /*bullet = tooltip.addPara(UGH_MD.au_m("bad_en"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), bad,
                    "+" + (int) ENERGY_TAKE_MULT + "%");*/
                bullet = tooltip.addPara(UGH_MD.au_m("bad_flux"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), bad,
                    "" + "0.5" + "%", "" + "1" + "%");
		bullet.setHighlight("" + "0.5" + "%", "" + "1" + "%");
		bullet.setHighlightColors(bad, h);
                bullet = tooltip.addPara(UGH_MD.au_m("bad_vent"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), h,
                    "");
		bullet.setHighlight(UGH_MD.au_m("bad_vent_h"));
		bullet.setHighlightColors(h);
                
		tooltip.addSectionHeading(UGH_MD.base("note"), Alignment.MID, opad);
		bullet = tooltip.addPara(UGH_MD.au_m("note_logi"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), h,
			"");
		bullet.setHighlight(UGH_MD.au_m("note_logi_h"));
		bullet.setHighlightColors(h);
		bullet = tooltip.addPara(UGH_MD.au_m("note_dim"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), h,
			"12,500",
			"50,000",
			"1/4");
		bullet = tooltip.addPara(UGH_MD.au_m("note_peak"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), h,
			"35%",
			"1/3");
            
            tooltip.setBulletedListMode(null);
        }

}