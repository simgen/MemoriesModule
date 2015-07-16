/*
 ===========================================================================
 Copyright (c) 2012 Three Pillar Global Inc. http://threepillarglobal.com

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sub-license, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 ===========================================================================
 */

package com.wishnuu.photoweaver.viewAdapters;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wishnuu.photoweaverplus.R;
import com.wishnuu.photoweaver.flickr.api.Photo;

public class PhotoAdapter extends ArrayAdapter<Photo> {

	// SocialAuth Components
	List<Photo> photoList;

	// Android Components
	LayoutInflater mInflater;
    int mTextViewResourceId;
    Context mContext;

	// Other Components
	ViewHolder viewHolder;
	ImageLoader imageLoader;

	public PhotoAdapter(Context context, int textViewResourceId, List<Photo> photoList) {
		super(context, textViewResourceId);
		this.photoList = photoList;
		this.mContext = context;
        this.mTextViewResourceId = textViewResourceId;
        this.mInflater = LayoutInflater.from(context);
        this.imageLoader = new ImageLoader(context);
	}

	@Override
	public int getCount() {
		return photoList.size();
	}

	@Override
	public View getView(int position, View row, ViewGroup parent) {

		final Photo bean = photoList.get(position);

		if (row == null) {
            row = mInflater.inflate(this.mTextViewResourceId, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.fullPhotoImageView = (ImageView) row.findViewById(R.id.fullPhoto);
			viewHolder.photoCommentsCountTextView = (TextView) row.findViewById(R.id.photoCommentsCount);
			viewHolder.photoLikesCountTextView = (TextView) row.findViewById(R.id.photolikesCount);
			
			row.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) row.getTag();
		}

		if (bean.title != null)
			Log.d("LifeView", "Photo Title = " + bean.title);
		
		imageLoader.DisplayImage(bean.getPartialUrl(), viewHolder.fullPhotoImageView);
		
		String likeCount = String.valueOf(bean.mLikesCount);
		String commCount = String.valueOf(bean.mCommentsCount);
		//we r keeping upper limit as 25 to keep page faster or else we have to make multiple round trips for fb api
		
		if(likeCount.equals("25")) { 
			viewHolder.photoLikesCountTextView.setText(likeCount + "+ Likes");
		} else {
			viewHolder.photoLikesCountTextView.setText(likeCount + " Likes");
		}
		if(commCount.equals("25")) {
			viewHolder.photoCommentsCountTextView.setText(commCount + "+ Comments");	
		} else { 
			viewHolder.photoCommentsCountTextView.setText(commCount + " Comments");
		}
	
		return row;
	}

	class ViewHolder {
		ImageView fullPhotoImageView;
		TextView photoLikesCountTextView;
        TextView photoCommentsCountTextView;
	}
}
