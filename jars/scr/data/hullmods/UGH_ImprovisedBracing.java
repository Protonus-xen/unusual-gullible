package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;
import java.util.HashMap;
import java.util.Map;

public class UGH_ImprovisedBracing extends BaseLogisticsHullMod {
	private static Map mag = new HashMap();
	static {
		mag.put(HullSize.FRIGATE, 100f);
		mag.put(HullSize.DESTROYER, 150f);
		mag.put(HullSize.CRUISER, 200f);
		mag.put(HullSize.CAPITAL_SHIP, 250f);
	}
	
        @Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getHullBonus().modifyPercent(id, 10f);
		stats.getArmorBonus().modifyFlat(id, (Float) mag.get(hullSize));
		stats.getPeakCRDuration().modifyPercent(id, 10f);
		stats.getSuppliesToRecover().modifyPercent(id, 20f);
	}
	
        @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "100" + "/" + "150" + "/" + "200" + "/" + "250";
            if (index == 1) return "10%";
            if (index == 2) return "10%";
            if (index == 3) return "20%";
            if (index == 4) return "count as a Logistics hullmod";
            return null;
        }
}