package com.ipms.arena.helper;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by t-ebcruz on 3/16/2016.
 */
public class Config
{
    public String get(String property)
    {
        Properties prop = new Properties();
        InputStream input = null;
        String output = "=AIzaSyD4LgKkRfkoRjSROPKZ-a_YaD3NYYdMrgQ";

        /*try
        {
            input = this.getClass().getResourceAsStream("app.properties"); //new FileInputStream("app.properties");
            prop.load(input);

            Enumeration<?> e = prop.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                String value = prop.getProperty(key);
                Log.e("PROPERTIES", "Key : " + key + ", Value : " + value);
            }


        } catch (FileNotFoundException e) {
            Log.e("ERROR", e.getMessage());
        } catch (IOException e) {
            Log.e("ERROR", e.getMessage());
        }*/

        return output;
    }
}
