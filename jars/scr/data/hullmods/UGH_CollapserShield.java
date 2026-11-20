package data.hullmods;

import com.fs.starfarer.api.Global;
import java.awt.*;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShieldAPI.ShieldType;
import com.fs.starfarer.api.combat.ShipAPI;

public class UGH_CollapserShield extends BaseHullMod {
        private static final String ugh_collapser_id = "UGH_CollapserShield";
        
        public static float SHIELD_MULT = 1.5f;
        public static float SHIELD_SMODIFIED = 1.2f;
	
        public static Color CENTER_COLOR = new Color(0.37f, 0f, 0.60f, 0.60f);
        public static Color RING_COLOR = new Color(0.20f, 0f, 0.30f, 0.40f);
	
        @Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
            boolean sMod = isSMod(stats);
            
            stats.getShieldDamageTakenMult().modifyMult(id, (sMod ? SHIELD_SMODIFIED : SHIELD_MULT));
	}
        
        @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "" + (int) (SHIELD_MULT * 100f - 100f) + "%";
            if (index == 1) return "50%";
            if (index == 2) return "80%";
            if (index == 3) return "50%";
            if (index == 4) return "25%";
            return null;
        }
        
        @Override
	public String getSModDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "" + (int) (SHIELD_SMODIFIED * 100f - 100f) + "%";
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
                    (ship.getHullSpec().getDefenseType() == ShieldType.FRONT || 
                    ship.getHullSpec().getDefenseType() == ShieldType.OMNI);
	}
	
        @Override
        public void advanceInCombat(ShipAPI ship, float amount) {
            if (ship == null) return;
            if (!ship.isAlive()) return;
            if (ship.getShield() == null) return;
            MutableShipStatsAPI stats = ship.getMutableStats();
            ShipAPI playerShip = Global.getCombatEngine().getPlayerShip();
            float Flux = ship.getFluxTracker().getFluxLevel();
            float InFlux = 0;
            float puttyFlux = 0;
            float bounceFlux = 0;
			
            ship.getShield().setInnerColor(CENTER_COLOR);
            ship.getShield().setRingColor(RING_COLOR);
			
            if (Flux < 0.67f) { 
                InFlux = Flux / (1f / 0.67f); 
                puttyFlux = Flux * 0.7462f; // 50/67
                bounceFlux = Flux / 0.8375f;
            }
            else if (Flux >= 0.67f) { 
                InFlux = 0.5f; 
                puttyFlux = 0.5f;
                bounceFlux = 0.80f;
            }
            else InFlux = 0f; 
            
            stats.getKineticShieldDamageTakenMult().modifyMult(ugh_collapser_id, 1f - bounceFlux);
            stats.getFragmentationShieldDamageTakenMult().modifyMult(ugh_collapser_id, 1f - puttyFlux);
            stats.getHardFluxDissipationFraction().modifyFlat(ugh_collapser_id, InFlux / 2f);
            
            if (Flux > 0f){
                if (ship == playerShip) {
                    if (ship.getShield().isOn()) {
                        Global.getCombatEngine().maintainStatusForPlayerShip("ugh_collapser_ui1", "graphics/icons/hullsys/phase_cloak.png",
                            "Collapser:", "" + Math.round(bounceFlux * 100) + "% Kinetic Shield",true);
                        Global.getCombatEngine().maintainStatusForPlayerShip("ugh_collapser_ui2", "graphics/icons/hullsys/phase_cloak.png",
                            "Collapser:", "" + Math.round(puttyFlux * 100) + "% Fragmentation Shield",true);
                    }
                }
            }
            
            if (ship.getShield() == null) {
                
            } else {
                ship.getShield().setRingColor(CENTER_COLOR);
                ship.getShield().setInnerColor(RING_COLOR);
            }
        }
}
		