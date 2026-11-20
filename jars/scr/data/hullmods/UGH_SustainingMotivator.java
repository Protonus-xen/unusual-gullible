package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;
import data.scripts.util.UGH_MD;

public class UGH_SustainingMotivator extends BaseLogisticsHullMod {

        @Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
            boolean sMod = isSMod(stats);
		stats.getSuppliesPerMonth().modifyPercent(id, 20f);
		stats.getCRLossPerSecondPercent().modifyPercent(id, (sMod ? 0 : 25f));
		stats.getMaxCombatReadiness().modifyFlat(id, (sMod ? 0 : 0.15f));
		stats.getPeakCRDuration().modifyPercent(id, (sMod ? 15f : 0));
                
		stats.getMaxSpeed().modifyPercent(id, (sMod ? 5f : 0));
		stats.getAcceleration().modifyPercent(id, (sMod ? 5f : 0));
		stats.getDeceleration().modifyPercent(id, (sMod ? 5f : 0));
		stats.getTurnAcceleration().modifyPercent(id, (sMod ? 5f : 0));
		stats.getMaxTurnRate().modifyPercent(id, (sMod ? 5f : 0));
                
		stats.getArmorDamageTakenMult().modifyPercent(id, (sMod ? -5f : 0));
		stats.getHullDamageTakenMult().modifyPercent(id, (sMod ? -5f : 0));
		stats.getShieldDamageTakenMult().modifyPercent(id, (sMod ? -5f : 0));
                
		stats.getFighterRefitTimeMult().modifyPercent(id, (sMod ? -5f : 0));
		stats.getAutofireAimAccuracy().modifyFlat(id, (sMod ? 25f : 0));
	}
	
        @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "15%";
            if (index == 1) return "20%";
            if (index == 2) return "25%";
            return null;
        }
        
        @Override
	public String getSModDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "85%";
            if (index == 1) return UGH_MD.susm("bad_stuff");
            if (index == 2) return "15%";
            return null;
	}
        
        @Override
        public boolean isApplicableToShip(ShipAPI ship) {
            return !((ship.getVariant().hasHullMod(HullMods.AUTOMATED) && ship.getVariant().getHullSpec().getMinCrew() == 0f) ||
                    (ship.getVariant().hasHullMod("ocua_drone_mod") && ship.getHullSpec().getMinCrew() <= 1f));
        }
    
        @Override
        public String getUnapplicableReason(ShipAPI ship) {
            if ((ship.getVariant().getHullMods().contains("automated") && ship.getHullSpec().getMinCrew() == 0f) ||
                    (ship.getVariant().getHullMods().contains("ocua_drone_mod") && ship.getHullSpec().getMinCrew() <= 1f)) {
               return UGH_MD.susm("crew");
            }
        
            return null;
        }
}