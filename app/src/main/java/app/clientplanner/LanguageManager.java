package app.clientplanner;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import java.util.Locale;

public class LanguageManager extends AppCompatActivity {

    public boolean isRus;
    private AppCompatActivity mContext;
    private Locale myLocale;

    public LanguageManager(AppCompatActivity mContext) {
        this.mContext = mContext;
    }

    public void changeLang(String lang) {
        if (lang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(lang);
        saveLocale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;

        isRus = !lang.equals("en");

        mContext.getBaseContext().getResources().updateConfiguration(config,
                mContext.getBaseContext().getResources().getDisplayMetrics());
        //updateTexts();
    }


    public void saveLocale(String lang)
    {
        String langPref = "Language";
        SharedPreferences prefs = mContext.getSharedPreferences("CommonPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.commit();
    }


    public void loadLocale()
    {
        String langPref = "Language";
        SharedPreferences prefs = mContext.getSharedPreferences("CommonPrefs", MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
        changeLang(language);
    }
}
