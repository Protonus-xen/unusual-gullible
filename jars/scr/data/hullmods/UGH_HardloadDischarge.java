package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShieldAPI.ShieldType;
import com.fs.starfarer.api.combat.ShipAPI;

public class UGH_HardloadDischarge extends BaseHullMod {
        private static final String ugh_hardload_id = "UGH_HardloadDischarge";
        public boolean HARD_LOADED = true;
        
        @Override
        public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getShieldDamageTakenMult().modifyMult(id , 1.20f);
	}
        
        @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "20%";
            if (index == 1) return "33%";
            if (index == 2) return "50%";
            if (index == 3) return "turning off the shield";
            if (index == 4) return "5/6/7/9";
            if (index == 5) return "shield is turned on";
            return null;
        }

        @Override
	public String getUnapplicableReason(ShipAPI ship) {
            if (ship == null || ship.getShield() == null)
                return "Ship has no shields";
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
            ship.getMutableStats().getDynamic().getStat("ugh_HDC_clock").modifyFlat("ugh_hardload_table" , -1f);
            ship.getMutableStats().getDynamic().getStat("ugh_HDC_load").modifyFlat("ugh_hardload_load" , -1f);
            float HardFlux = ship.getFluxTracker().getHardFlux();
            float MaxFlux = ship.getFluxTracker().getMaxFlux();
            float HDC_clock = ship.getMutableStats().getDynamic().getStat("ugh_HDC_clock").getModifiedValue();
            float HDC_load = ship.getMutableStats().getDynamic().getStat("ugh_HDC_load").getModifiedValue();
            /*float InFlux = 0;
            
            if (HardFlux < 1) { InFlux = HardFlux; }
            else if (HardFlux == 1) { InFlux = 1f; }
            else InFlux = 0f; */
            
            float TimeSize;
            if (ship.getHullSpec().getHullSize() == HullSize.FRIGATE) TimeSize = 5f;
            else if (ship.getHullSpec().getHullSize() == HullSize.DESTROYER) TimeSize = 6f;
            else if (ship.getHullSpec().getHullSize() == HullSize.CRUISER) TimeSize = 7f;
            else if (ship.getHullSpec().getHullSize() == HullSize.CAPITAL_SHIP) TimeSize = 9f;
            else TimeSize = 7f;
            
            if (HARD_LOADED == true) {
                if (ship.getFluxTracker().isOverloaded() == true && ship.getMutableStats().getDynamic().getStat("ugh_HDC_clock").getModifiedValue() > 0f) {
                    ship.getMutableStats().getDynamic().getStat("ugh_HDC_clock").modifyFlat("ugh_HDC_cc" , 0f);
                }
                if (ship.getShield().isOn() == false && HardFlux > 0){
                    ship.getMutableStats().getDynamic().getStat("ugh_HDC_clock").modifyFlat("ugh_HDC_cc" , 
                            ship.getMutableStats().getDynamic().getStat("ugh_HDC_clock").getModifiedValue() + amount);
                    if (HDC_load == 0) {
                        HDC_load = HardFlux / MaxFlux;
                    }
                    
                    stats.getBallisticWeaponDamageMult().modifyMult(ugh_hardload_id, 1f + (HDC_load / 3f));
                    stats.getEnergyWeaponDamageMult().modifyMult(ugh_hardload_id, 1f + (HDC_load / 3f));
                    stats.getArmorDamageTakenMult().modifyMult(ugh_hardload_id, 1f - (HDC_load / 2f));
                
                    if (ship == playerShip) {
                        Global.getCombatEngine().maintainStatusForPlayerShip("ugh_antiterminal_ui1", "graphics/icons/hullsys/phase_cloak.png",
                            "Hardload - Timer: " + Math.round(TimeSize - HDC_clock) + " second(s)", "Discharging",false);
                        Global.getCombatEngine().maintainStatusForPlayerShip("ugh_hardload_ui1", "graphics/icons/hullsys/phase_cloak.png",
                            "Hardload:", "+" + Math.round((100f * HDC_load) / 3f) + "% Damage",true);
                        Global.getCombatEngine().maintainStatusForPlayerShip("ugh_hardload_ui2", "graphics/icons/hullsys/phase_cloak.png",
                            "Hardload:", "-" + Math.round((100f * HDC_load) / 2f) + "% Armor damage taken",true);
                    }
                }
                if ((HDC_clock > TimeSize)){
                    HARD_LOADED = false;
                    ship.getMutableStats().getDynamic().getStat("ugh_HDC_clock").modifyFlat("ugh_HDC_cc" , 0f);
                    stats.getBallisticWeaponDamageMult().unmodify(ugh_hardload_id);
                    stats.getEnergyWeaponDamageMult().unmodify(ugh_hardload_id);
                    stats.getArmorDamageTakenMult().unmodify(ugh_hardload_id);
                } 
            } else {
                if (ship.getShield().isOn() == true) HARD_LOADED = true;
            }
            
            /* if (ship.getShield().isOn() == false){
                stats.getBallisticWeaponDamageMult().modifyMult(ugh_hardload_id, 1f + ((HardFlux / MaxFlux) / 4f));
                stats.getEnergyWeaponDamageMult().modifyMult(ugh_hardload_id, 1f + ((HardFlux / MaxFlux) / 4f));
                stats.getArmorDamageTakenMult().modifyMult(ugh_hardload_id, 1f - ((HardFlux / MaxFlux) / 4f));
                if (ship == playerShip) {
                    Global.getCombatEngine().maintainStatusForPlayerShip("ugh_hardload_ui1", "graphics/icons/hullsys/phase_cloak.png",
                    "Hardload:", "+" + Math.round((100f * (HardFlux / MaxFlux)) / 4f) + "% Damage",true);
                    Global.getCombatEngine().maintainStatusForPlayerShip("ugh_hardload_ui2", "graphics/icons/hullsys/phase_cloak.png",
                    "Hardload:", "+" + Math.round((100f * (HardFlux / MaxFlux)) / 4f) + "% Armor resistance",true);
                }
            }
            else {
		stats.getBallisticWeaponDamageMult().unmodify(ugh_hardload_id);
		stats.getEnergyWeaponDamageMult().unmodify(ugh_hardload_id);
                stats.getArmorDamageTakenMult().unmodify(ugh_hardload_id);
            }*/
	}
}