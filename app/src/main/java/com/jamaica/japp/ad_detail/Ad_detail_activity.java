package com.jamaica.japp.ad_detail;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.gson.JsonObject;
import com.jamaica.japp.R;
import com.jamaica.japp.adapters.ItemMainHomeRelatedAdapter;
import com.jamaica.japp.helper.LocaleHelper;
import com.jamaica.japp.helper.OnItemClickListener2;
import com.jamaica.japp.home.AddNewAdPost;
import com.jamaica.japp.modelsList.catSubCatlistModel;
import com.jamaica.japp.utills.Admob;
import com.jamaica.japp.utills.AdsTimerConvert;
import com.jamaica.japp.utills.AnalyticsTrackers;
import com.jamaica.japp.utills.CustomBorderDrawable;
import com.jamaica.japp.utills.Network.RestService;
import com.jamaica.japp.utills.RuntimePermissionHelper;
import com.jamaica.japp.utills.SettingsMain;
import com.jamaica.japp.utills.UrlController;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sufficientlysecure.htmltextview.HtmlResImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.IOException;

import cn.iwgang.countdownview.DynamicConfig;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ss.com.bannerslider.banners.RemoteBanner;

public class Ad_detail_activity extends AppCompatActivity implements RuntimePermissionHelper.permissionInterface {

    SettingsMain settingsMain;
    Intent intent;
    RuntimePermissionHelper runtimePermissionHelper;
    RestService restService;
    JSONObject JsonObjectData;
    Button btn;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_detail_activity);

        settingsMain = new SettingsMain(this);
        intent = getIntent();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(settingsMain.getMainColor()));
        }

        webView= (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // view.loadUrl(url);
                //for sharing
                if (url == null || url.startsWith("http://") || url.startsWith("https://")) return false;

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    view.getContext().startActivity(intent);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        runtimePermissionHelper = new RuntimePermissionHelper(this, this);
        btn= (Button) findViewById(R.id.contact);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(settingsMain.getMainColor())));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runtimePermissionHelper.requestLocationPermission(1);
            }
        });
        if (settingsMain.getAppOpen()) {
            restService = UrlController.createService(RestService.class);
        } else
            restService = UrlController.createService(RestService.class, settingsMain.getUserEmail(), settingsMain.getUserPassword(), getApplicationContext());

        toolbar.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));

        if (settingsMain.getAppOpen()) {
            fab.setVisibility(View.GONE);
        }
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (settingsMain.getBannerShow()) {
            if (settingsMain.getAdsShow() && !settingsMain.getBannerAdsId().equals("")) {
                if (settingsMain.getAdsPostion().equals("top")) {
                    LinearLayout frameLayout = (LinearLayout) findViewById(R.id.AdDetailsAdmob);
                    Admob.adforest_Displaybanners(Ad_detail_activity.this, frameLayout);
                } else {
                    LinearLayout frameLayout = (LinearLayout) findViewById(R.id.AdDetailsAdmobBottom);
                    RelativeLayout maimFrame = (RelativeLayout) findViewById(R.id.adDetailsLayout);
                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int width = size.x;
                    int height = size.y;

                    Admob.adforest_Displaybanners(this, frameLayout);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    layoutParams.bottomMargin = height / 10;
                    maimFrame.setLayoutParams(layoutParams);
                    CoordinatorLayout.LayoutParams layoutParams2 = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams2.bottomMargin = height / 5;
                    layoutParams2.setMarginStart(16);
                    layoutParams2.setMarginEnd(16);
                    layoutParams2.leftMargin = 16;
                    layoutParams2.rightMargin = 16;
                    layoutParams2.gravity = Gravity.BOTTOM | Gravity.END;
                    fab.setLayoutParams(layoutParams2);
                }
            }
        }

        FragmentAdDetail fragmentAdDetail = new FragmentAdDetail();

        Bundle bundle = new Bundle();
        bundle.putString("id", intent.getStringExtra("adId"));
        fragmentAdDetail.setArguments(bundle);


        startFragment(fragmentAdDetail, "FragmentAdDetail");
        updateViews(settingsMain.getAlertDialogMessage("gmap_lang"));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adforest_getAllData(intent.getStringExtra("adId"));
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    private void updateViews(String languageCode) {
        LocaleHelper.setLocale(this, languageCode);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_enter, R.anim.right_out);
    }

    public void startFragment(Fragment someFragment, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(tag);

        if (fragment == null) {
            fragment = someFragment;
            fm.beginTransaction()
                    .add(R.id.frameContainer, fragment, tag).commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        try {
            if (settingsMain.getAnalyticsShow() && !settingsMain.getAnalyticsId().equals(""))
                AnalyticsTrackers.getInstance().trackScreenView("Ad Details");
            super.onResume();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccessPermission(int code) {
        Intent intent = new Intent(Ad_detail_activity.this, AddNewAdPost.class);
        startActivity(intent);
    }

    public void adforest_getAllData(final String myId) {
        if (SettingsMain.isConnectingToInternet(getApplicationContext())) {
            JsonObject params = new JsonObject();
            params.addProperty("ad_id", myId);
            Log.d("info send AdDetails", "" + params.toString());

            Call<ResponseBody> myCall = restService.getAdsDetail(params, UrlController.AddHeaders(getApplicationContext()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info AdDetails Respon", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info AdDetails object", "" + response.getJSONObject("data"));
                                Log.d("info ProfileDetails obj", "" + response.getJSONObject("data").getJSONObject("profile_detail"));
                                Log.d("info Bids Data", "" + response.getJSONObject("data").getJSONObject("static_text"));

                                JsonObjectData = response.getJSONObject("data");
                                if (response.getJSONObject("data").getString("notification").equals("")) {
                                } else {

                                }

                                if (response.getJSONObject("data").getJSONObject("is_featured").getBoolean("is_show")) {
                                } else {

                                }

                                // noOfCol = response.getJSONObject("data").getJSONObject("ad_detail").getInt("fieldsData_column");

                                adforest_setAllViewsText(response.getJSONObject("data").getJSONObject("ad_detail"),
                                        response.getJSONObject("data").getJSONObject("profile_detail"),
                                        response.getJSONObject("data").getJSONObject("static_text"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    SettingsMain.hideDilog();
                    Log.d("info AdDetails error", String.valueOf(t));
                    Log.d("info AdDetails error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });

        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getApplicationContext(), "Internet error", Toast.LENGTH_SHORT).show();
//            getActivity().finish();
        }
    }

    private void adforest_setAllViewsText(final JSONObject data, JSONObject profileText, JSONObject buttonTexts) {
        try {

            String phoneNumber;
            String adAuthorId;
            phoneNumber = data.getString("phone");
            adAuthorId = data.getString("ad_author_id");
            //Toast.makeText(getApplicationContext(),"the phone number is :"+phoneNumber,Toast.LENGTH_LONG).show();
           String title= data.getString("ad_title");
//            textViewAdName.setText(data.getString("ad_title"));
//            textViewLocation.setText(data.getString("location_top"));

sendMessage(phoneNumber,title);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void sendMessage(String phoneNumber, String title) {
        Toast.makeText(this, "Setting Up WhatsApp!", Toast.LENGTH_SHORT).show();
        String str="I am interested in your ad "+title+" on JAPP Classifieds.\n Is it still available?\n" +
                "\n" +
                "Are you on Facebook? \n Join JAPP Classifieds Facebook group \n  http://facebook.com/groups/jappclassifieds";
        webView.setVisibility(View.INVISIBLE);
        webView.loadUrl("https://api.whatsapp.com/send?phone="+phoneNumber+"&text="+str+"%2c&source=&data=&app_absent=");
        webView.setVisibility(View.INVISIBLE);
    }
}





