package com.wishnuu.photoweaver.flickr;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.wishnuu.photoweaverplus.R;
import com.wishnuu.photoweaver.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FlickrBrowseActivity extends Activity implements AdapterView.OnItemClickListener {
    private GridView gridView;
    private GridViewAdapter customGridAdapter;

    // action bar
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flickr_browse);

        actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        gridView = (GridView) findViewById(R.id.categories);
        customGridAdapter = new GridViewAdapter(this, R.layout.flickr_category, getCategories());
        gridView.setAdapter(customGridAdapter);
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view,
                            int position, long id) {
        Intent intent = new Intent(this, FlickrSearchActivity.class);
        intent.putExtra(Utilities.EXTRA_SEARCH_STRING, customGridAdapter.data.get(position).text);
        startActivity(intent);
    }

    private ArrayList<Category> getCategories() {

        ArrayList<Category> results = new ArrayList<Category>();
        try {
            InputStream is = getResources().openRawResource(R.raw.categories);
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }

            String jsonString = writer.toString();

            JSONObject categoriesJson = new JSONObject(jsonString);
            JSONArray categories = categoriesJson.getJSONObject("categories").getJSONArray("category");
            results = new ArrayList<Category>(categories.length());
            for (int i = 0; i < categories.length(); i++) {
                results.add(new Category(categories.getJSONObject(i)));
            }
        } catch (Exception je) {
            Log.d(Utilities.LOG_TAG, "error reading categories json from resources: " + je.getStackTrace());
        }

        return results;
//
//        final ArrayList<ImageItem> imageItems = new ArrayList<ImageItem>();
//        // retrieve String drawable array
//        TypedArray imgs = getResources().obtainTypedArray(R.array.flickr_category_names);
//        for (int i = 0; i < imgs.length(); i++) {
//            Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),
//                    imgs.getResourceId(i, -1));
//            imageItems.add(new ImageItem(bitmap, "Image#" + i));
//        }
//
//        return imageItems;
    }

    public class Category {
        public String id;
        public String name;
        public String text;
        public String image;

        public Category() {
        }

        public Category(JSONObject jsonCategory) throws JSONException {
            this.id = jsonCategory.getString("id");
            this.name = jsonCategory.getString("name");
            this.text = jsonCategory.getString("text");
            this.image = jsonCategory.getString("image");
        }
    }

    public class GridViewAdapter extends ArrayAdapter<Category> {
        private Context context;
        private int layoutResourceId;
        private ArrayList<Category> data = new ArrayList<Category>();

        public GridViewAdapter(Context context, int layoutResourceId,
                               ArrayList<Category> data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder = null;

            if (row == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
                holder = new ViewHolder();
                holder.imageTitle = (TextView) row.findViewById(R.id.text);
                holder.image = (ImageView) row.findViewById(R.id.image);
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }

            Category category = data.get(position);

            int resourceId = getResources().getIdentifier(category.image, "drawable", getPackageName());
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
            holder.imageTitle.setText(category.name);
            holder.image.setImageBitmap(bitmap);
            return row;
        }


        public class ViewHolder {
            TextView imageTitle;
            ImageView image;
        }
    }

    public class ImageItem {
        private Bitmap image;
        private String title;

        public ImageItem(Bitmap image, String title) {
            super();
            this.image = image;
            this.title = title;
        }

        public Bitmap getImage() {
            return image;
        }

        public void setImage(Bitmap image) {
            this.image = image;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }


}

