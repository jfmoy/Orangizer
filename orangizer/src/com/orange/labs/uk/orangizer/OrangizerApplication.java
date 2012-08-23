package com.orange.labs.uk.orangizer;

import com.orange.labs.uk.orangizer.dependencies.OrangizerDependencyResolverImpl;

import android.app.Application;

public class OrangizerApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		
		// Simply initialize dependency resolver
		OrangizerDependencyResolverImpl.initialize(getApplicationContext());
	}
	
}
