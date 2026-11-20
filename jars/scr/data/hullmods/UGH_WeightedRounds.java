package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class UGH_WeightedRounds extends BaseHullMod {
	public static final float BULLET_SPEED = 33f;
        
	public static final float SMODIFIER = 20f;
        
        @Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
            boolean sMod = isSMod(stats);
            
            stats.getProjectileSpeedMult().modifyMult(id, (sMod ? (1f - (SMODIFIER * 0.01f)) : (1f - (BULLET_SPEED * 0.01f))));
            stats.getBallisticWeaponRangeBonus().modifyMult(id, (sMod ? 1 : 0.9f));
            stats.getBallisticWeaponDamageMult().modifyMult(id, 1.2f);
	}
        
        @Override
        public String getDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "20%";
            if (index == 1) return "" + (int) (BULLET_SPEED) + "%";
            if (index == 2) return "10%";
            return null;
        }
        
        @Override
	public String getSModDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "" + (int) (SMODIFIER) + "%";
            return null;
	}
}