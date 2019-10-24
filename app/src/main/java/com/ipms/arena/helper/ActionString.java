package com.ipms.arena.helper;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import com.ipms.arena.activity.R;

/**
 * Created by t-ebcruz on 4/7/2016.
 */
public class ActionString
{
  Context context;
  public boolean bold;

  public ActionString(Context context) {
    this.context = context;
  }

  public Spannable create(CharSequence label)
  {
    String html = "" + label;
    Spannable list = new SpannableString(Html.fromHtml(html));
    if(bold) {
      list.setSpan(new StyleSpan(Typeface.BOLD), 0, list.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    list.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorText)), 0, list.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    return list;
  }

  public void setBold(boolean bold) {
    this.bold = bold;
  }
}
