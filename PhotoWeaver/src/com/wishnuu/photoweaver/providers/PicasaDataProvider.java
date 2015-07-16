package com.wishnuu.photoweaver.providers;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.wishnuu.photoweaverplus.R;
import com.wishnuu.photoweaver.entities.Album;
import com.wishnuu.photoweaver.events.DialogListener;

import java.util.ArrayList;

public class PicasaDataProvider extends DataProvider {

    public PicasaDataProvider(Context context,DialogListener dialogListener)
    {
        super(context, dialogListener);
        this.name = context.getResources().getString(R.string.picasa_provider_name);
        this.logoTextUrl = R.drawable.facebook_text;
        this.logoUrl = R.drawable.facebook;

        createProvider((Activity)context);
    }

    @Override
    public void loadAlbums()
    {
        Bundle bundle = new Bundle();
        bundle.putString(PROVIDER_NAME, this.getName());
        this.dialogListener.onStart(bundle);

        this.dialogListener.onComplete(bundle);
    }

    @Override
    public void loadPhotos(Album album){

    }

    @Override
    public void doLogin() {

    }

    @Override
    public void doLogout() {

    }

    @Override
    public boolean isLoggedIn() {
        return false;
    }
}
