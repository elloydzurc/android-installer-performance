package com.ipms.arena.helper;

/**
 * Created by t-ebcruz on 4/4/2016.
 */
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by t-ebcruz on 2/23/2016.
 */
public class FontManager
{
  public static final String ROOT = "fonts/",
          FONTAWESOME = ROOT + "fontawesome-webfont.ttf";

  public static Typeface getTypeface(Context context, String font)
  {
    return Typeface.createFromAsset(context.getAssets(), font);
  }

  public static void markAsIconContainer(View v, Typeface typeface)
  {
    if (v instanceof ViewGroup)
    {
      ViewGroup vg = (ViewGroup) v;
      for (int i = 0; i < vg.getChildCount(); i++)
      {
        View child = vg.getChildAt(i);
        markAsIconContainer(child, typeface);
      }
    }
    else if (v instanceof TextView)
    {
      ((TextView) v).setTypeface(typeface);
    }
  }
}