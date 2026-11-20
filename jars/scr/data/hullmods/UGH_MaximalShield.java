package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class UGH_MaximalShield extends BaseHullMod {
        public static final float UPKEEP_MULT = 3.5f;
        public static final float UPKEEP_SMODIFIED = 2.5f;
        
        public static final float SHIELD_MULT = 42f;
        public static final float SHIELD_SMODIFIED = 33f;
        
        public static final float SHIELD_SPEED = 0.5f;
        
        public static final float SMODIFIER = 5f;
	
        @Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
            boolean sMod = isSMod(stats);
            
            stats.getShieldDamageTakenMult().modifyMult(id, (sMod ? SHIELD_SMODIFIED * 0.01f : SHIELD_MULT * 0.01f));
            stats.getDynamic().getStat(Stats.SHIELD_PIERCED_MULT).modifyMult(id, 0f);
            stats.getShieldArcBonus().modifyMult(id, 0.5f);
            stats.getShieldTurnRateMult().modifyMult(id, SHIELD_SPEED);
            stats.getShieldUnfoldRateMult().modifyMult(id, SHIELD_SPEED);
            stats.getShieldUpkeepMult().modifyMult(id, (sMod ? UPKEEP_SMODIFIED : UPKEEP_MULT));
	}
	
        @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
            float shield = (Math.round(100f * (100f / SHIELD_MULT))) / 100f;
            if (index == 0) return "" + (float) shield + "x";
            if (index == 1) return "0.5x";
            if (index == 2) return "" + (int) (100f - (SHIELD_SPEED * 100f)) + "%";
            if (index == 3) return "" + (float) UPKEEP_MULT + "x";
            return null;
        }
        
        @Override
	public String getSModDescriptionParam(int index, HullSize hullSize) {
            float shield = (Math.round((100f / SHIELD_SMODIFIED)));
            if (index == 0) return "" + (float) shield + "x";
            if (index == 1) return "" + (float) UPKEEP_SMODIFIED  + "x";
            return null;
	}
	
        @Override
	public String getUnapplicableReason(ShipAPI ship) {
		if (ship == null || ship.getShield() == null) return "Ship has no shields";
            return null;
	}
		
        @Override
	public boolean isApplicableToShip(ShipAPI ship) {
            return ship != null && (ship.getShield() != null) && 
                    (ship.getHullSpec().getDefenseType() == ShieldAPI.ShieldType.FRONT || 
                    ship.getHullSpec().getDefenseType() == ShieldAPI.ShieldType.OMNI);
	}
}