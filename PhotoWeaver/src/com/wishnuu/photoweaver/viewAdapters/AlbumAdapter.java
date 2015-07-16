package com.wishnuu.photoweaver.viewAdapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wishnuu.photoweaverplus.R;
import com.wishnuu.photoweaver.entities.*;

public class AlbumAdapter extends ArrayAdapter<Album> {

	// SocialAuth Components
	List<Album> albums;

	// Android Components
	Context mContext;
	LayoutInflater mInflater;
    int mTextViewResourceId;

    // Other Components
	ViewHolder viewHolder;
	ImageLoader imageLoader;

	public AlbumAdapter(Context context, int textViewResourceId, List<Album> albums) {
		super(context, textViewResourceId, albums);
		this.albums = albums;
		this.mContext = context;
        this.mTextViewResourceId = textViewResourceId;
        this.mInflater = LayoutInflater.from(context);
		imageLoader = new ImageLoader(context);
	}

	@Override
	public int getCount() {
		int i = albums.size();
		return albums.size();
	}

	@Override
	public View getView(int position, View row, ViewGroup parent) {

		final Album album = albums.get(position);

		if (row == null) {
			row = mInflater.inflate(this.mTextViewResourceId, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.coverImageView = (ImageView) row.findViewById(R.id.coverAlbum);
			viewHolder.albumName = (TextView) row.findViewById(R.id.albumName);

			viewHolder.likesCountTextView = (TextView) row.findViewById(R.id.likesCount);
			System.out.println("album name : " + R.id.albumName );
			System.out.println("album like : " + R.id.likesCount );

			System.out.println("album comments count  : " + R.id.commentsCount );
            viewHolder.commentsCountTextView = (TextView) row.findViewById(R.id.commentsCount);
			row.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) row.getTag();
		}

		imageLoader.DisplayImage(album.getCoverImageUrl(), viewHolder.coverImageView);
		viewHolder.albumName.setText(album.getTitle());
		String likeCount = String.valueOf(album.getLikesCount());
		String commCount = String.valueOf(album.getCommentsCount());
		//we r keeping upper limit as 25 to keep page faster or else we have to make multiple round trips for fb api

		if(likeCount.equals("25")) { 
			viewHolder.likesCountTextView.setText(likeCount + "+ Likes");
		} else {
			viewHolder.likesCountTextView.setText(likeCount + " Likes");
		}
		if(commCount.equals("25")) {
			viewHolder.commentsCountTextView.setText(commCount + "+ Comments");	
		} else { 
			viewHolder.commentsCountTextView.setText(commCount + " Comments");
		}
	

        // TODO two images are displayed because of setting image background for the text view.
        //viewHolder.albumName.setBackground(viewHolder.coverImageView.getDrawable());

		return row;
	}

	class ViewHolder {
		ImageView coverImageView;
		TextView albumName;
		TextView likesCountTextView;
        TextView commentsCountTextView;
	}
}
