package com.wishnuu.photoweaver;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.flurry.android.FlurryAgent;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Utilities {
    public static final String PHOTOWEAVER_APPNAME = "PhotoWeaver";
    public static final String LOG_TAG = "PhotoWeaver";
    public static final String EXTRA_IMAGEURI = "image";
    public static final String EXTRA_FB_SESSION = "fb_session";
    public static final String FLURRY_EVENT_FLICKR_CLICK = "FlickrClicked";
    public static final String FLURRY_EVENT_GALLERY_CLICK = "GalleryClicked";
    public static final String FLURRY_EVENT_CAMERA_CLICK = "CameraClicked";
    public static final String FLURRY_EVENT_CAMERA_IMAGE_SELECTED = "CameraImageSelected";
    public static final String FLURRY_EVENT_CAMERA_IMAGE_NOT_SELECTED = "CameraImageNotSelected";
    public static final String FLURRY_EVENT_CAMERA_PARAMETER = "CameraNotSelectionResponseCode";
    public static final String FLURRY_EVENT_GALLERY_IMAGE_SELECTED = "GalleryImageSelected";
    public static final String FLURRY_EVENT_GALLERY_IMAGE_NOT_SELECTED = "GalleryImageNotSelected";
    public static final String FLURRY_EVENT_FACEBOOK_LOGIN = "FacebookLogin";
    public static final String FLURRY_EVENT_AVIARY_EDITED = "AviaryEditing";
    public static final String FLURRY_EVENT_AVIARY_CLICK = "AviaryEditingClicked";
    public static final String FLURRY_EVENT_AVIARY_CANCELED = "AviaryEditingCanceled";
    public static final String FLURRY_EVENT_CANVAS_CLICK = "CanvasPopClicked";
    public static final String FLURRY_EVENT_POSTPHOTOS_CLICK = "PostPhotoClicked";
    public static final String FLURRY_EVENT_POSTPHOTOS_SUCCEEDED = "PostPhotoSucceeded";
    public static final String FLURRY_EVENT_POSTPHOTO_FAILED = "PostPhotoFailed";
    public static final String FLURRY_EVENT_POSTPHOTO_FAILED_MESSAGE_PARAM = "PostPhotoFailedMessageParam";
    public static final String FLURRY_ERROR_POST_PHOTO = "PostPhotoError";
    public static final String FLURRY_ERROR_FACEBOOK_LOGIN = "FacebookLoginError";
    public static final String FLURRY_ERROR_FACEBOOK_SESSION = "FacebookSessionError";
    public static final String FLURRY_ERROR_CLASS_SEVERE = "SevereError";
    public static final String FLURRY_ERROR_PHOTOVIEW_NO_EXTRAS_SENT = "PhotoviewNoExtrasSent";
    public static final String FLURRY_ERROR_PHOTOVIEW_EMPTY_PHOTOURI = "PhotoviewEmptyPhotoUri";
    public static final String FLURRY_ERROR_UPDATING_IMAGEVIEW = "UpdateImageView";
    public static final String FLURRY_ERROR_VIEWPAGER_NO_EXTRAS_SENT = "UpdateImageView";
    public static final String EXTRA_CURRENT_PHOTOS = "EXTRA_CURRENT_PHOTOS";
    public static final String EXTRA_POSITION = "EXTRA_POSITION";
    public static final String EXTRA_SEARCH_STRING = "EXTRA_SEARCH_STRING";
    public static final String EXTRA_FROM_FACEBOOK = "EXTRA_FROM_FACEBOOK";

    public static boolean isFlurryEnabled = false;

    public static void StartSessionFlurry(Activity activity) {
        try {
            FlurryAgent.onStartSession(activity, "RQYRWSFJPZNRRPWBTSW2");
            isFlurryEnabled = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void StopSessionFlurry(Activity activity) {
        try {
            if (isFlurryEnabled) {
                FlurryAgent.onEndSession(activity);
                isFlurryEnabled = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void LogFlurry(String eventName, String paramName, String paramValue) {
        try {
            if (isFlurryEnabled) {
                Map<String, String> articleParams = new HashMap<String, String>();
                articleParams.put(paramName, paramValue);
                FlurryAgent.logEvent(eventName, articleParams);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void LogFlurry(String eventName) {
        try {
            if (isFlurryEnabled) {
                FlurryAgent.logEvent(eventName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logError(String errorId, String errorMessage, String errorClass) {
        try {
            if (isFlurryEnabled) {
                FlurryAgent.onError(errorId, errorMessage, errorClass);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logHashKeyFromStore(PackageInfo info) {
        try {
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }
}

