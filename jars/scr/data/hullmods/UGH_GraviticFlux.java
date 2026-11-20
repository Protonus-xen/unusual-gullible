package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipAPI;
import java.util.HashMap;
import java.util.Map;


public class UGH_GraviticFlux extends BaseHullMod {
        private static final String ugh_gravitic_id = "UGH_GraviticFlux";
        public static Map<String,Boolean> reDenser = new HashMap<String,Boolean>();
        private float baseMass = 0f;
        private float basedMass = 0f;
        
        @Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
            stats.getHullBonus().modifyPercent(id, 10f);
	}
	
        @Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
            baseMass = ship.getMass();
            ship.setMass(ship.getMass() + (ship.getMass() / 4f));
            basedMass = ship.getMass();
	}
	
        @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "10%";
            if (index == 1) return "25%";
            if (index == 2) return "75%";
            if (index == 3) return "double";
            if (index == 4) return "50%";
            return null;
        }
        
        @Override
	public void advanceInCombat(ShipAPI ship, float amount) {
            if (!ship.isAlive()) return;
            MutableShipStatsAPI stats = ship.getMutableStats();
            float Flux = ship.getFluxTracker().getFluxLevel();
            float InFlux = 0;
            
            if (Flux < 0.75) { InFlux = (Flux - 0.25f); }
            else if (Flux >= 0.75) { InFlux = 0.5f; }
            else InFlux = 0f;
            final float FluxMass = baseMass * InFlux * 2;
            
            if (Flux > 0) { 
                ship.setMass(basedMass + FluxMass);
            } else {
                ship.setMass(basedMass);
            }
            reDenser.put(ship.getId(), true);
            
            stats.getTurnAcceleration().modifyMult(ugh_gravitic_id, 1f - InFlux);
            stats.getAcceleration().modifyMult(ugh_gravitic_id, 1f - InFlux);
	}
}

			
			