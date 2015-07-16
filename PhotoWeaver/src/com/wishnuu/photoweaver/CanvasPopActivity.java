package com.wishnuu.photoweaver;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.wishnuu.photoweaverplus.R;
import com.wishnuu.photoweaver.flickr.api.Photo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class CanvasPopActivity extends Activity {

    public static final String CANVASPOP_PUSH_IMAGE_URL = "https://store.canvaspop.com/api/push/image";
    public static final String CANVASPOP_PULL_IMAGE_URL = "https://store.canvaspop.com/api/pull?access_key=ab75bedb2073b32f68e781c4e7b38642&image_url=";
    public static final String CANVASPOP_API_KEY = "ab75bedb2073b32f68e781c4e7b38642";
    public static final String CANVASPOP_LOADER_URL = "https://store.canvaspop.com/loader/";
    public static final String JSOBJECT = "jsobject";
    public static final String JSON_IMAGE_TOKEN = "image_token";
    private Photo photo;

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

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.canvaspopview);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.d(Utilities.LOG_TAG, "No extras sent");
            return;
        }
        this.photo = extras.getParcelable(Utilities.EXTRA_IMAGEURI);
        new PhotoUploaderTask().execute(this.photo);
    }

    private class PhotoUploaderTask extends AsyncTask<Photo, Void, Void> {
        private DefaultHttpClient mHttpClient;
        public String imageToken;

        @Override
        protected void onPreExecute() {
            HttpParams params = new BasicHttpParams();
            params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            mHttpClient = new DefaultHttpClient(params);
            super.onPreExecute();
        }

        private Boolean isHttpUrl = false;
        private Photo photo;

        @Override
        protected Void doInBackground(Photo... params) {
            this.photo = params[0];
            if (this.photo.imagePath.startsWith("http:") || this.photo.imagePath.startsWith("https:")) {
                isHttpUrl = true;
            }
            else {
                try {
                    HttpPost httppost = new HttpPost(CANVASPOP_PUSH_IMAGE_URL);

                    httppost.setHeader("CP-Authorization", "basic");
                    httppost.setHeader("CP-ApiKey", CANVASPOP_API_KEY);

                    File imageFile = new File(this.photo.imagePath);
                    MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
                    multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                    multipartEntityBuilder.addBinaryBody("image", imageFile, ContentType.create("image/jpeg"), this.photo.imagePath);
                    httppost.setEntity(multipartEntityBuilder.build());
                    ResponseHandler responseHandler = new PhotoUploadResponseHandler(this);
                    mHttpClient.execute(httppost, responseHandler);

                } catch (Exception e) {
                    Log.e(Utilities.LOG_TAG, e.getLocalizedMessage(), e);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            String stringUrl;
            if (!isHttpUrl) {
                if (imageToken == null) {
                    Log.d(Utilities.LOG_TAG, "Image Token is null");
                    return;
                }

                stringUrl = CANVASPOP_LOADER_URL + imageToken;
            } else {
                stringUrl = CANVASPOP_PULL_IMAGE_URL + this.photo.imagePath;
            }
            canvasPopWebViewRedirectUrl(stringUrl);
            super.onPostExecute(result);
        }

        private void canvasPopWebViewRedirectUrl(String url) {
            WebView webView = (WebView) findViewById(R.id.webView1);
            if (webView != null) {
                webView.setVisibility(View.VISIBLE);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.addJavascriptInterface(new JSObject(), JSOBJECT); // This is the javascript object that would be exposed to the page.

                // Make sure we register an event to listen to canvas pop events. windows.jsobject in the javascript corresponds to the javascript interface we added above.
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        view.loadUrl("javascript:window.addEventListener( 'message', function (event) {window.jsobject.CanvasPopEvent(event.data);})");
                    }
                });

                webView.loadUrl(url);
            }
        }
    }

    public class JSObject {
        @JavascriptInterface
        public void CanvasPopEvent(String eventData) {
            Log.v(Utilities.LOG_TAG, eventData);
        }
    }

    private class PhotoUploadResponseHandler implements
            ResponseHandler<Object>, HttpContext {

        private PhotoUploaderTask mPhotoUploaderTask;

        public PhotoUploadResponseHandler(PhotoUploaderTask photoUploaderTask) {
            // TODO Auto-generated constructor stub
            mPhotoUploaderTask = photoUploaderTask;
        }

        @Override
        public Object handleResponse(HttpResponse response)
                throws ClientProtocolException, IOException {
            HttpEntity r_entity = response.getEntity();
            String responseString;
            try {
                responseString = EntityUtils.toString(r_entity);
            } catch (ParseException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return null;
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return null;
            }
            Log.d(Utilities.LOG_TAG, responseString);
            try {
                JSONObject jsonObject = new JSONObject(responseString);
                mPhotoUploaderTask.imageToken = jsonObject.getString(JSON_IMAGE_TOKEN);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

            return null;
        }

        @Override
        public Object getAttribute(String id) {
            return null;
        }

        @Override
        public Object removeAttribute(String id) {
            return null;
        }

        @Override
        public void setAttribute(String id, Object obj) {
        }
    }
}
