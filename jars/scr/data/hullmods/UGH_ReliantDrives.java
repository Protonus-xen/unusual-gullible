package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;
import java.util.HashMap;
import java.util.Map;

public class UGH_ReliantDrives extends BaseLogisticsHullMod {
	private static Map mag = new HashMap();
	private static Map mag2 = new HashMap();
	private static Map meg = new HashMap();
	static {
		mag.put(HullSize.FRIGATE, 50f);
		mag.put(HullSize.DESTROYER, 40f);
		mag.put(HullSize.CRUISER, 30f);
		mag.put(HullSize.CAPITAL_SHIP, 20f);
                
		mag2.put(HullSize.FRIGATE, 5f);
		mag2.put(HullSize.DESTROYER, 5f);
		mag2.put(HullSize.CRUISER, 15f);
		mag2.put(HullSize.CAPITAL_SHIP, 25f);
                
		meg.put(HullSize.FRIGATE, 10f);
		meg.put(HullSize.DESTROYER, 20f);
		meg.put(HullSize.CRUISER, 35f);
		meg.put(HullSize.CAPITAL_SHIP, 50f);
	}
	
        @Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
            boolean sMod = isSMod(stats);
            
		stats.getZeroFluxSpeedBoost().modifyFlat(id , (sMod ? 40 : (float) mag.get(hullSize)));
                
                stats.getEngineHealthBonus().modifyPercent(id, (sMod ? (float) meg.get(hullSize) : 0));
		stats.getAcceleration().modifyPercent(id, (sMod ? (float) mag2.get(hullSize) : 0));
		stats.getDeceleration().modifyPercent(id, (sMod ? (float) mag2.get(hullSize) : 0));
		stats.getTurnAcceleration().modifyPercent(id, (sMod ? (float) mag2.get(hullSize) : 0));
		stats.getMaxTurnRate().modifyPercent(id, (sMod ? (float) mag2.get(hullSize) : 0));
		stats.getMaxBurnLevel().modifyFlat(id, (sMod ? 1 : 0));
	}
        
	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		return (ship.getHullSize() == HullSize.CAPITAL_SHIP
                        || ship.getHullSize() == HullSize.CRUISER
                        || ship.getHullSize() == HullSize.DESTROYER);
	}
        
        @Override
	public String getUnapplicableReason(ShipAPI ship) {
		if (ship != null && ship.getHullSize() == HullSize.FRIGATE) {
			return "Cannot be installed on Frigates";
		}
		return null;
	}
	
        @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "40" + "/" + "30" + "/" + "20";
            if (index == 1) return "counts as a Logistics hullmod";
            return null;
        }
        
        @Override
	public String getSModDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "40";
            if (index == 1) return "20%" + "/" + "35%" + "/" + "50%";
            if (index == 2) return "1";
            return null;
	}
}

			
			