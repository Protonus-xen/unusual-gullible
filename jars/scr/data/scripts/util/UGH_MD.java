package data.scripts.util;

import com.fs.starfarer.api.Global;

public class UGH_MD {
    public static String base(String key) { return Global.getSettings().getString("ugh_hullmod", key); }
    public static String atb(String key) { return Global.getSettings().getString("ugh_atb", key); }
    public static String au_m(String key) { return Global.getSettings().getString("ugh_au_m", key); }
    public static String bob(String key) { return Global.getSettings().getString("ugh_bob", key); }
    public static String susm(String key) { return Global.getSettings().getString("ugh_susm", key); }
    public static String bfa(String key) { return Global.getSettings().getString("ugh_bfa", key); }
    public static String wngspc(String key) { return Global.getSettings().getString("ugh_wngspc", key); }
    public static String tachcm(String key) { return Global.getSettings().getString("ugh_tachcm", key); }
    public static String shunt_d(String key) { return Global.getSettings().getString("ugh_shunt_d", key); }
    public static String shunt_t(String key) { return Global.getSettings().getString("ugh_shunt_t", key); }
    public static String tm_react(String key) { return Global.getSettings().getString("ugh_tm_react", key); }
    public static String ipd(String key) { return Global.getSettings().getString("ugh_ipd", key); }
    public static String paf(String key) { return Global.getSettings().getString("ugh_paf", key); }
    public static String epc(String key) { return Global.getSettings().getString("ugh_epc", key); }
    public static String thrush(String key) { return Global.getSettings().getString("ugh_thrush", key); }
    public static String fault(String key) { return Global.getSettings().getString("ugh_fault", key); }
        
}
