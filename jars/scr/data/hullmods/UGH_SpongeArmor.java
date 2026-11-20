package data.hullmods;

import com.fs.starfarer.api.Global;
import java.util.HashMap;
import java.util.Map;
import java.awt.*;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ArmorGridAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.UGH_MD;
import static java.lang.Float.NaN;

public class UGH_SpongeArmor extends BaseHullMod {
	public static final float ARMOR_BONUS = 20f;
	public static final float EMP_RESIST = 50f;
	public static final float ARMOR_PEN = 2f;
	private static Map mag = new HashMap();
	static {
		mag.put(HullSize.FRIGATE, 200f);
		mag.put(HullSize.DESTROYER, 300f);
		mag.put(HullSize.CRUISER, 400f);
		mag.put(HullSize.CAPITAL_SHIP, 500f);
	}
        
        @Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getArmorBonus().modifyFlat(id, (Float) mag.get(hullSize));
		stats.getArmorBonus().modifyPercent(id, ARMOR_BONUS);
		stats.getEmpDamageTakenMult().modifyMult(id, 1f - (EMP_RESIST * 0.01f));
                stats.getKineticArmorDamageTakenMult().modifyMult(id, 2.0f);
                stats.getEffectiveArmorBonus().modifyMult(id, 1f / ARMOR_PEN);
                //stats.getFragmentationDamageTakenMult().modifyMult(id, 1.5f);
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
            //if (isForModSpec || ship == null) return;
            
            LabelAPI bullet;
            tooltip.setBulletedListMode(" • ");
		tooltip.addSectionHeading(UGH_MD.base("feature"), Alignment.MID, opad);
                bullet = tooltip.addPara(UGH_MD.bob("im_regen"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "" + "0.5" + "%");
                
		tooltip.addSectionHeading(UGH_MD.base("improv"), Alignment.MID, opad);
                bullet = tooltip.addPara(UGH_MD.bob("im_armor"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "" + ((Float) mag.get(HullSize.FRIGATE)).intValue() + 
                    "/" + ((Float) mag.get(HullSize.DESTROYER)).intValue() + 
                    "/" + ((Float) mag.get(HullSize.CRUISER)).intValue() + 
                    "/" + ((Float) mag.get(HullSize.CAPITAL_SHIP)).intValue() + 
                    " and " + "+" + (int) ARMOR_BONUS + "%" );
                bullet = tooltip.addPara(UGH_MD.bob("im_emp"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "+" + (int) EMP_RESIST + "%");
                
		tooltip.addSectionHeading(UGH_MD.base("bad"), Alignment.MID, opad);
                bullet = tooltip.addPara(UGH_MD.bob("bad_armor"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), bad,
                    "" + UGH_MD.bob("full"));
                bullet = tooltip.addPara(UGH_MD.bob("bad_efarm"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), bad,
                    "" + (float) (1f / ARMOR_PEN) + "x");
                /*
                bullet = tooltip.addPara(UGH_MD.bob("bad_frag"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), bad,
                    "+" + "50" + "%", UGH_MD.bob("bad_frag_h"));
		bullet.setHighlight("+" + "50" + "%", UGH_MD.bob("bad_frag_h"));
		bullet.setHighlightColors(bad, gray);
                */
                
		tooltip.addSectionHeading(UGH_MD.base("note"), Alignment.MID, opad);
		bullet = tooltip.addPara(UGH_MD.bob("note"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), h,
			"" + "");
		bullet.setHighlight(UGH_MD.bob("note_h"));
		bullet.setHighlightColors(h);
            
            tooltip.setBulletedListMode(null);
        }

        @Override
        public String getDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) {
                return "" + (int) 200 + "/" + 
                        (int) 300 + "/" + 
                        (int) 400 + "/" + 
                        (int) 500;
            }
            if (index == 1) return "" + (int) ARMOR_BONUS + "%";
            if (index == 2) return "50%";
            if (index == 3) return "0.15%";
            if (index == 4) return "full";
            if (index == 5) return "50%";
            if (index == 6) return "37.5% Armor damage, 150% Hull damage";
            return null;
        }
	
        @Override
        public String getUnapplicableReason(ShipAPI ship) {
		return null;
	}
		
        @Override
	public boolean isApplicableToShip(ShipAPI ship) {
		return ship != null;
	}
	
        @Override
        public void advanceInCombat(ShipAPI ship, float amount){
            if (ship == null) return;
            if (!ship.isAlive()) return;
            //MutableShipStatsAPI stats = ship.getMutableStats();

            final ArmorGridAPI armor = ship.getArmorGrid();
            final float[][] grid = armor.getGrid();	
            final float maxArmor = armor.getMaxArmorInCell();
            float toHeal =  maxArmor * (0.005f) * amount;
            if ((toHeal == NaN) || (toHeal > maxArmor)) toHeal = 0f; //I have no idea what I'm doing.
            for (int x = 0; x < grid.length; x++){
                for (int y = 0; y < grid[0].length; y++){
                    armor.setArmorValue(x, y, Math.min(grid[x][y] + toHeal, maxArmor));
                }
            }
	}
}