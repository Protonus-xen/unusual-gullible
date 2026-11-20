package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.UGH_MD;
import java.awt.Color;

public class UGH_ExPhaseCap extends BaseHullMod {
	public static final float PHASE_FLUX_ACTIVE_MULT = 15f;
	public static final float PHASE_UPKEEP_MULT = 25f;
        
	public static final float PHASE_COOLDOWN_MULT = 33.34f;
        
	public static final float SMODIFIER = 33f;
	public static final float SMODIFIER_COOLDOWN = 10f;

        @Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
            boolean sMod = isSMod(stats);
            
            stats.getPhaseCloakActivationCostBonus().modifyMult(id, 1f - PHASE_FLUX_ACTIVE_MULT * 0.01f);
            stats.getPhaseCloakUpkeepCostBonus().modifyMult(id, 1f - (sMod ? SMODIFIER : PHASE_UPKEEP_MULT) * 0.01f);
	}
	
        @Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
            boolean sMod = isSMod(ship.getMutableStats());
            
            MutableShipStatsAPI stats = ship.getMutableStats();
            if (ship.getVariant().getHullMods().contains("ugh_inverteddilator") || ship.getVariant().getHullMods().contains("ugh_phasearmor")) {}
            else stats.getPhaseCloakCooldownBonus().modifyMult(id, 1f + ((sMod ? SMODIFIER_COOLDOWN : PHASE_COOLDOWN_MULT) * 0.01f));
	}
	
        protected static final float LOAD_OF_BULL = 3f;
	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		float opad = 10f;
		float pad = 3f;
		Color h = Misc.getHighlightColor();
                Color bad = Misc.getNegativeHighlightColor();
                Color good = Misc.getPositiveHighlightColor();
                
                LabelAPI bullet;
                tooltip.setBulletedListMode(" • ");
                
		tooltip.addSectionHeading(UGH_MD.base("improv"), Alignment.MID, opad);
                bullet = tooltip.addPara(UGH_MD.epc("im_flux_use"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "-" + (int) PHASE_FLUX_ACTIVE_MULT + "%");
                bullet = tooltip.addPara(UGH_MD.epc("im_flux_gen"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "-" + (int) PHASE_UPKEEP_MULT + "%");
                
		tooltip.addSectionHeading(UGH_MD.base("bad"), Alignment.MID, opad);
                bullet = tooltip.addPara(UGH_MD.epc("bad_cooldown"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), bad,
                    "+" + (int) PHASE_COOLDOWN_MULT + "%" );
                
		tooltip.addSectionHeading(UGH_MD.base("compat"), Alignment.MID, opad);
                bullet = tooltip.addPara(UGH_MD.epc("comp_cooldown"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "" + "" + "");
		bullet.setHighlight(UGH_MD.epc("comp_IPD_h"), UGH_MD.epc("comp_PAF_h"));
		bullet.setHighlightColors(h, h);
                
            tooltip.setBulletedListMode(null);
	}
	
        @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
            /*if (index == 0) return "25%";
            if (index == 1) return "20%";
            if (index == 2) return "33%";
            if (index == 3) return "Inverted Phase Dilator";
            if (index == 4) return "Phasic Armor Frame";*/
            return null;
	}
        
        @Override
	public String getSModDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "" + (int) (SMODIFIER) + "%";
            if (index == 1) return "" + (int) (SMODIFIER_COOLDOWN) + "%";
            return null;
	}
	
        @Override
	public String getUnapplicableReason(ShipAPI ship) {
            if (ship.getHullSpec().getDefenseType() != ShieldAPI.ShieldType.PHASE)
                return "This modification is exclusive to Phase Ships";
            return null;
	}
	
        @Override
	public boolean isApplicableToShip(ShipAPI ship) {
            return ship != null && ship.getHullSpec().getDefenseType() == ShieldAPI.ShieldType.PHASE;
	}
	
}
