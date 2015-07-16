package com.wishnuu.photoweaver.providers;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.wishnuu.photoweaverplus.R;
import com.wishnuu.photoweaver.entities.Album;
import com.wishnuu.photoweaver.entities.Provider;
import com.wishnuu.photoweaver.events.DialogListener;

public class FlickrDataProvider extends DataProvider {

    public FlickrDataProvider(Context context, DialogListener dialogListener) {
        super(context, dialogListener);
        this.name = context.getResources().getString(R.string.flickr_provider_name);
        this.logoTextUrl = R.drawable.flickr_logo_transparent;
        this.logoUrl = R.drawable.flickr;

        createProvider((Activity)context);
    }

    @Override
    public void loadAlbums() {
        Bundle bundle = new Bundle();
        Provider provider = this.getProvider();
        bundle.putParcelable("provider", provider);
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
        return true;
    }
}