package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.util.IntervalUtil;

public class UGH_AuxOrdinanceForge extends BaseHullMod {

    public static final float RELOAD_TIME = 30f;
    public static final float LIM_AMMO = 25f;
    
    
    public static final float AMMO_BONUS = 25f;
    public static final float AMMO_SMOD = 50f;
    public static final float SMOD1 = 6f;

    public static String DA_KEE = "ugh_aux_ord_forge_key";

        public static class AmmoRegenClass { IntervalUtil interval = new IntervalUtil(RELOAD_TIME, RELOAD_TIME); }

        @Override
        public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
            boolean sMod = isSMod(stats);
            
            stats.getWeaponTurnRateBonus().modifyMult(id, (sMod ? 1 : 0.9f));
            stats.getBallisticAmmoRegenMult().modifyMult(id, (sMod ? (1f + (AMMO_SMOD * 0.01f)) : (1f + (AMMO_BONUS * 0.01f))));
            stats.getEnergyAmmoRegenMult().modifyMult(id, (sMod ? (1f + (AMMO_SMOD * 0.01f)) : (1f + (AMMO_BONUS * 0.01f))));
        }
/*
        @Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
            for (WeaponAPI gun : ship.getAllWeapons()) {
                if (gun.getType() != WeaponType.ENERGY || gun.getType() != WeaponType.BALLISTIC) continue;
                float AmmoRegen = gun.getSpec().getAmmoPerSecond();
                float ammo_set = AmmoRegen * (1f + (LIM_AMMO / 100f));
                if (gun.usesAmmo() && AmmoRegen > 0.00f) gun.getAmmoTracker().setAmmoPerSecond(ammo_set);
            }
	}
*/	
        @Override
        public String getDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
            if (index == 0) return "" + (int) AMMO_BONUS + "%";
            if (index == 1) return "" + (int) RELOAD_TIME;
            if (index == 2) return "10 units";
            if (index == 3) return "10%";
            if (index == 4) return "10%";
            return null;
        }
        
        @Override
	public String getSModDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "" + (int) (AMMO_SMOD - AMMO_BONUS) + "%";
            if (index == 1) return "" + (int) SMOD1 + " units";
            return null;
	}

        @Override
        public void advanceInCombat(ShipAPI ship, float amount) {
            MutableShipStatsAPI stats = ship.getMutableStats();
            boolean sMod = isSMod(stats);
            
            super.advanceInCombat(ship, amount);
            if (!ship.isAlive()) return;
            CombatEngineAPI engine = Global.getCombatEngine();

            String key = DA_KEE + "_" + ship.getId();
            
            AmmoRegenClass amreg = (AmmoRegenClass) engine.getCustomData().get(key);
            if (amreg == null) {
                amreg = new AmmoRegenClass();
                engine.getCustomData().put(key, amreg);
            }

            boolean hasAmmo = false;
            for (WeaponAPI gun : ship.getAllWeapons()) {
                if (gun.getType() != WeaponType.ENERGY && gun.getType() != WeaponType.BALLISTIC) continue;
                if (gun.usesAmmo() && gun.getAmmo() < gun.getMaxAmmo()) hasAmmo = true;
            }
            
            float ammo_cap = 10f;
            if (sMod) {
                ammo_cap = SMOD1;
            }
            
            if (hasAmmo) {
                amreg.interval.advance(amount);
                if (amreg.interval.intervalElapsed()) {
                    for (WeaponAPI gun : ship.getAllWeapons()) {
                        if (gun.getSpec().getAmmoPerSecond() == 0 && gun.getMaxAmmo() < ammo_cap) continue;
                        if (gun.getType() != WeaponType.ENERGY && gun.getType() != WeaponType.BALLISTIC) continue;

                        if (gun.usesAmmo() && gun.getAmmo() < gun.getMaxAmmo()) {
                            int reload = (int) Math.max(1f, (float) gun.getMaxAmmo() / 10f);
                            gun.setAmmo( Math.min( gun.getAmmo() + reload, gun.getMaxAmmo() ) );
                        }
                    }
                }
            } else amreg.interval.setElapsed(0f);
        }
}
