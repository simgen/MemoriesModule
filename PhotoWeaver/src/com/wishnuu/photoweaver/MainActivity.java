package com.wishnuu.photoweaver;

import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;












//import android.R;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Files.FileColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.Toast;










//import com.aviary.android.feather.library.providers.FeatherContentProvider.SessionsDbColumns.Session;
//import android.service.textservice.SpellCheckerService.Session;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.wishnuu.photoweaverplus.R;
import com.wishnuu.photoweaver.activities.AlbumActivity;
import com.wishnuu.photoweaver.entities.Provider;
import com.wishnuu.photoweaver.events.DialogListener;
import com.wishnuu.photoweaver.events.ListenerError;
import com.wishnuu.photoweaver.flickr.FlickrBrowseActivity;
import com.wishnuu.photoweaver.flickr.api.Photo;
import com.wishnuu.photoweaver.providers.DataProvider;
import com.wishnuu.photoweaver.providers.FacebookDataProvider;
import com.wishnuu.photoweaver.providers.FlickrDataProvider;
import com.wishnuu.photoweaver.providers.LocalDataProvider;
import com.wishnuu.photoweaver.viewAdapters.ProviderGridAdapter;

public class MainActivity extends Activity {

    private static final String CLASS_NAME = "MainActivity";

    // Android Components
    GridView gridview;
    ProgressDialog mDialog;

    // action bar
    private ActionBar actionBar;
    // Refresh menu item
    private MenuItem refreshMenuItem;

    // Variables
    public static int pos;
    public static Bitmap bitmap;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int SELECT_GALLERY_ACTIVITY_REQUEST_CODE = 200;
    private Photo photo;
    private UiLifecycleHelper uiHelper;

    private final ArrayList<DataProvider> dataProviders = new ArrayList<DataProvider>();
    private final ArrayList<Provider> providers = new ArrayList<Provider>();

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        /*
        // code to retrieve hash key from the store.
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.wishnuu.photosplus", PackageManager.GET_SIGNATURES);
            Utilities.logHashKeyFromStore(info);
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        }*/

        // Adapter initialization
        actionBar = getActionBar();
        this.fetchSelectedDataProviders();
        this.fetchProviders(this.dataProviders);

        final ProviderGridAdapter adapter = new ProviderGridAdapter(this, R.layout.provider_grid_item, this.providers);
        gridview = (GridView) findViewById(R.id.gridView);
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                DataProvider provider = providers.get(position).getDataProvider();
                
                if (provider.isLoggedIn()) {
                    provider.loadAlbums();
                    
                } else {
                    provider.doLogin();
                }
            }
        });
    }

    private void fetchSelectedDataProviders() {
        ResponseListener dialogListener = new ResponseListener();
        Log.d("fetchSelectedDataProviders", "fetching providers begin");

        this.dataProviders.add(new FacebookDataProvider(this, dialogListener));
        this.dataProviders.add(new FlickrDataProvider(this, dialogListener));
        this.dataProviders.add(new LocalDataProvider(this, dialogListener));
        
        Log.d("fetchSelectedDataProviders", "fetching providers complete");

    }

    private void fetchProviders(ArrayList<DataProvider> providers) {
        for (int i = 0; i < providers.size(); i++) {
            this.providers.add(providers.get(i).getProvider());
        }
        

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	int id = item.getItemId();
    	
        if( id == R.id.action_camera ){
         //   case R.id.action_camera:
                onCameraAction(null);
                return true;
        } else if (id == R.id.action_refresh) {

         //   case R.id.action_refresh:
                // refresh
                refreshMenuItem = item;
                // load the data from server
                new RefreshData().execute();
                return true;
        } else if (id == R.id.action_help ){

         //   case R.id.action_help:
                onHelpAction(item.getActionView());
                return true;
        } else if (id == R.id.action_about ){ 
         //   case R.id.action_about:
                // TODO
                return true;
        } else if (id == R.id.action_settings){

         //   case R.id.action_settings:
                // TODO
                return true;
        } else {
            //default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Create a File for saving an image or video
     */
    private Photo getOutputMediaFile(int type) {
        String state = Environment.getExternalStorageState();

        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            // We cannot read or write the media
            Log.d(Utilities.LOG_TAG, "External storage media state invalid, state: " + state);
            return null;
        }

        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Utilities.PHOTOWEAVER_APPNAME);
        if (mediaStorageDir == null) {
            Log.d(Utilities.LOG_TAG, "Could not create media storage directory");
            return null;
        }

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(Utilities.LOG_TAG, "failed to create directory");
                return null;
            }
            Log.v(Utilities.LOG_TAG, "Successfully created media storage directory :" + mediaStorageDir.getPath());
        }

        // Create a media file name
        String fileName = getNewFileName(type, mediaStorageDir);
        if (fileName == null) {
            Log.d(Utilities.LOG_TAG, "could not get a file name");
            return null;
        }

        File mediaFile = new File(fileName);
        if (mediaFile == null) {
            Log.d(Utilities.LOG_TAG, "could not create a new file :" + fileName);
            return null;
        }

        return new Photo(getApplicationContext(), Uri.fromFile(mediaFile));
    }

    private static String getNewFileName(int type, File mediaStorageDir) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        if (type == FileColumns.MEDIA_TYPE_IMAGE) {
            return mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg";
        } else if (type == FileColumns.MEDIA_TYPE_VIDEO) {
            return mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4";
        }

        Log.d(Utilities.LOG_TAG, "Unknown type");
        return null;
    }

    private void onHelpAction(View view) {
        View popupView = this.getLayoutInflater().inflate(R.layout.popup_help, null);

        final PopupWindow popupWindow = new PopupWindow(
                popupView);

        popupWindow.showAsDropDown(view, 50, -30);
    }

    private void onCameraAction(View view) {
        Utilities.LogFlurry(Utilities.FLURRY_EVENT_CAMERA_CLICK);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        this.photo = getOutputMediaFile(FileColumns.MEDIA_TYPE_IMAGE); // create a file to save the image
        if (this.photo == null) {
            Toast.makeText(
                    getApplicationContext(),
                    getResources().getString(R.string.storage_access_problem),
                    Toast.LENGTH_LONG).show();
            return;
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, this.photo.imageUri); // set the image file name

        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private void addPhotoToMediaStore(Uri imageUri) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void MoveToPictureView(Photo photo) {
        ArrayList<Photo> photoList = new ArrayList<Photo>();
        photoList.add(photo);

        Intent intent = new Intent(this, ViewPagerActivity.class);
        intent.putExtra(Utilities.EXTRA_POSITION, 0);
        intent.putParcelableArrayListExtra(Utilities.EXTRA_CURRENT_PHOTOS, photoList);
        startActivity(intent);
    }

    // To receive the response after authentication
    private final class ResponseListener implements DialogListener {

        @Override
        public void onStart(Bundle values) {
            Log.d(CLASS_NAME, "onStart");
            mDialog = new ProgressDialog(MainActivity.this);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setMessage("Loading...");
        }

        @Override
        public void onComplete(Bundle values) {

            Log.d(CLASS_NAME, "onComplete");
            mDialog.dismiss();

            Provider provider = values.getParcelable("provider");
            displayAlbums(provider);
        }

        @Override
        public void onError(ListenerError error) {
            Log.d(CLASS_NAME, "ListenerError");
            error.printStackTrace();
        }

        @Override
        public void onCancel() {
            Log.d(CLASS_NAME, "Cancelled");
        }

        @Override
        public void onBack() {
            Log.d(CLASS_NAME, "Dialog Closed by pressing Back Key");
        }
    }

    private void displayAlbums(Provider provider) {

        String providerName = provider.getName();
        if (providerName == this.getResources().getString(R.string.local_provider_name)) {
            // Local provider
            Log.v(Utilities.LOG_TAG, "GalleryOnClick");
            Utilities.LogFlurry(Utilities.FLURRY_EVENT_GALLERY_CLICK);
            Intent intent = new Intent(Intent.ACTION_PICK, EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, SELECT_GALLERY_ACTIVITY_REQUEST_CODE);
        } else if (providerName == this.getResources().getString(R.string.flickr_provider_name)) {
            Utilities.LogFlurry(Utilities.FLURRY_EVENT_FLICKR_CLICK);
            Intent intent = new Intent(this, FlickrBrowseActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
            intent.putExtra("provider", provider);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Utilities.LogFlurry(Utilities.FLURRY_EVENT_CAMERA_IMAGE_SELECTED);
                    MoveToPictureView(this.photo);
                    addPhotoToMediaStore(this.photo.imageUri);
                } else {
                    Utilities.LogFlurry(Utilities.FLURRY_EVENT_CAMERA_IMAGE_NOT_SELECTED, Utilities.FLURRY_EVENT_CAMERA_PARAMETER, String.valueOf(resultCode));
                    super.onActivityResult(requestCode, resultCode, data);
                    Log.d(Utilities.LOG_TAG, "Capture image activity failed :" + String.valueOf(resultCode));
                }
                return;
            case SELECT_GALLERY_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Utilities.LogFlurry(Utilities.FLURRY_EVENT_GALLERY_IMAGE_SELECTED);
                    Photo photo = new Photo(getApplicationContext(), data.getData());
                    MoveToPictureView(photo);
                    return;
                } else {
                    Utilities.LogFlurry(Utilities.FLURRY_EVENT_GALLERY_IMAGE_NOT_SELECTED, Utilities.FLURRY_EVENT_CAMERA_PARAMETER, String.valueOf(resultCode));
                    super.onActivityResult(requestCode, resultCode, data);
                    Log.d(Utilities.LOG_TAG, "Capture image activity failed :" + String.valueOf(resultCode));
                }
                return;
            default:
                Log.v(Utilities.LOG_TAG, "Received activity result for requestCode:" + String.valueOf(requestCode));
                uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
        @Override
        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
            Log.d(Utilities.LOG_TAG, String.format("ListenerError: %s", error.toString()));
            Utilities.logError(Utilities.FLURRY_ERROR_FACEBOOK_LOGIN, error.toString(), Utilities.FLURRY_ERROR_CLASS_SEVERE);
        }

        @Override
        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
            Utilities.LogFlurry(Utilities.FLURRY_EVENT_FACEBOOK_LOGIN);
            Log.d(Utilities.LOG_TAG, "Success!");
        }
    };

    public boolean isFacebookAvailable() {

        Intent intent = new Intent(Intent.ACTION_SEND);
    intent.putExtra(Intent.EXTRA_TEXT, "Test; please ignore");
    intent.setType("text/plain");

        final PackageManager pm = this.getApplicationContext().getPackageManager();
        for(ResolveInfo resolveInfo: pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)){
            ActivityInfo activity = resolveInfo.activityInfo;
            // Log.i("actividad ->", activity.name);
            if (activity.name.contains("com.facebook.katana")) {
                return true;
            }
        }
        return false;
    }
    private void addFacebookSession(Intent intent) {
        Session session = Session.getActiveSession();
        if ((session != null && session.isOpened())) {
            intent.putExtra(Utilities.EXTRA_FB_SESSION, session);
        }
    }

    private void onSessionStateChange(Session session, SessionState state,
                                      Exception exception) {
        if (state.isOpened()) {
            Log.d(Utilities.LOG_TAG, "Logged in...");
        } else if (state.isClosed()) {
            Log.d(Utilities.LOG_TAG, "Logged out...");
        } else {
            Log.d(Utilities.LOG_TAG, "Unknown state: " + state);
        }
        if (exception != null) {
            Utilities.logError(Utilities.FLURRY_ERROR_FACEBOOK_SESSION, exception.toString(), Utilities.FLURRY_ERROR_CLASS_SEVERE);
        }
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        uiHelper.onResume();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
        if (this.photo != null) {
            outState.putParcelable("photo", this.photo);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("photo")) {
            this.photo = savedInstanceState.getParcelable("photo");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    /**
     * Async task to load the data from server
     */
    private class RefreshData extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            // set the progress bar view
            refreshMenuItem.setActionView(R.layout.action_progressbar);
            refreshMenuItem.expandActionView();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Thread.sleep(1000); // TODO
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            refreshMenuItem.collapseActionView();
            // remove the progress bar view
            refreshMenuItem.setActionView(null);
        }
    }
}
