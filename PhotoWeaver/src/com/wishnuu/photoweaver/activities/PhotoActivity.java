package com.wishnuu.photoweaver.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wishnuu.photoweaver.MainActivity;
import com.wishnuu.photoweaver.entities.Album;
import com.wishnuu.photoweaver.events.DialogListener;
import com.wishnuu.photoweaver.events.ListenerError;
import com.wishnuu.photoweaver.flickr.api.Photo;
import com.wishnuu.photoweaver.providers.FacebookDataProvider;
import com.wishnuu.photoweaver.viewAdapters.ImageLoader;
import com.wishnuu.photoweaver.viewAdapters.PhotoAdapter;
import com.wishnuu.photoweaverplus.R;

public class PhotoActivity extends Activity {

	protected static final String tag = MainActivity.class.getSimpleName();
	Album album;
	ArrayList<Photo> photoList;

	// Android Components
	private LinearLayout dataSectionView, frameView;

	// Other Components
	GridView gridview;
	private List<Photo> mPhotoList;
	private String mAlbumId;
	private ProgressDialog mDialog;
	final ImageLoader imgLoader = new ImageLoader(this);
	int mPosition = 0;
	protected static final String likesCountStr = "likesCount";
	protected static final String commentsCountStr = "commentsCount";
//called to create photogrid view for facebook
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.photos_view);

		this.album = (Album) getIntent().getParcelableExtra("album");
		final PhotoAdapter adapter = new PhotoAdapter(this, R.layout.photos_view_item, this.album.getPhotoList());
		mPhotoList = this.album.getPhotoList();
		//Log.e("photoactivity" , "photolist : " + mPhotoList.get(1));
		mAlbumId = this.album.getId();

		gridview = (GridView) findViewById(R.id.gridView);
		gridview.setAdapter(adapter);

		gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent,
					View view,
					int position, long id) {


				Log.d(tag , "photo selected  " + position );
				Photo photo = mPhotoList.get(position);
				Log.d("loadMainPhoto", "main photo comments: " + photo.mCommentsCount);
				FacebookDataProvider d = new FacebookDataProvider(PhotoActivity.this, new ResponseListener());
				d.loadMainPhotos(photo,mAlbumId);
				/*//old working code move to displayMainPhoto
                setContentView(R.layout.facebook_full_photo);
            	ImageView mImageView = (ImageView) findViewById(R.id.fbFullImgView);
            	imgLoader.DisplayImage(photo.getPartialUrl(), mImageView);
				 */
				}
		});
	}

	// To receive the response after authentication
	private final class ResponseListener implements DialogListener {



		@Override
		public void onStart(Bundle values) {
			Log.d(tag, "onStart");
			mDialog = new ProgressDialog(PhotoActivity.this);
			mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mDialog.setMessage("Loading...");
		}

		@Override
		public void onComplete(Bundle values) {

			Log.d(tag, "onComplete");
			mDialog.dismiss();
			//fetch img src and display full image . img Url will fallback to 
			//partialUrl if we don't find proper source
			
			String imgUrl = values.getString("imgUrl");
			String commCount = values.getString(commentsCountStr, "0");
			String likeCount = values.getString(likesCountStr, "0");
			
			setContentView(R.layout.facebook_full_photo);
			
			ImageView mImageView = (ImageView) findViewById(R.id.fbFullImgView);
			imgLoader.DisplayImage(imgUrl, mImageView);
			
			TextView commText = (TextView) findViewById(R.id.commentsCount);
			if(commCount.equals("25")){
				commText.setText(commCount + "+ Comments");

			} else { 
				commText.setText(commCount + " Comments");
			}
			
			TextView likeText = (TextView) findViewById(R.id.likesCount);
			
			if(likeCount.equals("25")) {
				likeText.setText(likeCount + "+ Likes");
			} else {
				likeText.setText(likeCount + " Likes");
			}
			
			
			
		}

		@Override
		public void onError(ListenerError error) {
			Log.d(tag, "ListenerError");
			error.printStackTrace();
		}

		@Override
		public void onCancel() {
			Log.d(tag, "Cancelled");
		}

		@Override
		public void onBack() {
			Log.d(tag, "Dialog Closed by pressing Back Key");
		}
	}



}