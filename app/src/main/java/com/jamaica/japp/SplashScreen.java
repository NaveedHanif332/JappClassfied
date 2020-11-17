package com.jamaica.japp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.jamaica.japp.Shop.shopMenuModel;
import com.jamaica.japp.helper.LocaleHelper;
import com.jamaica.japp.home.HomeActivity;
import com.jamaica.japp.home.helper.AdPostImageModel;
import com.jamaica.japp.home.helper.CalanderTextModel;
import com.jamaica.japp.home.helper.Location_popupModel;
import com.jamaica.japp.home.helper.ProgressModel;
import com.jamaica.japp.modelsList.permissionsModel;
import com.jamaica.japp.signinorup.MainActivity;
import com.jamaica.japp.utills.Network.RestService;
import com.jamaica.japp.utills.SettingsMain;
import com.jamaica.japp.utills.UrlController;
import com.loopj.android.http.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends AppCompatActivity {

    public static JSONObject jsonObjectAppRating, jsonObjectAppShare;
    public static boolean gmap_has_countries = false, app_show_languages = false;
    public static JSONArray app_languages;
    public static String languagePopupTitle, languagePopupClose, gmap_countries;
    Activity activity;
    SettingsMain setting;
    JSONObject jsonObjectSetting;
    boolean isRTL = false;
    String gmap_lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Configuration configuration = getResources().getConfiguration();
        configuration.fontScale = (float) 1; //0.85 small size, 1 normal size, 1,15 big etc

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        getBaseContext().getResources().updateConfiguration(configuration, metrics);

        activity = this;
        setting = new SettingsMain(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", false)) {

            setting.setUserLogin("0");

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.apply();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (NoSuchAlgorithmException e) {

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (SettingsMain.isConnectingToInternet(this)) {
            adforest_getSettings();
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(SplashScreen.this);
            alert.setTitle(setting.getAlertDialogTitle("error"));
            alert.setCancelable(false);
            alert.setMessage(setting.getAlertDialogMessage("internetMessage"));
            alert.setPositiveButton(setting.getAlertOkText(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog,
                                    int which) {
                    dialog.dismiss();
                    SplashScreen.this.recreate();
                }
            });
            alert.show();
        }


    }

    public void adforest_getSettings() {
        RestService restService =
                UrlController.createService(RestService.class);
        try {

            Call<ResponseBody> myCall = restService.getSettings(UrlController.AddHeaders(this));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());
                            Log.d("info settings Responce", "" + response);
                            if (response.getBoolean("success")) {
                                jsonObjectSetting = response.getJSONObject("data");

                                setting.setMainColor(jsonObjectSetting.getString("main_color"));

                                isRTL = jsonObjectSetting.getBoolean("is_rtl");
                                setting.setRTL(isRTL);
                                setting.setAlertDialogTitle("error", jsonObjectSetting.getJSONObject("internet_dialog").getString("title"));
                                setting.setAlertDialogMessage("internetMessage", jsonObjectSetting.getJSONObject("internet_dialog").getString("text"));
                                setting.setAlertOkText(jsonObjectSetting.getJSONObject("internet_dialog").getString("ok_btn"));
                                setting.setAlertCancelText(jsonObjectSetting.getJSONObject("internet_dialog").getString("cancel_btn"));

                                setting.setAlertDialogTitle("info", jsonObjectSetting.getJSONObject("alert_dialog").getString("title"));
                                setting.setAlertDialogMessage("confirmMessage", jsonObjectSetting.getJSONObject("alert_dialog").getString("message"));

                                setting.setAlertDialogMessage("waitMessage", jsonObjectSetting.getString("message"));

                                setting.setAlertDialogMessage("search", jsonObjectSetting.getJSONObject("search").getString("text"));
                                setting.setAlertDialogMessage("catId", jsonObjectSetting.getString("cat_input"));
                                setting.setAlertDialogMessage("location_type", jsonObjectSetting.getString("location_type"));
                                setting.setAlertDialogMessage("gmap_lang", jsonObjectSetting.getString("gmap_lang"));

                                gmap_lang = jsonObjectSetting.getString("gmap_lang");

                                setting.setGoogleButn(jsonObjectSetting.getJSONObject("registerBtn_show").getBoolean("google"));
                                setting.setfbButn(jsonObjectSetting.getJSONObject("registerBtn_show").getBoolean("facebook"));

                                JSONObject alertDialog = jsonObjectSetting.getJSONObject("dialog").getJSONObject("confirmation");
                                setting.setGenericAlertTitle(alertDialog.getString("title"));
                                setting.setGenericAlertMessage(alertDialog.getString("text"));
                                setting.setGenericAlertOkText(alertDialog.getString("btn_ok"));
                                setting.setGenericAlertCancelText(alertDialog.getString("btn_no"));
                                setting.setAdShowOrNot(true);

                                setting.isAppOpen(jsonObjectSetting.getBoolean("is_app_open"));
                                setting.checkOpen(jsonObjectSetting.getBoolean("is_app_open"));
                                setting.setGuestImage(jsonObjectSetting.getString("guest_image"));

                                JSONObject jsonObjectLocationPopup = jsonObjectSetting.optJSONObject("location_popup");
                                Location_popupModel Location_popupModel = new Location_popupModel();
                                Location_popupModel.setSlider_number(jsonObjectLocationPopup.optInt("slider_number"));
                                Location_popupModel.setSlider_step(jsonObjectLocationPopup.optInt("slider_step"));
                                Location_popupModel.setLocation(jsonObjectLocationPopup.optString("location"));
                                Location_popupModel.setText(jsonObjectLocationPopup.optString("text"));
                                Location_popupModel.setBtn_submit(jsonObjectLocationPopup.optString("btn_submit"));
                                Location_popupModel.setBtn_clear(jsonObjectLocationPopup.optString("btn_clear"));
                                setting.setLocationPopupModel(Location_popupModel);

                                JSONObject jsonObjectLocationSettings = jsonObjectSetting.optJSONObject("gps_popup");
                                setting.setShowNearby(jsonObjectSetting.optBoolean("show_nearby"));
                                setting.setGpsTitle(jsonObjectLocationSettings.optString("title"));
                                setting.setGpsText(jsonObjectLocationSettings.optString("text"));
                                setting.setGpsConfirm(jsonObjectLocationSettings.optString("btn_confirm"));
                                setting.setGpsCancel(jsonObjectLocationSettings.optString("btn_cancel"));

                                setting.setAdsPositionSorter(jsonObjectSetting.optBoolean("ads_position_sorter"));


                                setting.setNotificationTitle("");
                                setting.setNotificationMessage("");
                                setting.setNotificationTitle("");

                                if (setting.getAppOpen()) {
                                    setting.setNoLoginMessage(jsonObjectSetting.optString("notLogin_msg"));
                                }

                                setting.setFeaturedScrollEnable(jsonObjectSetting.optBoolean("featured_scroll_enabled"));
                                if (setting.isFeaturedScrollEnable()) {
                                    setting.setFeaturedScroolDuration(jsonObjectSetting.optJSONObject("featured_scroll").getInt("duration"));
                                    setting.setFeaturedScroolLoop(jsonObjectSetting.optJSONObject("featured_scroll").getInt("loop"));
                                }

                                jsonObjectAppRating = jsonObjectSetting.optJSONObject("app_rating");
                                jsonObjectAppShare = jsonObjectSetting.optJSONObject("app_share");

                                gmap_has_countries = jsonObjectSetting.optBoolean("gmap_has_countries");
                                if (gmap_has_countries) {
                                    gmap_countries = jsonObjectSetting.optString("gmap_countries");
                                }
                                app_show_languages = jsonObjectSetting.optBoolean("app_show_languages");

                                if (app_show_languages) {
                                    languagePopupTitle = jsonObjectSetting.optString("app_text_title");
                                    languagePopupClose = jsonObjectSetting.optString("app_text_close");
                                    app_languages = jsonObjectSetting.optJSONArray("app_languages");

                                }

                                try {
                                    ProgressModel progressModel = new ProgressModel();
                                    JSONObject progressJsonObject = jsonObjectSetting.optJSONObject("upload").optJSONObject("progress_txt");
                                    progressModel.setTitle(progressJsonObject.optString("title"));
                                    progressModel.setSuccessTitle(progressJsonObject.optString("title_success"));
                                    progressModel.setFailTitles(progressJsonObject.optString("title_fail"));
                                    progressModel.setSuccessMessage(progressJsonObject.optString("msg_success"));
                                    progressModel.setFailMessage(progressJsonObject.optString("msg_fail"));
                                    progressModel.setButtonText(progressJsonObject.optString("btn_ok"));
                                    SettingsMain.setProgressModel(progressModel);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    permissionsModel permissionsModel = new permissionsModel();
                                    JSONObject permissionJsonObject = jsonObjectSetting.optJSONObject("permissions");
                                    permissionsModel.setTitle(permissionJsonObject.optString("title"));
                                    permissionsModel.setDesc(permissionJsonObject.optString("desc"));
                                    permissionsModel.setBtn_goTo(permissionJsonObject.optString("btn_goto"));
                                    permissionsModel.setBtnCancel(permissionJsonObject.optString("btn_cancel"));
                                    SettingsMain.setPermissionsModel(permissionsModel);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    AdPostImageModel adPostImageModel = new AdPostImageModel();
                                    JSONObject adpostInmageJsonObject = jsonObjectSetting.optJSONObject("ad_post");
                                    adPostImageModel.setImg_size(adpostInmageJsonObject.optString("img_size"));
                                    adPostImageModel.setImg_message(adpostInmageJsonObject.optString("img_message"));
                                    adPostImageModel.setDim_is_show(adpostInmageJsonObject.optBoolean("dim_is_show"));
                                    if (adpostInmageJsonObject.optBoolean("dim_is_show")) {
                                        adPostImageModel.setDim_width(adpostInmageJsonObject.optString("dim_width"));
                                        adPostImageModel.setDim_height(adpostInmageJsonObject.optString("dim_height"));
                                        adPostImageModel.setDim_height_message(adpostInmageJsonObject.optString("dim_height_message"));
                                    }
                                    setting.setAdPostImageModel(adPostImageModel);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                setting.setShopUrl(jsonObjectSetting.optString("app_page_test_url"));

                                ArrayList<shopMenuModel> menuModelArrayList = new ArrayList<>();
                                JSONArray jsonArray = jsonObjectSetting.optJSONArray("shop_menu");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    shopMenuModel menuModel = new shopMenuModel();
                                    menuModel.setTitle(jsonObject.getString("title"));
                                    menuModel.setUrl(jsonObject.getString("url"));
                                    menuModelArrayList.add(menuModel);
                                }
                                setting.setShopMenu(menuModelArrayList);

                                CalanderTextModel calanderTextModel = new CalanderTextModel();
                                JSONObject calenderJsonObject = jsonObjectSetting.optJSONObject("calander_text");

                                calanderTextModel.setBtn_ok(calenderJsonObject.optString("ok_btn"));
                                calanderTextModel.setBtn_cancel(calenderJsonObject.optString("cancel_btn"));
                                calanderTextModel.setTitle(calenderJsonObject.optString("date_time"));
                                setting.setCalanderTextModel(calanderTextModel);

                                setting.setAlertDialogMessage("search_text", jsonObjectSetting.getString("search_text"));

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Do something after 100ms

                                        if (setting.getUserLogin().equals("0")) {
                                            if (setting.getAppOpen()) {
                                                SharedPreferences.Editor editor = activity.getSharedPreferences("com.adforest", MODE_PRIVATE).edit();
                                                editor.putString("isSocial", "false");
                                                editor.apply();
                                                Intent intent = new Intent(activity, HomeActivity.class);
                                                startActivity(intent);
                                                activity.overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                                                activity.finish();
                                                setting.setUserEmail("");
                                                setting.setUserImage("");

                                                updateViews(gmap_lang);
                                            } else {
                                                SplashScreen.this.finish();

                                                updateViews(gmap_lang);

                                                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                                            }
                                        } else {
                                            SplashScreen.this.finish();

                                            updateViews(gmap_lang);

                                            setting.isAppOpen(false);
                                            Intent intent = new Intent(SplashScreen.this, HomeActivity.class);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                                        }

                                        if (app_show_languages && !setting.isLanguageChanged()) {
                                            if (setting.getLanguageRtl()) {
                                                updateViews("ur");
                                            } else {
                                                updateViews("en");
                                            }
                                        }
                                    }
                                }, 2000);
                            } else {
                                Toast.makeText(activity, response.get("message").toString(), Toast.LENGTH_SHORT).show();
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
                    Log.d("info settings error", String.valueOf(t));
                    Log.d("info settings error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    private void updateViews(String languageCode) {
        LocaleHelper.setLocale(this, languageCode);
    }
}
