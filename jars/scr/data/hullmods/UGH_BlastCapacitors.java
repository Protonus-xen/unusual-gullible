package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class UGH_BlastCapacitors extends BaseHullMod {
	public static final float EN_FIRE = 20f;
	public static final float BEAM_RNG = 25f;
        
	public static final float SMODIFIER = 10f;
	public static final float SMODIFIER_BEAM = 15f;
	public static final float SMODIFIER2 = 5f;
        
        @Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
            boolean sMod = isSMod(stats);
            
            stats.getBeamWeaponDamageMult().modifyPercent(id, 30f);
            stats.getEnergyWeaponDamageMult().modifyPercent(id, 15f);
            stats.getEnergyRoFMult().modifyMult(id, (sMod ? (1f - (SMODIFIER * 0.01f)) : (1f - (EN_FIRE * 0.01f))));
            stats.getBeamWeaponRangeBonus().modifyMult(id, (sMod ? (1f - (SMODIFIER_BEAM * 0.01f)) : (1f - (BEAM_RNG * 0.01f))));
            stats.getEnergyWeaponFluxCostMod().modifyMult(id, (sMod ? (1f - (SMODIFIER2 * 0.01f)) : 1));
	}
        
        @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "" + 30 + "%";
            if (index == 1) return "" + 15 + "%";
            if (index == 2) return "" + (int) (EN_FIRE) + "%";
            if (index == 3) return "" + (int) (BEAM_RNG) + "%";
            return null;
        }
        
        @Override
	public String getSModDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "" + (int) (SMODIFIER) + "%";
            if (index == 1) return "" + (int) (SMODIFIER_BEAM) + "%";
            if (index == 2) return "" + (int) (SMODIFIER2) + "%";
            return null;
	}
}