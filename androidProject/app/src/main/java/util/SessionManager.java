package util;

import android.content.Context;
import android.content.SharedPreferences;

import data.ConfigDraft;

public class SessionManager {

    private static final String PREF_NAME     = "mtg_session_prefs";
    private static final String KEY_USER_ID   = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_LOGGED_IN = "logged_in";

    private static SharedPreferences getPrefs(Context ctx) {
        return ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Salva login após sucesso na API
    public static void saveLogin(Context ctx, String userId, String userName) {
        SharedPreferences.Editor ed = getPrefs(ctx).edit();
        ed.putString(KEY_USER_ID, userId);
        ed.putString(KEY_USER_NAME, userName);
        ed.putBoolean(KEY_LOGGED_IN, true);
        ed.apply();

        // Mantém em sincronia com o que o app já usa
        ConfigDraft.get().setUsuarioId(userId);
    }

    public static boolean isLoggedIn(Context ctx) {
        return getPrefs(ctx).getBoolean(KEY_LOGGED_IN, false)
                && getUserId(ctx) != null;
    }

    public static String getUserId(Context ctx) {
        return getPrefs(ctx).getString(KEY_USER_ID, null);
    }

    public static String getUserName(Context ctx) {
        return getPrefs(ctx).getString(KEY_USER_NAME, null);
    }

    // Desloga usuário
    public static void logout(Context ctx) {
        SharedPreferences.Editor ed = getPrefs(ctx).edit();
        ed.clear();
        ed.apply();

        ConfigDraft.get().setUsuarioId(null);
    }
}
