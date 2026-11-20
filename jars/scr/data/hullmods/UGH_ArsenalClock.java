package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipAPI;

    public class UGH_ArsenalClock extends BaseHullMod {
        private static final String ugh_arse_clock = "UGH_ArsenalClock";
        
        @Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
            boolean sMod = isSMod(stats);
            
            stats.getFluxDissipation().modifyMult(id, (sMod ? 1 : 0.9f));
	}
		
        @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "10%";
            if (index == 1) return "25%";
            if (index == 2) return "50%";
            if (index == 3) return "1/4";
            return null;
        }
        
        @Override
	public String getSModDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "1/3";
            if (index == 1) return "30%";
            return null;
	}
        
        @Override
	public void advanceInCombat(ShipAPI ship, float amount) {
            MutableShipStatsAPI stats = ship.getMutableStats();
            boolean sMod = isSMod(stats);
            
            ShipAPI playerShip = Global.getCombatEngine().getPlayerShip();
            float noLossTime = ship.getMutableStats().getPeakCRDuration().computeEffective(ship.getHullSpec().getNoCRLossTime());
            float PeakTimer = ship.getTimeDeployedForCRReduction();
            float ArsenalMult = 0f;
            float ThreshMath = 0.75f;
            float sMoth = 1.25f;
            if (sMod) {
                ThreshMath = 0.67f;
                sMoth = 1.30f;
            }
		
            if (PeakTimer < (ThreshMath * noLossTime)) { //Ugh, threshold math is hard
                ArsenalMult = sMoth - ((noLossTime - (PeakTimer + (PeakTimer * (1f / ThreshMath - 1f)))) / noLossTime) * (sMod ? 0.3f : 0.25f); 
            } else if (PeakTimer >= (ThreshMath * noLossTime)) { ArsenalMult = sMoth; }
            else ArsenalMult = 0f; 
            
            if (noLossTime > PeakTimer) {
                stats.getAutofireAimAccuracy().modifyMult(ugh_arse_clock, ArsenalMult * 2);
                stats.getBallisticWeaponDamageMult().modifyMult(ugh_arse_clock, ArsenalMult);
                stats.getEnergyWeaponDamageMult().modifyMult(ugh_arse_clock, ArsenalMult);
                
		if (ship == playerShip){
                    Global.getCombatEngine().maintainStatusForPlayerShip("ugh_arse_clock_ui1", "graphics/icons/hullsys/active_flare_launcher.png",
                        "Arsenal Clock: ","" + Math.round(ArsenalMult * 100 - 100) + "% Increased Damage",false);
		}
            } else if (noLossTime < PeakTimer) {
                stats.getAutofireAimAccuracy().unmodify(ugh_arse_clock);
                stats.getBallisticWeaponDamageMult().unmodify(ugh_arse_clock);
                stats.getEnergyWeaponDamageMult().unmodify(ugh_arse_clock);
            }
	}
}

// Left for preservation
            /*if (PeakTimer < (0.75 * noLossTime)) { //Ugh, threshold math is hard
                ArsenalMult = 1.25f - (((noLossTime - (PeakTimer + (PeakTimer * (1f/0.75f -1f)))) / noLossTime) * 0.25f); 
            } else if (PeakTimer >= (0.75 * noLossTime)) { ArsenalMult = 1.25f; }*/