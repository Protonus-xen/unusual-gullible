package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.UGH_MD;
import java.awt.Color;

public class UGH_WingSpec extends BaseHullMod {
	public static final float REPLACEMENT_REC_PENALTY = 10f;
	public static final float REPLACEMENT_DEG_PENALTY = 10f;
        
	public static final float BOMBER_BONUS = 20f;
	public static final float INTERCEPTOR_BONUS = 25f;
	public static final float FIGHTER_BONUS = 20f;
	public static final float FIGHTER_FRIG_BONUS = 10f;
        
	public static final float S_BOMBER_BONUS = 30f;
	public static final float S_INTERCEPTOR_BONUS = 35f;
	public static final float S_FIGHTER_BONUS = 25f;
	public static final float S_FIGHTER_FRIG_BONUS = 20f;
        
	public static final float SMODIFIER = 50f;
    
        @Override
        public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
            boolean sMod = isSMod(stats);
		float mult = 100f;
		if (sMod) mult = SMODIFIER; 
		stats.getDynamic().getStat(Stats.FIGHTER_CREW_LOSS_MULT).modifyMult(id, mult * 0.01f);
            
		stats.getDynamic().getStat(Stats.REPLACEMENT_RATE_INCREASE_MULT).modifyMult(id, 1f - ((sMod ? 0 : REPLACEMENT_REC_PENALTY) * 0.01f));
		stats.getDynamic().getStat(Stats.REPLACEMENT_RATE_DECREASE_MULT).modifyMult(id, 1f + ((sMod ? 0 : REPLACEMENT_DEG_PENALTY) * 0.01f));
        }

        @Override
        public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {
            MutableShipStatsAPI stats = fighter.getMutableStats();
            MutableShipStatsAPI based_stats = ship.getMutableStats();
            boolean sMod = isSMod(based_stats);
            
            if (fighter.getWing().getSpec().isBomber()){
		stats.getDamageToDestroyers().modifyMult(id, 1f + ((sMod ? S_BOMBER_BONUS : BOMBER_BONUS) * 0.01f));
		stats.getDamageToCruisers().modifyMult(id, 1f + ((sMod ? S_BOMBER_BONUS : BOMBER_BONUS) * 0.01f));
		stats.getDamageToCapital().modifyMult(id, 1f + ((sMod ? S_BOMBER_BONUS : BOMBER_BONUS) * 0.01f));
            }
            if (fighter.getWing().getSpec().isAssault() || fighter.getWing().getSpec().isRegularFighter()|| fighter.getWing().getSpec().isSupport()){
		stats.getDamageToTargetShieldsMult().modifyMult(id, 1f + ((sMod ? S_FIGHTER_BONUS : FIGHTER_BONUS) * 0.01f));
		stats.getDamageToFrigates().modifyMult(id, 1f + ((sMod ? S_FIGHTER_FRIG_BONUS : FIGHTER_FRIG_BONUS) * 0.01f));
            }
            if (fighter.getWing().getSpec().isInterceptor()){
		stats.getDamageToFighters().modifyMult(id, 1f + ((sMod ? S_INTERCEPTOR_BONUS : INTERCEPTOR_BONUS) * 0.01f));
		stats.getDamageToMissiles().modifyMult(id, 1f + ((sMod ? S_INTERCEPTOR_BONUS : INTERCEPTOR_BONUS) * 0.01f));
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
                bullet = tooltip.addPara(UGH_MD.wngspc("im_bomber"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "+" + (int) BOMBER_BONUS + "%");
		bullet.setHighlight(UGH_MD.wngspc("im_bomber_h"), UGH_MD.wngspc("im_bomber_h2"), "+" + (int) BOMBER_BONUS + "%");
		bullet.setHighlightColors(h, h, good);
                bullet = tooltip.addPara(UGH_MD.wngspc("im_fighter"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "+" + (int) FIGHTER_BONUS + "%", "+" + (int) FIGHTER_FRIG_BONUS + "%");
		bullet.setHighlight(UGH_MD.wngspc("im_fighter_h"), UGH_MD.wngspc("im_fighter_h2"), UGH_MD.wngspc("im_fighter_h3"), "+" + (int) FIGHTER_BONUS + "%", UGH_MD.wngspc("im_fighter_h4"), "+" + (int) FIGHTER_FRIG_BONUS + "%");
		bullet.setHighlightColors(h, gray, h, good, h, good);
                bullet = tooltip.addPara(UGH_MD.wngspc("im_inter"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "+" + (int) INTERCEPTOR_BONUS + "%");
		bullet.setHighlight(UGH_MD.wngspc("im_inter_h"), UGH_MD.wngspc("im_inter_h2"), "+" + (int) INTERCEPTOR_BONUS + "%");
		bullet.setHighlightColors(h, h, good);
                
		tooltip.addSectionHeading(UGH_MD.base("bad"), Alignment.MID, opad);
                bullet = tooltip.addPara(UGH_MD.base("rep_rec"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), bad,
                    "-" + (int) REPLACEMENT_DEG_PENALTY + "%");
                bullet = tooltip.addPara(UGH_MD.base("rep_deg"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), bad,
                    "+" + (int) REPLACEMENT_DEG_PENALTY + "%");
                
            tooltip.setBulletedListMode(null);
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
		if (index == 0) return "20%";
		if (index == 1) return "20%";
		if (index == 2) return "10%";
		if (index == 3) return "20%";
		if (index == 4) return "10%";
		return null;
	}
        
        @Override
	public String getSModDescriptionParam(int index, HullSize hullSize) {
            if (index == 0) return "" + (int) (SMODIFIER) + "%";
            if (index == 1) return "" + (int) (S_BOMBER_BONUS) + "%";
            if (index == 2) return "" + (int) (S_FIGHTER_BONUS) + "%";
            if (index == 3) return "" + (int) (S_FIGHTER_FRIG_BONUS) + "%";
            if (index == 4) return "" + (int) (S_INTERCEPTOR_BONUS) + "%";
            return null;
	}
}
