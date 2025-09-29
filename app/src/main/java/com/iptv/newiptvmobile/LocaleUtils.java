package com.iptv.newiptvmobile;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

public class LocaleUtils {
    public static final String THAI = "th";
    public static final String ENG = "en";


    public static void initialize(Context context, @LocaleDef String defaultLanguage) {
        setLocale(context, defaultLanguage);
    }

    public static boolean setLocale(Context context, @LocaleDef String language) {
        return updateResources(context, language);
    }

    private static boolean updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        context.createConfigurationContext(configuration);
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return true;
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({THAI, ENG})
    public @interface LocaleDef {
        String[] SUPPORTED_LOCALES = {THAI, ENG};
    }


    private static SharedPreferences getDefaultSharedPreference(Context context) {
        if (context.getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE) != null)
            return context.getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE);
        else
            return null;
    }

    public static void setSelectedLanguageId(String id,Context context){
        final SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("lang", id);
        editor.apply();
    }

    public static String getSelectedLanguageId(Context context){
        return context.getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE)
                .getString("lang", "en");
    }
}
