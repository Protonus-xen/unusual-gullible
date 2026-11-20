package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.UGH_MD;
import java.awt.Color;

public class UGH_ThrushWarheads extends BaseHullMod {
	public static final float MIS_SPEED = 33f;
	public static final float MIS_RANGE = 20f;
	public static final float MIS_FLUX = 20f;
        
	public static final float MIS_DUR = 20f;
	public static final float MIS_DUR_SMOD = 25f;
	public static final float MIS_AMMO_SMOD = 20f;
	public static final float MIS_TURN = 50f;
	public static final float MIS_TURN_A = 80f;
	
        @Override
        public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
            boolean sMod = isSMod(stats);
            
            stats.getMissileHealthBonus().modifyMult(id, sMod ? (1f + (MIS_DUR_SMOD * 0.01f)) : (1f - (MIS_DUR * 0.01f)));
            stats.getMissileAmmoBonus().modifyPercent(id, (sMod ? MIS_AMMO_SMOD : 0));
            stats.getMissileMaxSpeedBonus().modifyMult(id, 1f + (MIS_SPEED * 0.01f));
            stats.getMissileWeaponRangeBonus().modifyPercent(id, 1f + (MIS_RANGE * 0.01f));
            stats.getMissileMaxTurnRateBonus().modifyMult(id, 1f - (MIS_TURN * 0.01f));
            stats.getMissileTurnAccelerationBonus().modifyMult(id, 1f - (MIS_TURN_A * 0.01f));
            stats.getMissileWeaponFluxCostMod().modifyMult(id, 1f - (MIS_FLUX * 0.01f));
	}
	
        @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "1.5";
            if (index == 1) return "33%";
            if (index == 2) return "" + (int) (MIS_DUR) + "%";
            if (index == 3) return "80%";
            if (index == 4) return "20%";
            return null;
        }
        
        protected static final float LOAD_OF_BULL = 3f;
	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		float opad = 10f;
		float pad = 3f;
		Color h = Misc.getHighlightColor();
                Color bad = Misc.getNegativeHighlightColor();
                Color good = Misc.getPositiveHighlightColor();
                Color gray = Misc.getGrayColor();
                
                LabelAPI bullet;
                tooltip.setBulletedListMode(" • ");
		tooltip.addSectionHeading(UGH_MD.base("improv"), Alignment.MID, opad);
                bullet = tooltip.addPara(UGH_MD.thrush("mis_range"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "+" + (int) MIS_RANGE + "%" );
                bullet = tooltip.addPara(UGH_MD.thrush("mis_speed"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "1." + (int) MIS_SPEED + "x" );
                bullet = tooltip.addPara(UGH_MD.thrush("mis_flux"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "-" + (int) MIS_FLUX + "%" );
                
		tooltip.addSectionHeading(UGH_MD.base("bad"), Alignment.MID, opad);
                bullet = tooltip.addPara(UGH_MD.thrush("mis_dur"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), bad,
                    "-" + (int) MIS_DUR + "%" );
                bullet = tooltip.addPara(UGH_MD.thrush("mis_turn"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), bad,
                    "-" + (int) MIS_TURN_A + "%" );
                
            tooltip.setBulletedListMode(null);
	}
	
        @Override
	public String getSModDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "" + (int) (MIS_DUR_SMOD) + "%";
            if (index == 1) return "" + (int) (MIS_AMMO_SMOD) + "%";
            return null;
	}
}

			
			