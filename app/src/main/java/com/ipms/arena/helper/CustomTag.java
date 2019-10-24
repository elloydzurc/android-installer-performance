package com.ipms.arena.helper;

/**
 * Created by t-ebcruz on 9/12/2016.
 */

import android.text.Editable;
import android.text.Html.TagHandler;

import org.xml.sax.XMLReader;

public class CustomTag implements TagHandler
{
    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        // you may add more tag handler which are not supported by android here
        if("li".equals(tag)){
            if(opening){
                output.append(" \u2022 ");
            }else{
                output.append("\n");
            }
        }
    }
}