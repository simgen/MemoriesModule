
package com.wishnuu.photoweaver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.aviary.android.feather.FeatherActivity;
import com.aviary.android.feather.library.Constants;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.bumptech.glide.loader.image.ImageManagerLoader;
import com.bumptech.glide.loader.model.Cache;
import com.bumptech.glide.loader.transformation.CenterCrop;
import com.bumptech.glide.presenter.ImagePresenter;
import com.bumptech.glide.presenter.target.Target;
import com.wishnuu.photoweaverplus.R;
import com.wishnuu.photoweaver.flickr.FlickrModelLoader;
import com.wishnuu.photoweaver.flickr.api.Photo;
import com.wishnuu.photoweaver.photoview.HackyViewPager;

import java.net.URL;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

public class ViewPagerActivity extends Activity {

    private final Cache<URL> urlCache = new Cache<URL>();

    public static final int AVIARY_EDIT_REQUEST_CODE = 1;
    public static final String FACEBOOK_PUBLISH_ACTION = "publish_actions";
    PagerAdapter pagerAdapter;
    ViewPager viewPager;
    ArrayList<Photo> photoList;
    private Photo photo = null;
    private Bitmap imageBitmap;
    View mImageContainer;
    ImageView mImage;
    boolean fromFb;

    @Override
    protected void onStart() {
        super.onStart();
        Utilities.StartSessionFlurry(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Utilities.StopSessionFlurry(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        mImage = ((ImageView) findViewById(R.id.imageView1));
        mImageContainer = findViewById(R.id.image_container);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photoviewactivity_view_pager);
        this.viewPager = (HackyViewPager) findViewById(R.id.hacky_view_pager);
        setContentView(this.viewPager);

        int position = getIntent().getIntExtra(Utilities.EXTRA_POSITION, 0);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.d(Utilities.LOG_TAG, "No extras sent");
            Utilities.logError(Utilities.FLURRY_ERROR_VIEWPAGER_NO_EXTRAS_SENT, "No extras sent", Utilities.FLURRY_ERROR_CLASS_SEVERE);
            return;
        }

        this.photoList = (ArrayList<Photo>) extras.get(Utilities.EXTRA_CURRENT_PHOTOS);
        this.photo = this.photoList.get(position);

        this.pagerAdapter = new SamplePagerAdapter(this.photoList, this);
        this.viewPager.setAdapter(pagerAdapter);
        this.viewPager.setCurrentItem(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem fbMenuItem = menu.findItem(R.id.action_post_to_facebook);
        if (fromFb) {
            fbMenuItem.setVisible(false);
        } else {
            fbMenuItem.setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	int id = item.getItemId();
    	//convert case to if for adt14
    	if(id == R.id.action_aviary_edit ) {
         //   case R.id.action_aviary_edit:
                onAviaryEditAction(null);
                return true;
    	} else if (id ==R.id.action_show_canvas_pop ) {
         //   case R.id.action_show_canvas_pop:
                onCanvasPopAction(null);
                return true;
    	} else if (id == R.id.action_post_to_facebook ){

         //   case R.id.action_post_to_facebook:
                onPostToFacebookAction(null);
                return true;
    	} else { 

            //default:
                return super.onOptionsItemSelected(item);
    	}
        
    }

    public void onAviaryEditAction(View view) {
        Utilities.LogFlurry(Utilities.FLURRY_EVENT_AVIARY_CLICK);
        Intent newIntent = new Intent(this, FeatherActivity.class);
        Photo selectedPhoto = this.photoList.get(this.viewPager.getCurrentItem());
        newIntent.setData(selectedPhoto.imageUri);
        startActivityForResult(newIntent, AVIARY_EDIT_REQUEST_CODE);
    }

    public void onCanvasPopAction(View view) {
        Utilities.LogFlurry(Utilities.FLURRY_EVENT_CANVAS_CLICK);
        Intent intent = new Intent(this, CanvasPopActivity.class);
        Photo selectedPhoto = this.photoList.get(this.viewPager.getCurrentItem());
        intent.putExtra(Utilities.EXTRA_IMAGEURI, selectedPhoto);
        startActivity(intent);
    }

    public void onPostToFacebookAction(View view) {
        Utilities.LogFlurry(Utilities.FLURRY_EVENT_POSTPHOTOS_CLICK);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(getResources().getString(R.string.post_to_facebook_title));
        alert.setMessage(getResources().getString(R.string.post_to_facebook_caption));

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Editable value = input.getText();
                performPublish(false, value.toString());
            }
        });

        alert.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    public class SamplePagerAdapter extends PagerAdapter {

        public SamplePagerAdapter(ArrayList<Photo> photos, Context context) {
            this.photos = photos;
            this.context = context;
        }

        private ArrayList<Photo> photos;
        private Context context;

        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            final Photo current = this.photos.get(position);

            if (current.server == null || current.server.isEmpty()) {
                // for local photos
                photoView.setImageURI(current.imageUri);
            } else {
                // for remote photos
                final ImagePresenter<Photo> imagePresenter;

                //this is an example of how one might use ImagePresenter directly, there is otherwise no particular
                //reason why ImagePresenter is used here and not in FlickrPhotoList.
                final Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                imagePresenter = new ImagePresenter.Builder<Photo>()
                        .setModelLoader(new FlickrModelLoader(context, urlCache))
                        .setImageView(photoView)
                        .setImageLoader(new ImageManagerLoader(context))
                        .setTransformationLoader(new CenterCrop<Photo>())
                        .setImageReadyCallback(new ImagePresenter.ImageReadyCallback<Photo>() {
                            @Override
                            public void onImageReady(Photo photo, Target target, boolean fromCache) {
                                if (!fromCache) {
                                    target.startAnimation(fadeIn);
                                }
                            }
                        })
                        .build();

                photoView.setTag(imagePresenter);

                imagePresenter.setModel(current);
            }
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    private boolean hasPublishPermission() {
        Session session = Session.getActiveSession();
        return session != null && session.getPermissions().contains(FACEBOOK_PUBLISH_ACTION);
    }

    private interface GraphObjectWithId extends GraphObject {
        String getId();
    }

    private void showPublishResult(String message, GraphObject result, FacebookRequestError error) {
        String title = null;
        String alertMessage = null;
        if (error == null) {
            Utilities.LogFlurry(Utilities.FLURRY_EVENT_POSTPHOTOS_SUCCEEDED);
            title = getResources().getString(R.string.success);
            String id = result.cast(GraphObjectWithId.class).getId();
            Log.v(Utilities.LOG_TAG, "Successfully posted " + id);
            alertMessage = getResources().getString(R.string.success_message);
        } else {
            title = getResources().getString(R.string.error);
            alertMessage = error.getErrorMessage();
            Utilities.LogFlurry(Utilities.FLURRY_EVENT_POSTPHOTO_FAILED, Utilities.FLURRY_EVENT_POSTPHOTO_FAILED_MESSAGE_PARAM, alertMessage);
        }

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(alertMessage)
                .setPositiveButton(getResources().getString(R.string.ok), null)
                .show();
    }

    private ProgressBar progressBar;

    private void postPhoto(String message) {

        Bundle parameters = new Bundle(1);
        parameters.putParcelable("picture", imageBitmap);
        parameters.putString("message", message);
//        progressBar = (ProgressBar) findViewById(R.id.marker_progress);
//        progressBar.setVisibility(View.VISIBLE);
        Request request = new Request(Session.getActiveSession(), "me/photos", parameters, HttpMethod.POST, new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                showPublishResult("PostPhoto", response.getGraphObject(), response.getError());
            }
        });

        request.executeAsync();
    }

    private void performPublish(boolean allowNoSession, String message) {
        Session session = Session.getActiveSession();
        if ((session == null) || !session.isOpened()) {
            String errorMessage = getResources().getString(R.string.facebook_login_error);
            Utilities.logError(Utilities.FLURRY_ERROR_POST_PHOTO, errorMessage, Utilities.FLURRY_ERROR_CLASS_SEVERE);
            Toast.makeText(
                    getApplicationContext(),
                    errorMessage,
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (hasPublishPermission()) {
            // We can do the action right away.
            postPhoto(message);
        } else {
            // We need to get new permissions, then complete the action when we get called back.
            session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, FACEBOOK_PUBLISH_ACTION));
            if (hasPublishPermission()) {
                // We can do the action right away.
                postPhoto(message);
            } else {
                String errorMessage = getResources().getString(R.string.facebook_permission_error);
                Utilities.logError(Utilities.FLURRY_ERROR_POST_PHOTO, errorMessage, Utilities.FLURRY_ERROR_CLASS_SEVERE);
                Toast.makeText(
                        getApplicationContext(),
                        errorMessage,
                        Toast.LENGTH_LONG).show();
            }
        }

        return;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AVIARY_EDIT_REQUEST_CODE:
                    // output image path
                    Uri aviaryImageUri = data.getData();
                    Bundle extra = data.getExtras();
                    if (null != extra) {
                        // image has been changed by the user?
                        boolean changed = extra.getBoolean(Constants.EXTRA_OUT_BITMAP_CHANGED);
                        if (changed) {
                            Utilities.LogFlurry(Utilities.FLURRY_EVENT_AVIARY_EDITED);
                            this.photo = new Photo(getApplicationContext(), aviaryImageUri);

                            //TODO: update photo (show the new edited photo)
                        }
                    } else {
                        Utilities.LogFlurry(Utilities.FLURRY_EVENT_AVIARY_CANCELED);
                    }
                    break;
            }
        }
    }
}
