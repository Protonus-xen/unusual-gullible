package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipAPI;

public class UGH_StandoffCalcutron extends BaseHullMod {
        private static final String ugh_standoff_id = "UGH_StandoffCalcutron";
	
        @Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
            stats.getBallisticWeaponRangeBonus().modifyPercent(id, 10f);
            stats.getEnergyWeaponRangeBonus().modifyPercent(id, 10f);
		
            if (hullSize == HullSize.FRIGATE) {
                stats.getPeakCRDuration().modifyMult(id, 0.9f);
            } else if (hullSize == HullSize.DESTROYER) {
                stats.getPeakCRDuration().modifyMult(id, 0.8f);
            } else if (hullSize == HullSize.CRUISER) {
                stats.getPeakCRDuration().modifyMult(id, 0.7f);
            } else if (hullSize == HullSize.CAPITAL_SHIP) {
                stats.getPeakCRDuration().modifyMult(id, 0.6f);
            }
	}
	
        @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "10%";
            if (index == 1) return "25%";
            if (index == 2) return "20%";
            if (index == 3) return "70%";
            if (index == 4) return "25%";
            if (index == 5) return "10%/20%/30%/40%";
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
            if (!ship.isAlive()) return;
            float Flux = ship.getFluxTracker().getFluxLevel();
            float InFlux = 0;
            ShipAPI playerShip = Global.getCombatEngine().getPlayerShip();
            MutableShipStatsAPI stats = ship.getMutableStats();
            
            if (Flux > 0.2 && Flux < 0.7) { InFlux = Flux - 0.2f; }
            else if (Flux >= 0.7) { InFlux = 0.5f; }
            else InFlux = 0f; 
            
            if (ship.isAlive()){
                stats.getBallisticRoFMult().modifyPercent(ugh_standoff_id, 25f - (25f * InFlux * 2f));
                stats.getBallisticWeaponFluxCostMod().modifyMult(ugh_standoff_id, 1.25f - (0.25f * InFlux * 2f));
                stats.getEnergyWeaponDamageMult().modifyPercent(ugh_standoff_id, 25f * InFlux * 2f);
                stats.getEnergyWeaponFluxCostMod().modifyMult(ugh_standoff_id, 1f - (0.25f * InFlux * 2f));
                if (ship == playerShip){
                    Global.getCombatEngine().maintainStatusForPlayerShip("ugh_standoff_RoF", "graphics/icons/hullsys/high_energy_focus.png",
                        "Standoff Ballistics: ","+" + Math.round((0.25f - (0.25f * InFlux * 2f)) * 100f) + "% Rate Of Fire",false);
                    Global.getCombatEngine().maintainStatusForPlayerShip("ugh_standoff_DpS", "graphics/icons/hullsys/high_energy_focus.png",
                        "Standoff Energy: ","+" + Math.round((0f + (0.25f * InFlux * 2f)) * 100f) + "% Damage",false);
                } else {
                    stats.getBallisticRoFMult().unmodify(ugh_standoff_id);
                    stats.getBallisticWeaponFluxCostMod().unmodify(ugh_standoff_id);
                    stats.getEnergyWeaponDamageMult().unmodify(ugh_standoff_id);
                    stats.getEnergyWeaponFluxCostMod().unmodify(ugh_standoff_id);
                }
            }
        }
}