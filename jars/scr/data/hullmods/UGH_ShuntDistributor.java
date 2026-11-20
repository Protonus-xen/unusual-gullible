package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignUIAPI;
import com.fs.starfarer.api.campaign.CampaignUIAPI.CoreUITradeMode;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
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
import java.util.HashMap;
import java.util.Map;

public class UGH_ShuntDistributor extends BaseHullMod {
        public static final float BASE_FLUX = 10f;
        public static final float BASE_C_FLUX = 10f;
        public static final float FIRE_SPEED_MULT = 10f;
	private static Map mag = new HashMap();
	static {
		mag.put(HullSize.FRIGATE, 50f);
		mag.put(HullSize.DESTROYER, 100f);
		mag.put(HullSize.CRUISER, 150f);
		mag.put(HullSize.CAPITAL_SHIP, 250f);
	}
	public static final float SMODIFIER = 15f;
	public static final float SMODIFIER2 = 20f;
	public static final float WEP_FLUX = 10f;
	
        @Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
            boolean sMod = isSMod(stats);
            
            stats.getBallisticRoFMult().modifyPercent(id, (sMod ? SMODIFIER : FIRE_SPEED_MULT));
            stats.getEnergyRoFMult().modifyPercent(id, (sMod ? SMODIFIER : FIRE_SPEED_MULT));
            
            stats.getFluxDissipation().modifyFlat(id, (Float) mag.get(hullSize));
            stats.getFluxDissipation().modifyPercent(id, (sMod ? SMODIFIER2 : BASE_FLUX));
            stats.getFluxCapacity().modifyPercent(id, BASE_C_FLUX);
            
            stats.getBallisticWeaponFluxCostMod().modifyMult(id, (sMod ? (1f - (WEP_FLUX * 0.01f)) : 1));
            stats.getEnergyWeaponFluxCostMod().modifyMult(id, (sMod ? (1f - (WEP_FLUX * 0.01f)) : 1));
            
	}
        
        @Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
            if (!(ship.getVariant().hasHullMod("shield_shunt"))){
                ship.getVariant().removeMod("ugh_shuntdistributor");
                ship.getVariant().getSMods().remove("ugh_shuntdistributor");
                ship.getVariant().getSModdedBuiltIns().remove("ugh_shuntdistributor");
                ship.getVariant().removePermaMod("ugh_shuntdistributor");
            }
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
                bullet = tooltip.addPara(UGH_MD.shunt_d("im_flux"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "" + ((Float) mag.get(HullSize.FRIGATE)).intValue() + 
                    "/" + ((Float) mag.get(HullSize.DESTROYER)).intValue() + 
                    "/" + ((Float) mag.get(HullSize.CRUISER)).intValue() + 
                    "/" + ((Float) mag.get(HullSize.CAPITAL_SHIP)).intValue() + 
                    " and " + "+" + (int) BASE_FLUX + "%" );
                bullet = tooltip.addPara(UGH_MD.shunt_d("im_cap_flux"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "+" + (int) BASE_C_FLUX + "%" );
                bullet = tooltip.addPara(UGH_MD.shunt_d("im_weapon"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "+" + (int) FIRE_SPEED_MULT + "%" );
                
		tooltip.addSectionHeading(UGH_MD.base("compat"), Alignment.MID, opad);
                bullet = tooltip.addPara(UGH_MD.shunt_d("comp_Shun"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), bad,
                    "" + "" + "");
		bullet.setHighlight(UGH_MD.shunt_d("comp_Shun_h"));
		bullet.setHighlightColors(h);
                bullet = tooltip.addPara(UGH_MD.shunt_d("comp_Warn"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), bad,
                    "" + "" + "");
		bullet.setHighlight(UGH_MD.shunt_d("comp_Warn_h"));
		bullet.setHighlightColors(bad);
                    tooltip.setBulletedListMode("   ");
                    tooltip.addPara("%s", 0f, Global.getSettings().getColor("standardTextColor"), gray, UGH_MD.base("shunt_warn"));
                tooltip.setBulletedListMode(" • ");
                
            tooltip.setBulletedListMode(null);
	}
	
        @Override
        public String getDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "" + ((Float) mag.get(HullSize.FRIGATE)).intValue() + "/"
                     + ((Float) mag.get(HullSize.DESTROYER)).intValue() + "/"
                     + ((Float) mag.get(HullSize.CRUISER)).intValue() + "/"
                     + ((Float) mag.get(HullSize.CAPITAL_SHIP)).intValue() + " + "
                     + "20%";
            if (index == 1) return "" + (int) BASE_FLUX + "%";
            if (index == 2) return "Shield Shunt";
            return null;
        }
        
        @Override
	public String getSModDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "" + (int) (SMODIFIER) + "%";
            if (index == 1) return "" + (int) (WEP_FLUX) + "%";
            if (index == 2) return "" + (int) (SMODIFIER2) + "%";
            return null;
	}
	
	@Override
        public String getUnapplicableReason(ShipAPI ship) {
            if (!(ship.getVariant().hasHullMod("shield_shunt"))) {
                return "Shield Shunt is not installed";
            }
            return null;
        }
        
	@Override
	public boolean canBeAddedOrRemovedNow(ShipAPI ship, MarketAPI marketOrNull, CoreUITradeMode mode) {
		if (ship == null || ship.getVariant() == null) return true; // autofit
		if (!ship.getVariant().hasHullMod("ugh_shuntdistributor")) return true; // can always add

                if (ship.getVariant().hasHullMod("shield_shunt")) {
                    return false;
                }
		return true;
	}

	@Override
	public String getCanNotBeInstalledNowReason(ShipAPI ship, MarketAPI marketOrNull, CampaignUIAPI.CoreUITradeMode mode) {
		return "Removing the Shield Shunt will remove the distributor";
	}

		
        @Override
	public boolean isApplicableToShip(ShipAPI ship) {
            return ship != null && ((ship.getVariant().hasHullMod("shield_shunt")));
	}
}