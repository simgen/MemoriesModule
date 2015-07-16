package com.wishnuu.photoweaver.flickr.api;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;

import com.wishnuu.photoweaver.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

public class Photo implements Parcelable {
    public String id;
    public String owner;
    public String title;
    public String server;
    public String farm;
    public String secret;
    private String partialUrl = null;
    public String mLikesCount = null;
    public String mCommentsCount = null;
    

    public String imagePath;
    public Uri imageUri;

    public Photo() {
    }

    public Photo(String mediaFilePath, Uri mediaFileUri) {
        this.imagePath = mediaFilePath;
        this.imageUri = mediaFileUri;
    }

    public Photo(Context context, Uri uri) {
        this.imageUri = uri;
        this.setPathFromUri(uri, context);
    }

    public Photo(String path) {
        this.imagePath = path;
        this.imageUri = Uri.parse(this.imagePath);
    }

    private void setPathFromUri(Uri uri, Context context) {
        if (uri == null) {
            Log.d(Utilities.LOG_TAG, "Invalid file path uri");
            return;
        }

        if (uri.getScheme() == null) {
            Log.d(Utilities.LOG_TAG, "can't get scheme");
        } else if (uri.getScheme().toString().compareTo("content") == 0) {
            String[] proj = {MediaStore.Images.Media.DATA};
            CursorLoader loader = new CursorLoader(context, uri, proj, null, null, null);
            Cursor cursor = loader.loadInBackground();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            imagePath = cursor.getString(column_index);
            cursor.close();
        }

        if (imagePath == null) {
            imagePath = uri.getPath();
        }
    }

    public Photo(JSONObject jsonPhoto) throws JSONException {
        this.id = jsonPhoto.getString("id");
        this.owner = jsonPhoto.getString("owner");
        this.title = jsonPhoto.optString("title", "");
        this.server = jsonPhoto.getString("server");
        this.farm = jsonPhoto.getString("farm");
        this.secret = jsonPhoto.getString("secret");
    }

    public Photo(String id, String title, String owner, String partialUrl ) {
        this.id = id;
        this.title = title;
        this.owner = owner;
        this.partialUrl = partialUrl;
        
    }
    
    public Photo(String id, String title, String owner, String partialUrl , String likesCount , String commentsCount ) {
        this.id = id;
        this.title = title;
        this.owner = owner;
        this.partialUrl = partialUrl;
        this.mCommentsCount = commentsCount;
        this.mLikesCount = likesCount;
              
    }

    public String getPartialUrl() {
        if (partialUrl == null) {
            partialUrl = Api.getCacheableUrl(this);
        }
        return partialUrl;
    }
    // added comments and like for facebook pics - varsha borhade
    
    public String getCommentsCount() {
    	if(mCommentsCount == null) {
    		return null;
    	}
    	return mCommentsCount;
    }
    
    public String getLikeCount() {
    	if(mLikesCount == null) {
    		return null;
    	}
    	return mLikesCount;
    }
    

    /**
     * **************************
     * PARCELABLE IMPLEMENTATION *
     * ***************************
     */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(owner);
        out.writeString(title);
        out.writeString(server);
        out.writeString(farm);
        out.writeString(secret);
        out.writeString(partialUrl);

        if (imageUri != null) {
            out.writeString(imageUri.toString());
        } else {
            out.writeString("");
        }
        out.writeString(imagePath);
        if(mCommentsCount != null ) {
            out.writeString(mCommentsCount);
        } else {
            out.writeString("");
        }
        
        if(mLikesCount != null) {
            out.writeString(mLikesCount);
        } else {
            out.writeString("");
        }
    }

    public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    private Photo(Parcel in) {
        this();
        id = in.readString();
        owner = in.readString();
        title = in.readString();
        server = in.readString();
        farm = in.readString();
        secret = in.readString();
        partialUrl = in.readString();       
        String parcelValue = in.readString();
        if (parcelValue != null && !parcelValue.isEmpty()) {
            imageUri = Uri.parse(parcelValue);
        }

        imagePath = in.readString();
        mCommentsCount = in.readString();
        mLikesCount = in.readString();
    }
}
