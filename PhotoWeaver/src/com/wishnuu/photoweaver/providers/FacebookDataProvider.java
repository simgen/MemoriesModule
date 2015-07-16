package com.wishnuu.photoweaver.providers;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.android.Util;
import com.facebook.model.GraphObject;
import com.flurry.android.monolithic.sdk.impl.sr;
import com.sromku.simple.fb.*;
import com.sromku.simple.fb.SimpleFacebook.OnAlbumsRequestListener;
import com.sromku.simple.fb.SimpleFacebook.OnMainPhotosRequestListener;
import com.sromku.simple.fb.SimpleFacebook.OnPhotosRequestListener;
import com.sromku.simple.fb.entities.dto.Likes;
import com.wishnuu.photoweaverplus.R;
import com.wishnuu.photoweaver.MainActivity;
import com.wishnuu.photoweaver.entities.Album;
import com.wishnuu.photoweaver.entities.Provider;
import com.wishnuu.photoweaver.events.DialogListener;
import com.wishnuu.photoweaver.flickr.api.Photo;
//import com.sromku.simple.fb.entities.Photo;



import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FacebookDataProvider extends DataProvider {

	private SimpleFacebook mSimpleFacebook;

	@Override
	protected void createProvider(Activity activity) {
		super.createProvider(activity);
		configureSimpleFacebook();
		mSimpleFacebook = SimpleFacebook.getInstance(activity);
	}

	private static final String APP_ID = "410836425696814";
	private static final String APP_NAMESPACE = "photosplus";
	protected static final String TAG = MainActivity.class.getSimpleName();
	public JSONArray fbPhotosJson = null;
	protected static final String likesCountStr = "likesCount";
	protected static final String commentsCountStr = "commentsCount";
	protected static final String imgSrcStr = "source";
	protected static final String imgUrlStr = "imgUrl";

	private void configureSimpleFacebook()
	{

		// initialize facebook configuration
		Permissions[] permissions = new Permissions[]
				{
				Permissions.BASIC_INFO,
				Permissions.EMAIL,
				Permissions.USER_PHOTOS,
				Permissions.USER_BIRTHDAY,
				Permissions.USER_LOCATION,
				Permissions.PUBLISH_ACTION,
				Permissions.PUBLISH_STREAM
				};

		SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
		.setAppId(APP_ID)
		.setNamespace(APP_NAMESPACE)
		.setPermissions(permissions)
		.setDefaultAudience(SessionDefaultAudience.FRIENDS)
		.build();

		//varsha to do : manually set config for testing 
		SimpleFacebook.setConfiguration(configuration);
	}


	public FacebookDataProvider(Context context, DialogListener dialogListener) {
		super(context, dialogListener);
		this.name = context.getResources().getString(R.string.facebook_provider_name);
		this.logoTextUrl = R.drawable.facebook_logo_transparent;
		this.logoUrl = R.drawable.facebook;

		createProvider((Activity)context);
	}

	@Override
	public void loadAlbums() {
		//        Bundle bundle = new Bundle();
		//        this.dialogListener.onStart(bundle);
		//
		//        // TODO: hook up with actual data provider to get albums
		//        Provider provider = this.getProvider();
		//        ArrayList<Album> albums = provider.getAlbumList();
		//
		//        int index = 0;
		//        albums.add(new Album(String.valueOf(index++), "Test 1", "test 1 album", R.drawable.image_animals, 10, 4, ""));
		//        albums.add(new Album(String.valueOf(index++), "Test 2", "test 2 album", R.drawable.image_celebrities, 10, 4, ""));
		//        albums.add(new Album(String.valueOf(index++), "Test 3", "test 3 album", R.drawable.image_food, 10, 4, ""));
		//
		//        bundle.putParcelable("provider", provider);
		//        this.dialogListener.onComplete(bundle);


		// listener for albums request
		final SimpleFacebook.OnAlbumsRequestListener onAlbumsRequestListener = new SimpleFacebook.OnAlbumsRequestListener()
		{
			Bundle bundle = new Bundle();


			@Override
			public void onFail(String reason)
			{
				//                hideDialog();
				//                // insure that you are logged in before getting the friends
				//                Log.w(TAG, reason);

			}

			@Override
			public void onException(Throwable throwable)
			{
				//                hideDialog();
				//                Log.e(TAG, "Bad thing happened", throwable);
			}

			@Override
			public void onThinking()
			{
				//                showDialog();
				//                // show progress bar or something to the user while fetching profile
				//                Log.i(TAG, "Thinking...");
				dialogListener.onStart(bundle);
			}

			public JSONObject getAlbumLikeCount(com.sromku.simple.fb.entities.Album fbAlbum) throws JSONException{

				JSONArray jsonLikeArray = new JSONArray();
				JSONArray jsonCommentsArray = new JSONArray();
				JSONObject jsonCounts = new JSONObject();           	

				GraphObject fbGraphObj = fbAlbum.getGraphObject();
				JSONObject jsonGraphObj = fbGraphObj.getInnerJSONObject();

				JSONObject jsonLikeObj = new JSONObject();
				JSONObject jsonCommentsObj = new JSONObject();

				try { 
					if(!jsonGraphObj.isNull("likes")){
						jsonLikeObj = jsonGraphObj.getJSONObject("likes");

						if(jsonLikeObj.length() !=0 ) {
							jsonLikeArray = jsonLikeObj.getJSONArray("data");
							if(!jsonLikeArray.isNull(0) ) {
								jsonCounts.put(likesCountStr, jsonLikeArray.length() > 0 ? jsonLikeArray.length() : 0);
							}
						}
					} else {
						jsonCounts.put(likesCountStr, 0);           			
					}
					if(!jsonGraphObj.isNull("comments")){
						jsonCommentsObj = jsonGraphObj.getJSONObject("comments");
						if(jsonCommentsObj.length() !=0 ){
							jsonCommentsArray = jsonCommentsObj.getJSONArray("data");
							if(!jsonCommentsArray.isNull(0)) {
								jsonCounts.put(commentsCountStr, jsonCommentsArray.length() > 0 ? jsonCommentsArray.length() : 0 );
							}

						}  
					} else {
						jsonCounts.put(commentsCountStr,0 );
					}


				} catch(JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return jsonCounts ; 


			}

			@Override
			public void onComplete(List<com.sromku.simple.fb.entities.Album> albums) {

				for(int i=0; i<albums.size(); i++){

					JSONObject jsonCounts = new JSONObject();
					com.sromku.simple.fb.entities.Album fbAlbum = albums.get(i);
					Provider provider = getProvider();
					String cover_photo_url = String.format("https://graph.facebook.com/%s/picture?access_token=%s",
							fbAlbum.getCoverPhotoId(), mSimpleFacebook.getAccessToken());  
					int likeCounts = 0 ;
					int commentsCounts = 0 ;
					try {
						jsonCounts = getAlbumLikeCount(fbAlbum);
						if(jsonCounts.has(likesCountStr)) {
							likeCounts = (int) jsonCounts.get(likesCountStr);
						}
						if(jsonCounts.has(commentsCountStr)) {
							commentsCounts = (int) jsonCounts.get(commentsCountStr);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					Album album  = new Album(fbAlbum.getId(), fbAlbum.getName(), fbAlbum.getDescription(), cover_photo_url, likeCounts, commentsCounts, fbAlbum.getLink());
					provider.getAlbumList().add(album);
				}
				Provider provider = getProvider();
				bundle.putParcelable("provider", provider);
				dialogListener.onComplete(bundle);
			}

		};


		mSimpleFacebook.getAlbums(onAlbumsRequestListener);
	}

	public void getPhotoLikeCommentsCount(JSONArray data , JSONArray jsonPhotoLikeCommentsCount ) throws JSONException{

		for(int i =0 ; i < data.length() ; i++) {
			//get imagesource json 
			JSONObject photoJson = data.getJSONObject(i);
			//get likes , comments count and image id jsonobj 
			JSONObject likeCommCount= getLikeCommentCount(photoJson);
			//add above obj to jsonArray 
			jsonPhotoLikeCommentsCount.put(likeCommCount);

		}		


	}


	@Override
	public void loadPhotos(Album album) {
		final JSONArray jsonPhotoLikeCommentsCount = new JSONArray();
		Bundle bundle = new Bundle();

		final SimpleFacebook.OnPhotosRequestListener onPhotosRequestListener = new SimpleFacebook.OnPhotosRequestListener()
		{
			Bundle bundle = new Bundle();

			@Override
			public void onFail(String reason)
			{
				//                hideDialog();
				//                // insure that you are logged in before getting the friends
				//                Log.w(TAG, reason);

			}

			@Override
			public void onException(Throwable throwable)
			{
				//                hideDialog();
				//                Log.e(TAG, "Bad thing happened", throwable);
			}

			@Override
			public void onThinking()
			{
				//                showDialog();
				//                // show progress bar or something to the user while fetching profile
				//                Log.i(TAG, "Thinking...");
				dialogListener.onStart(bundle);
			}

			@Override
			//public void onComplete(Bundle bundleToUse, List<com.sromku.simple.fb.entities.Photo> photos) {
			//data have photos details
			public void onComplete(Bundle bundleToUse, List<com.sromku.simple.fb.entities.Photo> photos,JSONArray data) {

				Album album = bundleToUse.getParcelable("album");
				List<Photo> albumPhotosList = album.getPhotoList();
				try {
					//get  like and comments counts for all photos in the albums since flickr photo don't support this
					getPhotoLikeCommentsCount(data,jsonPhotoLikeCommentsCount);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				for(int i=0; i<photos.size(); i++){
					String likeCount = "0";
					String commCount = "0";
					com.sromku.simple.fb.entities.Photo fbPhoto = photos.get(i);

					//look up in jsonArray for id and get likes and comment json
					for (int index = 0 ; index < jsonPhotoLikeCommentsCount.length(); index++) { 
						try {
							JSONObject likeCommentJson = (JSONObject) jsonPhotoLikeCommentsCount.get(index);
							String fbId = fbPhoto.getId();
							String likeId = likeCommentJson.getString("id");

							if(fbId.equals(likeId) ) {
								likeCount = likeCommentJson.getString(likesCountStr);
								commCount = likeCommentJson.getString(commentsCountStr);

							} 

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					Photo albumPhoto = new Photo(fbPhoto.getId(), fbPhoto.getTitle(), "", fbPhoto.getUri(),likeCount , commCount);
					albumPhotosList.add(albumPhoto);
				}

				dialogListener.onComplete(bundleToUse);
			}
		};

		bundle.putParcelable("album", album);
		mSimpleFacebook.getPhotos(album.getId(), bundle, onPhotosRequestListener);
	}

	//generic method to be called to get likes and comments count for the json object passed 

	public JSONObject getLikeCommentCount (JSONObject jsonObj) {
		JSONArray jsonLikeArray = new JSONArray();
		JSONArray jsonCommentsArray = new JSONArray();

		JSONObject jsonCounts = new JSONObject(); 
		JSONObject jsonLikeObj = new JSONObject();
		JSONObject jsonCommentsObj = new JSONObject();
		try { 
			jsonCounts.put("id", jsonObj.get("id")); //add object id 
			if(!jsonObj.isNull("likes")){
				jsonLikeObj = jsonObj.getJSONObject("likes");

				if(jsonLikeObj.length() !=0 ) {
					jsonLikeArray = jsonLikeObj.getJSONArray("data");
					if(!jsonLikeArray.isNull(0) ) {
						jsonCounts.put(likesCountStr, jsonLikeArray.length() > 0 ? jsonLikeArray.length() : 0);
					}
				}
			} else {
				jsonCounts.put(likesCountStr, 0);           			
			}
			if(!jsonObj.isNull("comments")){
				jsonCommentsObj = jsonObj.getJSONObject("comments");
				if(jsonCommentsObj.length() !=0 ){
					jsonCommentsArray = jsonCommentsObj.getJSONArray("data");
					if(!jsonCommentsArray.isNull(0)) {
						jsonCounts.put(commentsCountStr, jsonCommentsArray.length() > 0 ? jsonCommentsArray.length() : 0 );
					}

				}  
			} else {
				jsonCounts.put(commentsCountStr,0 );
			}



		} catch(JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonCounts ; 

	}

	//load bigImage when thumbnail pic is clicked in facebook picture list
	public void loadMainPhotos(final Photo photo,String albumId) {

		Bundle bundle = new Bundle();


		final OnMainPhotosRequestListener onMainPhotosRequestListener = new OnMainPhotosRequestListener()
		{
			Bundle bundle = new Bundle();

			@Override
			public void onFail(String reason)
			{
				Log.w(TAG, reason);

			}

			@Override
			public void onException(Throwable throwable)
			{
				//                hideDialog();
				Log.e(TAG, "Bad thing happened", throwable);
			}

			@Override
			public void onThinking()
			{
				//                showDialog();
				//                // show progress bar or something to the user while fetching profile
				Log.i(TAG, "Thinking...");
				dialogListener.onStart(bundle);
			}

			@Override
			public void onComplete(Bundle bundleToUse ,JSONArray data ) {

				//Log.d(TAG, "MainImageListener : onComplete data : " + data );

				try {
					// there are almost 5 diff sizes in the data json array pick the 2nd largest size 
					if(!data.isNull(1)) {
						JSONObject imgSrc = data.getJSONObject(1);
						String imgUrl = (String) imgSrc.get(imgSrcStr);					 
						bundleToUse.putString(imgUrlStr,imgUrl );
						bundleToUse.putString(commentsCountStr,photo.mCommentsCount );
						bundleToUse.putString(likesCountStr,photo.mLikesCount);
					} else {
						//bundleToUse.putString("imgUrl",  photo.getPartialUrl());
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				dialogListener.onComplete(bundleToUse);
			}
		};

		mSimpleFacebook.getFullPhoto(albumId  , photo.id, bundle, onMainPhotosRequestListener); 
	}



	// Login listener
	private SimpleFacebook.OnLoginListener mOnLoginListener = new SimpleFacebook.OnLoginListener()
	{

		@Override
		public void onFail(String reason)
		{
			//            mTextStatus.setText(reason);
			Log.w("fB loggin error", "Failed to login");
			System.out.println("fbdp onFail");
		}

		@Override
		public void onException(Throwable throwable)
		{
			//            mTextStatus.setText("Exception: " + throwable.getMessage());
			Log.e("fb exception", "Bad thing happened", throwable);
		}

		@Override
		public void onThinking()
		{
			// show progress bar or something to the user while login is happening
			//            mTextStatus.setText("Thinking...");

		}

		@Override
		public void onLogin()
		{
			// change the state of the button or do whatever you want
			//            mTextStatus.setText("Logged in");
			//            loggedInUIState();
			//            toast("You are logged in");

		}

		@Override
		public void onNotAcceptingPermissions()
		{
			//            toast("You didn't accept read permissions");

		}
	};

	// Logout listener
	private SimpleFacebook.OnLogoutListener mOnLogoutListener = new SimpleFacebook.OnLogoutListener()
	{

		@Override
		public void onFail(String reason)
		{
			//            mTextStatus.setText(reason);
			//            Log.w(TAG, "Failed to login");
		}

		@Override
		public void onException(Throwable throwable)
		{
			//            mTextStatus.setText("Exception: " + throwable.getMessage());
			//            Log.e(TAG, "Bad thing happened", throwable);
		}

		@Override
		public void onThinking()
		{
			// show progress bar or something to the user while login is happening
			//            mTextStatus.setText("Thinking...");
		}

		@Override
		public void onLogout()
		{
			//            // change the state of the button or do whatever you want
			//            mTextStatus.setText("Logged out");
			//            loggedOutUIState();
			//            toast("You are logged out");
		}

	};


	@Override
	public void doLogin() {
		mSimpleFacebook.login(mOnLoginListener);

	}

	@Override
	public void doLogout() {
		mSimpleFacebook.logout(mOnLogoutListener);
	}

	@Override
	public boolean isLoggedIn() {
		return mSimpleFacebook.isLogin();
	}

}
