package com.wishnuu.photoweaver.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.wishnuu.photoweaverplus.R;
import com.wishnuu.photoweaver.entities.Album;
import com.wishnuu.photoweaver.entities.Provider;
import com.wishnuu.photoweaver.events.DialogListener;
import com.wishnuu.photoweaver.events.ListenerError;
import com.wishnuu.photoweaver.providers.FacebookDataProvider;
import com.wishnuu.photoweaver.viewAdapters.*;

public class AlbumActivity extends Activity {

    private static final String CLASS_NAME = "AlbumActivity";

    Provider provider;
    GridView gridview;
    ArrayList<Album> albumList;
    ProgressDialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.albums_view);
        this.provider = getIntent().getParcelableExtra("provider");

        TextView titleTextView = (TextView) findViewById(R.id.albumTitle);
        titleTextView.setText(this.provider.getName() + " Albums");

        this.albumList = this.provider.getAlbumList();
        final AlbumAdapter adapter = new AlbumAdapter(this, R.layout.albums_view_item, this.albumList);
        gridview = (GridView) findViewById(R.id.gridView);
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // TODO: need access to list of providers (make them global of some sort)
                Album selectedAlbum = albumList.get(position);
                FacebookDataProvider d = new FacebookDataProvider(AlbumActivity.this, new ResponseListener());
                d.loadPhotos(selectedAlbum);
            }
        });
    }

    // To receive the response after authentication
    private final class ResponseListener implements DialogListener {

        @Override
        public void onStart(Bundle values) {
            Log.d(CLASS_NAME, "onStart");
            mDialog = new ProgressDialog(AlbumActivity.this);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setMessage("Loading...");
        }

        @Override
        public void onComplete(Bundle values) {

            Log.d(CLASS_NAME, "onComplete");
            mDialog.dismiss();

            Album album = values.getParcelable("album");
            displayPhotos(album);
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

    private void displayPhotos(Album album) {

        Intent intent = new Intent(AlbumActivity.this, PhotoActivity.class);
        intent.putExtra("album", (Parcelable) album);
        startActivity(intent);
    }

}