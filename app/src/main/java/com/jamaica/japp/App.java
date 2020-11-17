package com.jamaica.japp;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.jamaica.japp.helper.LocaleHelper;
import com.jamaica.japp.utills.NoInternet.AppLifeCycleManager;

public class App extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		Fresco.initialize(this);
		AppLifeCycleManager.init(this);
	}
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
		MultiDex.install(this);
	}
}
