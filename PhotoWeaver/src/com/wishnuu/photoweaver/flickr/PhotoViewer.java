package com.wishnuu.photoweaver.flickr;

import com.wishnuu.photoweaver.flickr.api.Photo;

import java.util.ArrayList;
import java.util.List;

public interface PhotoViewer {
    public void onPhotosUpdated(ArrayList<Photo> photos);
}
