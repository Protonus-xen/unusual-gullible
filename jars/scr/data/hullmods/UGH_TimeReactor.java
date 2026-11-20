package data.hullmods;

import com.fs.starfarer.api.Global;
import java.awt.Color;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.UGH_MD;
import java.util.ArrayList;
import java.util.List;

public class UGH_TimeReactor extends BaseHullMod {
        private static final String ugh_id = "ugh_timereactor_ID";
	public static final Color JITTER_COLOR = new Color(210,210,210,80);
	public static final Color JITTER_COLOR2 = new Color(205,140,140,50);

	public static final float PEAK_HURT = 33f;
	public static final float TIME_OF_HURT = 1.5f;
	public static final float TIME_OF_SUPER_HURT = 2.5f;
        
	public static final float MIN_TIME_MULT = 0.1f;
	public static final float MAX_TIME_MULT = 33.33f;
	public static final float SUPER_TIME_MULT = 66.67f;
	
        @Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
            boolean sMod = isSMod(stats);
            
            stats.getPeakCRDuration().modifyMult(id, 1f - (PEAK_HURT * 0.01f));
            stats.getHullDamageTakenMult().modifyMult(id, (sMod ? TIME_OF_SUPER_HURT : TIME_OF_HURT));
            stats.getArmorDamageTakenMult().modifyMult(id, (sMod ? TIME_OF_SUPER_HURT : TIME_OF_HURT));
            stats.getShieldDamageTakenMult().modifyMult(id, (sMod ? TIME_OF_SUPER_HURT : TIME_OF_HURT));
	}
	
        @Override
        public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {
            MutableShipStatsAPI based_stats = ship.getMutableStats();
            boolean sMod = isSMod(based_stats);
            MutableShipStatsAPI stats = fighter.getMutableStats();
            
            stats.getHullDamageTakenMult().modifyMult(id, (sMod ? TIME_OF_SUPER_HURT : TIME_OF_HURT));
            stats.getArmorDamageTakenMult().modifyMult(id, (sMod ? TIME_OF_SUPER_HURT : TIME_OF_HURT));
            stats.getShieldDamageTakenMult().modifyMult(id, (sMod ? TIME_OF_SUPER_HURT : TIME_OF_HURT));
        }
        
	public static List<ShipAPI> getFighters(ShipAPI carrier) {
		List<ShipAPI> result = new ArrayList<ShipAPI>();
		
		for (ShipAPI ship : Global.getCombatEngine().getShips()) {
			if (!ship.isFighter()) continue;
			if (ship.getWing() == null) continue;
			if (ship.getWing().getSourceShip() == carrier) {
				result.add(ship);
			}
		}
		
		return result;
	}
	
        @Override
        public void advanceInCombat(ShipAPI ship, float amount) {
            MutableShipStatsAPI b_stats = ship.getMutableStats();
            boolean sMod = isSMod(b_stats);
            float maxRange = 1f;
                
                if (Global.getCombatEngine().isPaused()) return;
		boolean player = ship == Global.getCombatEngine().getPlayerShip();
                if (!ship.isAlive() || ship.isPiece()) return;
                float soup_time = MAX_TIME_MULT;
                if (sMod) { soup_time = SUPER_TIME_MULT; }
		
                if (ship.isAlive()){ //Ah yes, Rocket Heaven.
                    ship.setJitterUnder(this, JITTER_COLOR, 1, 3, 2, 10 + maxRange);
                    if (sMod) ship.setJitterUnder(this, JITTER_COLOR2, 1, 3, 2, 15 + maxRange);
                    ship.getMutableStats().getTimeMult().modifyMult(ugh_id, 1f + (soup_time * 0.01f));
                    for (ShipAPI fighter : getFighters(ship)) {
			if (fighter.isHulk()) continue;
			if (!fighter.isAlive()) return;
                                
			MutableShipStatsAPI fStats = fighter.getMutableStats();
                        fighter.setJitterUnder(this, JITTER_COLOR, 1, 3, 2, 10 + maxRange);
                        if (sMod) fighter.setJitterUnder(this, JITTER_COLOR2, 1, 3, 2, 15 + maxRange);
                        fStats.getTimeMult().modifyMult(ugh_id, 1f + (soup_time * 0.01f));
                    }
                } else {
                    ship.getMutableStats().getTimeMult().unmodify(ugh_id);
                    for (ShipAPI fighter : getFighters(ship)) {
			if (fighter.isHulk()) continue;
			if (!fighter.isAlive()) return;
                                
			MutableShipStatsAPI fStats = fighter.getMutableStats();
                        fStats.getTimeMult().unmodify(ugh_id);
                    }
                }
                
                if (player) {
                    if (ship.isAlive()){
                        Global.getCombatEngine().getTimeMult().modifyMult(ugh_id, 1f / (1f + (soup_time * 0.01f)));
                        if (sMod) Global.getCombatEngine().maintainStatusForPlayerShip(ugh_id, "graphics/icons/hullsys/temporal_shell.png", "Time Reactor", "1.67x Timeflow", false);
                        else Global.getCombatEngine().maintainStatusForPlayerShip(ugh_id, "graphics/icons/hullsys/temporal_shell.png", "Time Reactor", "1.33x Timeflow", false);
                    } else {
                        Global.getCombatEngine().getTimeMult().unmodify(ugh_id);
                    }
                }
                
	}
        
        protected static final float LOAD_OF_BULL = 3f;
	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		float opad = 10f;
		Color h = Misc.getHighlightColor();
                Color bad = Misc.getNegativeHighlightColor();
                Color good = Misc.getPositiveHighlightColor();
		
                LabelAPI bullet;
                tooltip.setBulletedListMode(" • ");
                
		tooltip.addSectionHeading(UGH_MD.base("improv"), Alignment.MID, opad);
                bullet = tooltip.addPara(UGH_MD.tm_react("im_flow"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "1." + (int) MAX_TIME_MULT + "x" );
                
		tooltip.addSectionHeading(UGH_MD.base("bad"), Alignment.MID, opad);
                bullet = tooltip.addPara(UGH_MD.tm_react("bad_peak"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), bad,
                    "-" + (int) PEAK_HURT + "%" );
                bullet = tooltip.addPara(UGH_MD.tm_react("bad_dam"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), bad,
                    "" + (float) TIME_OF_HURT + "x" );
                
		tooltip.addSectionHeading(UGH_MD.base("compat"), Alignment.MID, opad);
                bullet = tooltip.addPara(UGH_MD.tm_react("note"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), h,
                    UGH_MD.tm_react("note_h") );
                
                
            tooltip.setBulletedListMode(null);
	}
        
        @Override
	public String getSModDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "1.67x";
            if (index == 1) return "2.5x";
            return null;
	}
	
	@Override
	public boolean isSModEffectAPenalty() {
		return true;
	}
        
        @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
		return null;
	}

        @Override
        public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
            super.applyEffectsAfterShipCreation(ship, id);
        }
    
	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		return true;
		
	}
	
        @Override
	public String getUnapplicableReason(ShipAPI ship) {
            return null;
	}
}
