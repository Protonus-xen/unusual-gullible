package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import data.scripts.util.UGH_MD;

public class UGH_TachyonBroadcast extends BaseHullMod {
	public static float ZERO_FLUX_TAB;
	public static boolean SPEEDIFIED = true;
	public static boolean DEFLUXED = false;
        
	public static final float ENGAGEMENT_MULT = 10.0f;
	public static final float SMODIFIER = 20.0f;
	
	public float getZeroFlux(MutableShipStatsAPI stats) {
		return (float) (stats.getZeroFluxSpeedBoost().getModifiedValue());
	}
	
        @Override
        public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
                boolean sMod = isSMod(stats);
            
                stats.getFluxDissipation().modifyMult(id, sMod ? 1 : 0.8f);
		stats.getFighterWingRange().modifyMult(id, ENGAGEMENT_MULT);
                ZERO_FLUX_TAB = getZeroFlux(stats);
                //float ZeroFlux = getZeroFlux(stats);
	}
	
        @Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
                MutableShipStatsAPI stats = ship.getMutableStats();
                ZERO_FLUX_TAB = getZeroFlux(stats);
                if (DEFLUXED == true && SPEEDIFIED == true){
                    stats.getMaxSpeed().unmodify(id);
                }
		if (ZERO_FLUX_TAB <= 0f){ //(ZeroFlux <= 0f){
                    stats.getMaxSpeed().modifyMult(id, 0.85f);
                    SPEEDIFIED = false;
                }
                else {
                    stats.getZeroFluxSpeedBoost().modifyMult(id, 0f);
                    DEFLUXED = true;
                }
            //ZERO_FLUX_TAB = stats.getZeroFluxSpeedBoost().getModifiedValue();
	}
	
        @Override
	public boolean isApplicableToShip(ShipAPI ship) {
            int bays = (int) ship.getMutableStats().getNumFighterBays().getModifiedValue();
            return bays > 0;
	}
	
        @Override
	public String getUnapplicableReason(ShipAPI ship) {
		return UGH_MD.base("no_bays");
	}
        
        @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "" + (int) ENGAGEMENT_MULT + "x";
            if (index == 1) return UGH_MD.tachcm("zflux_bad");
            if (index == 2) return "20%";
            if (index == 3) return "15%";
            return null;
        }
        
        @Override
	public String getSModDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "" + (int) (SMODIFIER) + "x";
            return null;
	}

}

			
			