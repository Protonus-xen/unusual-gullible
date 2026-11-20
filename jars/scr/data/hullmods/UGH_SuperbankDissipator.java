package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;
import java.util.HashMap;
import java.util.Map;

public class UGH_SuperbankDissipator extends BaseLogisticsHullMod {
	/*private static Map mag = new HashMap();
	static {
		mag.put(HullSize.FRIGATE, 10f);
		mag.put(HullSize.DESTROYER, 25f);
		mag.put(HullSize.CRUISER, 40f);
		mag.put(HullSize.CAPITAL_SHIP, 75f);
	}*/
        
        @Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getFluxDissipation().modifyMult(id, 1.25f);
                
		stats.getHullBonus().modifyMult(id, 0.8f);
		/*stats.getMinCrewMod().modifyFlat(id, (Float) mag.get(hullSize));
                stats.getMinCrewMod().modifyPercent(id, 10f);*/
		stats.getOverloadTimeMod().modifyMult(id, 1.33f);
		stats.getVentRateMult().modifyMult(id, 0.67f);
                stats.getSuppliesPerMonth().modifyPercent(id, 25f);
                stats.getSuppliesToRecover().modifyPercent(id, 25f);
	}
	
        @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "1.25";
            if (index == 1) return "20%";
            /*if (index == 2) return "" + ((Float) mag.get(HullSize.FRIGATE)).intValue() + "/"
                     + ((Float) mag.get(HullSize.DESTROYER)).intValue() + "/"
                     + ((Float) mag.get(HullSize.CRUISER)).intValue() + "/"
                     + ((Float) mag.get(HullSize.CAPITAL_SHIP)).intValue() + "+ 10%";*/
            if (index == 2) return "50%";
            if (index == 3) return "33%";
            if (index == 4) return "25%";
            if (index == 5) return "counts as a Logistics hullmod";
            return null;
        }
}