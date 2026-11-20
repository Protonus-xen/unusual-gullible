package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class UGH_MagnetoBarrels extends BaseHullMod {
        @Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
            stats.getProjectileSpeedMult().modifyMult(id, 2f);
            stats.getBallisticWeaponDamageMult().modifyMult(id, 1.2f);
            stats.getEnergyWeaponDamageMult().modifyMult(id, 1.2f);
            
            stats.getBallisticRoFMult().modifyMult(id, 0.67f);
            stats.getEnergyRoFMult().modifyMult(id, 0.67f);
            stats.getBallisticWeaponFluxCostMod().modifyMult(id, 1.5f);
            stats.getEnergyWeaponFluxCostMod().modifyMult(id, 1.5f);
	}
        
        @Override
        public String getDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "2";
            if (index == 1) return "20%";
            if (index == 2) return "33%";
            if (index == 3) return "50%";
            return null;
        }
}