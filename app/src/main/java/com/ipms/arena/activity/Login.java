package com.ipms.arena.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.percent.PercentLayoutHelper;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.ipms.arena.helper.Connection;
import com.ipms.arena.helper.VersionManager;
import com.ipms.arena.interfaces.OnReceiveListener;
import com.ipms.arena.model.LoginResponse;
import com.ipms.arena.soap.AsyncTaskLogin;
import com.ipms.arena.soap.AsyncTaskSoap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A login screen that offers login via email/password.
 */
public class Login extends AppCompatActivity
{
    Context context = this;

    private EditText mEmailView;
    private EditText mPasswordView;
    private CheckBox isNt;
    private View root;
    private View top;
    private View mid;
    private View bot;

    private AsyncTask loginTask;

    SharedPreferences settings;
    SharedPreferences.Editor editor;
    Connection conn;
    ProgressDialog progress;

    public static final String IPMS_PREFERENCES = "IPMS_settings";
    public static final String NT_ACCOUNT = "NT_ACCOUNT";
    public static final String ID = "ID";
    public LoginResponse loginResponse;

    public static final String VER_CODE = "version_code";

    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        Stetho.initializeWithDefaults(this);

        conn = new Connection(this);
        settings = getSharedPreferences(IPMS_PREFERENCES, Context.MODE_PRIVATE);
        editor = settings.edit();

        if(settings.contains(NT_ACCOUNT)) {
            toMain();
        }

        root = findViewById(android.R.id.content);
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL || id == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(textView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                attemptLogin();
            }
        });

        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout()
            {
                int heightDiff = root.getRootView().getHeight() - root.getHeight();

                top = findViewById(R.id.top);
                PercentRelativeLayout.LayoutParams pTop = (PercentRelativeLayout.LayoutParams) top.getLayoutParams();
                final PercentLayoutHelper.PercentLayoutInfo iTop = pTop.getPercentLayoutInfo();

                bot = findViewById(R.id.bot);
                PercentRelativeLayout.LayoutParams pBot = (PercentRelativeLayout.LayoutParams) bot.getLayoutParams();
                final PercentLayoutHelper.PercentLayoutInfo iBot = pBot.getPercentLayoutInfo();

                mid = findViewById(R.id.mid);
                PercentRelativeLayout.LayoutParams pMid = (PercentRelativeLayout.LayoutParams) mid.getLayoutParams();
                final PercentLayoutHelper.PercentLayoutInfo iMid = pMid.getPercentLayoutInfo();

                if(heightDiff > dpToPx(context, 200)) {
                    mid.setVisibility(View.GONE);
                    iTop.heightPercent = 0.65f;
                    iMid.heightPercent = 0f;
                    iBot.heightPercent = 0.35f;
                    mid.requestLayout();
                    top.requestLayout();
                    bot.requestLayout();
                } else {
                    mid.setVisibility(View.VISIBLE);
                    iTop.heightPercent = 0.38f;
                    iMid.heightPercent = 0.25f;
                    iBot.heightPercent = 0.37f;
                    top.requestLayout();
                    mid.requestLayout();
                    bot.requestLayout();
                }
            }
        });

        isNt = (CheckBox) findViewById(R.id.isNT);
    }

    private void attemptLogin()
    {
        if (loginTask != null) {
            return;
        }

        mEmailView.setError(null);
        mPasswordView.setError(null);

        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();
        final boolean isNT = isNt.isChecked();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        }
        else
        {
            VersionManager vm = new VersionManager(this);
            progress = ProgressDialog.show(context, null, getString(R.string.async_status_version), true);

            vm.setVersionContentUrl(Config.UPDATE_URL + Config.VERSION_FILE);
            vm.setUpdateUrl(Config.UPDATE_URL + Config.APK_FILE);

            vm.setUpdateNowLabel(Config.UPDATE_YES);
            vm.setIgnoreThisVersionLabel(Config.UPDATE_NO);
            vm.setDialogCancelable(false);
            vm.checkVersion();

            vm.setOnReceiveListener(new OnReceiveListener()
            {
                @Override
                public boolean onReceive(int i, String s)
                {
                    boolean update = false;
                    double version;
                    JSONObject obj;

                    try {
                        if (i >= 200 && i < 207) {
                            obj = new JSONObject(s);
                            version = obj.getDouble(VER_CODE);

                            if (version > BuildConfig.VERSION_CODE) {
                                if(progress != null && progress.isShowing()) {
                                    progress.dismiss();
                                }
                                progress = null;
                                update = true;
                            } else {
                                loginTask = loginTask(email, password, isNT);
                            }
                        } else {
                            if(progress != null && progress.isShowing()) {
                                progress.dismiss();
                            }
                            progress = null;
                            Toast.makeText(context, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return update;
                }
            });
        }
    }

    private boolean isEmailValid(String email)
    {
        boolean valid = true;

        Pattern regex = Pattern.compile("[^A-Za-z0-9-_@.]");
        Matcher match = regex.matcher(email);

        if(match.find()) {
            valid = false;
        }

        return valid;
    }

    private AsyncTask loginTask(String email, String password, boolean isNT)
    {
        return new AsyncTaskLogin(context, email, password, isNT, new AsyncTaskSoap.AsyncResponse()
        {
            @Override
            public void progress()
            {
                progress.setMessage(getString(R.string.async_status_login));
            }

            @Override
            public void finish(List result)
            {
                if(progress != null && progress.isShowing()) {
                    progress.dismiss();
                }
                progress = null;
                if(loginTask.isCancelled()) {
                    Toast.makeText(context, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }

                if(result != null)
                {
                    loginResponse = (LoginResponse) result.get(0);
                    if (loginResponse.getError() == null)
                    {
                        editor.putString(NT_ACCOUNT, loginResponse.getUsername());
                        editor.putInt(ID, loginResponse.getId());
                        editor.commit();
                        toMain();
                    }
                    else
                    {
                        mPasswordView.setError(getString(R.string.error_incorrect_password));
                        mPasswordView.requestFocus();
                    }
                }
                loginTask = null;
            }
        }).execute();
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private void toMain()
    {
        Intent i = new Intent(context, Main.class);
        startActivity(i);
        finish();
    }

    public static float dpToPx(Context context, float valueInDp)
    {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

}