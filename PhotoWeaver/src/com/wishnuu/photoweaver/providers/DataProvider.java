package com.wishnuu.photoweaver.providers;

import android.app.Activity;
import android.content.Context;

import com.flurry.android.monolithic.sdk.impl.act;
import com.sromku.simple.fb.SimpleFacebook;
import com.wishnuu.photoweaver.entities.Album;
import com.wishnuu.photoweaver.entities.Provider;
import com.wishnuu.photoweaver.events.DialogListener;

import java.util.ArrayList;

public abstract class DataProvider {

    public static final String PROVIDER_NAME = "provider_name";

    protected String name;
    protected int logoUrl;
    protected int logoTextUrl;
    protected String description;
    // dialogListener object
    protected DialogListener dialogListener;

    protected Provider provider;

    private static Activity mActivity;

    protected Context mContext;

    public DataProvider(Context context, DialogListener dialogListener)
    {
        this.mContext = context;
        this.dialogListener = dialogListener;
    }

    public String getName() {
        return this.name;
    }

    public int getLogoUrl() {
        return this.logoUrl;
    }

    public int getLogoTextUrl() {
        return this.logoTextUrl;
    }

    public abstract void loadAlbums();

    public abstract void loadPhotos(Album album);

    public Provider getProvider() {
        return this.provider;
    }

    protected void createProvider(Activity activity)
    {
        this.provider = new Provider(this, this.getName(), "", this.logoUrl, this.logoTextUrl, "", "", "");
        this.mActivity = activity;
    }


    public abstract void doLogin();
    public abstract void doLogout();

    public abstract boolean isLoggedIn();
}
