package com.wishnuu.photoweaver.flickr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockFragment;
import com.bumptech.glide.loader.image.ImageManagerLoader;
import com.bumptech.glide.loader.model.Cache;
import com.bumptech.glide.loader.transformation.CenterCrop;
import com.bumptech.glide.presenter.ImagePresenter;
import com.bumptech.glide.presenter.target.Target;
import com.wishnuu.photoweaverplus.R;
import com.wishnuu.photoweaver.Utilities;
import com.wishnuu.photoweaver.ViewPagerActivity;
import com.wishnuu.photoweaver.flickr.api.Photo;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FlickrPhotoGrid extends SherlockFragment implements PhotoViewer, AdapterView.OnItemClickListener {
    private static final String IMAGE_SIZE_KEY = "image_size";

    private PhotoAdapter adapter;
    private ArrayList<Photo> currentPhotos;
    private int photoSize;
    private final Cache<URL> urlCache = new Cache<URL>();

    public static FlickrPhotoGrid newInstance(int size) {
        FlickrPhotoGrid photoGrid = new FlickrPhotoGrid();
        Bundle args = new Bundle();
        args.putInt(IMAGE_SIZE_KEY, size);
        photoGrid.setArguments(args);
        return photoGrid;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        photoSize = args.getInt(IMAGE_SIZE_KEY);

        final View result = inflater.inflate(R.layout.flickr_photo_grid, container, false);
        GridView grid = (GridView) result.findViewById(R.id.images);
        grid.setColumnWidth(photoSize);
        adapter = new PhotoAdapter();
        grid.setAdapter(adapter);
        if (currentPhotos != null)
            adapter.setPhotos(currentPhotos);

        grid.setOnItemClickListener(this);
        return result;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view,
                            int position, long id)
    {
        Intent intent = new Intent(getActivity(), ViewPagerActivity.class);
        intent.putExtra(Utilities.EXTRA_POSITION, position);
        intent.putParcelableArrayListExtra(Utilities.EXTRA_CURRENT_PHOTOS, this.currentPhotos);
        startActivity(intent);
    }

    @Override
    public void onPhotosUpdated(ArrayList<Photo> photos) {
        currentPhotos = photos;
        if (adapter != null)
            adapter.setPhotos(currentPhotos);
    }

    private class PhotoAdapter extends BaseAdapter {

        private List<Photo> photos = new ArrayList<Photo>(0);
        private final LayoutInflater inflater;

        public PhotoAdapter() {
            this.inflater = LayoutInflater.from(getActivity());
        }

        public void setPhotos(List<Photo> photos) {
            this.photos = photos;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public Object getItem(int i) {
            return photos.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup container) {
            final Photo current = photos.get(position);
            final ImagePresenter<Photo> imagePresenter;
            if (view == null) {
                ImageView imageView = (ImageView) inflater.inflate(R.layout.flickr_photo_grid_item, container, false);
                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                params.width = photoSize;
                params.height = photoSize;

                final Context context = getActivity();

                //this is an example of how one might use ImagePresenter directly, there is otherwise no particular
                //reason why ImagePresenter is used here and not in FlickrPhotoList.
                final Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                imagePresenter = new ImagePresenter.Builder<Photo>()
                        .setModelLoader(new FlickrModelLoader(context, urlCache))
                        .setImageView(imageView)
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
                view = imageView;
                view.setTag(imagePresenter);
            } else {
                imagePresenter = (ImagePresenter<Photo>) view.getTag();
            }

            imagePresenter.setModel(current);
            return view;
        }
    }

}
