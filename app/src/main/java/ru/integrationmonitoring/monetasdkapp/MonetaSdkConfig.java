package ru.integrationmonitoring.monetasdkapp;

import android.content.Context;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by dmo on 13.04.2016.
 */
public class MonetaSdkConfig {

    private Properties configuration;

    public MonetaSdkConfig() {
        configuration = new Properties();
    }

    public boolean load(Context context) {
        boolean retval = false;

        try {
            configuration.load(context.getAssets().open("android_basic_settings.ini"));
            configuration.load(context.getAssets().open("android_payment_systems.ini"));
            configuration.load(context.getAssets().open("error_texts.ini"));
            configuration.load(context.getAssets().open("payment_urls.ini"));
            retval = true;
        } catch (IOException e) {
            System.out.println("Configuration error: " + e.getMessage());
        }

        return retval;
    }

    public void set(String key, String value) {
        configuration.setProperty(key, value);
    }

    public String get(String key) {
        String readKey = configuration.getProperty(key).replace("\"", "");
        return readKey;
    }

}
