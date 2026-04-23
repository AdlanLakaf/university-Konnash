package com.konnash.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {

    private static final String PREFS_NAME       = "konnash_prefs";
    private static final String KEY_ONBOARDED    = "onboarded";
    private static final String KEY_PHONE        = "phone";
    private static final String KEY_LANGUAGE     = "language";
    private static final String KEY_CASH_INTRO   = "cash_intro_seen";

    private final SharedPreferences prefs;

    private static PrefsManager instance;

    public static PrefsManager getInstance(Context ctx) {
        if (instance == null) {
            instance = new PrefsManager(ctx.getApplicationContext());
        }
        return instance;
    }

    private PrefsManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean isOnboarded()              { return prefs.getBoolean(KEY_ONBOARDED, false); }
    public void    setOnboarded(boolean v)    { prefs.edit().putBoolean(KEY_ONBOARDED, v).apply(); }

    public String  getPhone()                 { return prefs.getString(KEY_PHONE, ""); }
    public void    setPhone(String phone)     { prefs.edit().putString(KEY_PHONE, phone).apply(); }

    public String  getLanguage()              { return prefs.getString(KEY_LANGUAGE, "ar"); }
    public void    setLanguage(String lang)   { prefs.edit().putString(KEY_LANGUAGE, lang).apply(); }

    public boolean isCashIntroSeen()          { return prefs.getBoolean(KEY_CASH_INTRO, false); }
    public void    setCashIntroSeen(boolean v){ prefs.edit().putBoolean(KEY_CASH_INTRO, v).apply(); }
}
