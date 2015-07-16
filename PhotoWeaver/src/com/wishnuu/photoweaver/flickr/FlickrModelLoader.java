package com.wishnuu.photoweaver.flickr;

import android.content.Context;
import android.net.Uri;

import com.bumptech.glide.loader.model.Cache;
import com.bumptech.glide.loader.model.UrlModelLoader;
import com.wishnuu.photoweaver.flickr.api.Api;
import com.wishnuu.photoweaver.flickr.api.Photo;

import java.net.URL;

/**
 * An implementation of ModelStreamLoader that leverages the StreamOpener class and the ExecutorService backing the
 * ImageManager to download the image and resize it in memory before saving the resized version
 * directly to the disk cache.
 */
public class FlickrModelLoader extends UrlModelLoader<Photo> {

    public FlickrModelLoader(Context context, Cache<URL> modelCache) {
        super(context, modelCache);
    }

    @Override
    protected String getUrl(Photo model, int width, int height) {
       String photoUrl = Api.getPhotoURL(model, width, height);
     
        //flicker photos are loaded asynchronously and this is the place to set imagePath and imageUri that are to be used by Aviary and CanvasPop
        model.imagePath = photoUrl;
        model.imageUri = Uri.parse(model.imagePath);
        return photoUrl;
    }

    @Override
    public String getId(Photo model) {
        return model.id;
    }
}
