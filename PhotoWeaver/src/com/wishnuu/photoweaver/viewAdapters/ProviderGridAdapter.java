package com.wishnuu.photoweaver.viewAdapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wishnuu.photoweaverplus.R;
import com.wishnuu.photoweaver.entities.Provider;

import java.util.ArrayList;

public class ProviderGridAdapter extends ArrayAdapter<Provider> {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final ArrayList<Provider> mProviderList;

    private final int mTextViewResourceId;

    ViewHolder viewHolder;

    // Android Components
    private Bitmap mIcon;

    public ProviderGridAdapter(Context context, int textViewResourceId, ArrayList<Provider> providerList) {
        super(context, textViewResourceId, providerList);
        this.mContext = context;
        this.mTextViewResourceId = textViewResourceId;
        this.mProviderList = providerList;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(this.mTextViewResourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.logoImageView = (ImageView) convertView.findViewById(R.id.providerLogo);
            viewHolder.logoTextImageView = (ImageView) convertView.findViewById(R.id.providerLogoText);
           /* viewHolder.imageView1 = (ImageView) convertView.findViewById(R.id.provider_image1);
            viewHolder.imageView2 = (ImageView) convertView.findViewById(R.id.provider_image2);
            viewHolder.imageView3 = (ImageView) convertView.findViewById(R.id.provider_image3); */

            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.logoImageView.setImageResource(this.mProviderList.get(position).getLogoUrl());
        viewHolder.logoTextImageView.setImageResource(this.mProviderList.get(position).getLogoTextUrl());
        /*viewHolder.imageView1.setImageResource(R.drawable.image_home);
        viewHolder.imageView2.setImageResource(R.drawable.image_beach);
        viewHolder.imageView3.setImageResource(R.drawable.image_animals); */

        return convertView;
    }

    class ViewHolder {
        ImageView logoImageView;
        ImageView logoTextImageView;
        ImageView imageView1;
        ImageView imageView2;
        ImageView imageView3;
    }
} // End of ProviderGridAdapter
