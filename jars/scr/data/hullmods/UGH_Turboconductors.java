package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import java.util.HashMap;
import java.util.Map;

public class UGH_Turboconductors extends BaseHullMod {
	private static Map mag = new HashMap();
	static {
		mag.put(HullSize.FRIGATE, 30f);
		mag.put(HullSize.DESTROYER, 25f);
		mag.put(HullSize.CRUISER, 15f);
		mag.put(HullSize.CAPITAL_SHIP, 10f);
	}
        
        @Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getFluxCapacity().modifyMult(id, 1.5f);
		stats.getZeroFluxMinimumFluxLevel().modifyFlat(id, 0.01f);
		stats.getZeroFluxSpeedBoost().modifyFlat(id, (Float) mag.get(hullSize));
                stats.getSuppliesPerMonth().modifyPercent(id, 25f);
                stats.getSuppliesToRecover().modifyPercent(id, 100f);
	}
	
        @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "1.5";
            if (index == 1) return "1%";
            if (index == 2) return "" + ((Float) mag.get(HullSize.FRIGATE)).intValue() + "/"
                     + ((Float) mag.get(HullSize.DESTROYER)).intValue() + "/"
                     + ((Float) mag.get(HullSize.CRUISER)).intValue() + "/"
                     + ((Float) mag.get(HullSize.CAPITAL_SHIP)).intValue();
            if (index == 3) return "25%";
            if (index == 4) return "100%";
            return null;
        }
}