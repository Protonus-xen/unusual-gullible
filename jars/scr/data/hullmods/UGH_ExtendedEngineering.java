package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;

public class UGH_ExtendedEngineering extends BaseLogisticsHullMod {

        @Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		boolean sMod = isSMod(stats);
            
		stats.getSystemUsesBonus().modifyFlat(id, (sMod ? 1 : 0));
		stats.getSystemRegenBonus().modifyPercent(id, 10f);
		stats.getSystemCooldownBonus().modifyMult(id, (sMod ? 0.8f : 0.9f));
		stats.getSystemRangeBonus().modifyPercent(id, (sMod ? 30f : 20f));
	}
	
        @Override
	public String getDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
		if (index == 0) return "10%";
		if (index == 1) return "10%";
		if (index == 2) return "20%";
                if (index == 3) return "counts as a Logistics hullmod";
		return null;
	}
        
        @Override
	public String getSModDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "1";
		if (index == 1) return "20%";
		if (index == 2) return "30%";
		return null;
	}
}
