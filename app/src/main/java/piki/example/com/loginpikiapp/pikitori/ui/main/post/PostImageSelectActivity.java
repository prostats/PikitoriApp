package piki.example.com.loginpikiapp.pikitori.ui.main.post;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.pikitori.core.ImageUtils;
import piki.example.com.loginpikiapp.pikitori.domain.ImageModel;


public class PostImageSelectActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "PostImageSelectActivity";
    private ListView listivew;
    private ImageSelectorAdapter imageSelectorAdapter;
    private ImageSelectoritemAdapter itemadapter;
    Map<String,ImageModel> selecteditem = new LinkedHashMap<String,ImageModel>();

    private boolean flag = false;
    private int selected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_write_image_select);

        getSupportActionBar().setTitle("이미지 선택");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listivew = (ListView) findViewById(R.id.imageList);
        selecteditem = new LinkedHashMap<String,ImageModel>();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Map<String, ArrayList<ImageModel>> dayUrilist = ImageUtils.getImagesFromExternalByDay(this);
        String[] keyset = dayUrilist.keySet().toArray(new String[dayUrilist.size()]);
        imageSelectorAdapter = new ImageSelectorAdapter(this, dayUrilist);
        listivew.setAdapter(imageSelectorAdapter);

        ComponentName callingApplication = getCallingActivity();
        switch (callingApplication.getShortClassName()){
            case ".pikitori.ui.main.pikiMainActivity":{
                Toast.makeText(getApplicationContext(),"Main 으로 부터",Toast.LENGTH_LONG).show();
                flag = false;
            }
            break;
            case ".pikitori.ui.main.profile.ProfileModifyActivity":{
                Toast.makeText(getApplicationContext(),"Profile 으로 부터",Toast.LENGTH_LONG).show();
                flag = true;
            }
            break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        ImageModel imageModel = (ImageModel) adapterView.getAdapter().getItem(position);
        if (imageModel.isSelected()) {
            imageModel.setSelected(false);
            selecteditem.remove(imageModel.getId());

            (view.findViewById(R.id.icon)).setVisibility(View.GONE);
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                ((ImageView) view.findViewById(R.id.Image)).setAlpha(255);
            } else {
                ((ImageView) view.findViewById(R.id.Image)).clearColorFilter();
            }
            selected--;

        } else {
            imageModel.setSelected(true);
            selecteditem.put(imageModel.getId(),imageModel);

            (view.findViewById(R.id.icon)).setVisibility(View.VISIBLE);
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                ((ImageView) view.findViewById(R.id.Image)).setAlpha(100);
            } else {
                ((ImageView) view.findViewById(R.id.Image)).setColorFilter(Color.argb(180, 53, 53, 53), PorterDuff.Mode.SRC_ATOP);
            }
            selected++;
        }

        if(flag){
            if(selecteditem.size() == 1){
                Intent i = new Intent();
                i.putExtra("uri",imageModel.getUri().toString());
                setResult(Activity.RESULT_OK,i);
                finish();
            }
        }
    }

    private class ImageSelectorAdapter extends BaseAdapter {

        private final TreeMap<String, ArrayList<ImageModel>> dayUrilist;
        Context context;
        LayoutInflater layoutInflater;
        String[] keyset;

        public ImageSelectorAdapter(Context context, Map<String, ArrayList<ImageModel>> dayUrilist) {
            this.context = context;
            this.dayUrilist = new TreeMap<String, ArrayList<ImageModel>>(dayUrilist);
            layoutInflater = LayoutInflater.from(context);
            keyset = dayUrilist.keySet().toArray(new String[dayUrilist.size()]);

        }

        @Override
        public int getCount() {

            return dayUrilist.size();
        }

        @Override
        public Object getItem(int position) {
            return dayUrilist.get(keyset[position]);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {

            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.select_grid_item, null, false);
                viewHolder = new ViewHolder();
                viewHolder.tv_day = (TextView) convertView.findViewById(R.id.tv_day);
                viewHolder.gridview = (GridView) convertView.findViewById(R.id.imageGrid);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tv_day.setText(keyset[position]);

            ArrayList<ImageModel> urilist = (ArrayList<ImageModel>) getItem(position);
            itemadapter = new ImageSelectoritemAdapter(context, urilist);
            viewHolder.gridview.setAdapter(itemadapter);
            viewHolder.gridview.setOnItemClickListener(PostImageSelectActivity.this);
            viewHolder.gridview.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 330 * (int) Math.ceil((urilist.size() / (double) 3))));
            Log.d(TAG, "height " + (int) Math.ceil((urilist.size() / (double) 3)));

            return convertView;
        }

        class ViewHolder {
            TextView tv_day;
            GridView gridview;
        }

    }

    private class ImageSelectoritemAdapter extends BaseAdapter {
        ArrayList<ImageModel> imageList;
        LayoutInflater layoutInflater;
        final int THUMBSIZE = 150;
        private DisplayImageOptions options;
        Paint paint = new Paint();


        public ImageSelectoritemAdapter(Context context, ArrayList<ImageModel> imageList) {
            this.imageList = imageList;
            layoutInflater = LayoutInflater.from(context);
            options = ImageUtils.universalImageConfiguration(context.getResources().getDimensionPixelSize(R.dimen.gallery_image_size), context.getResources().getDimensionPixelSize(R.dimen.gallery_image_size));
            paint.setColor(Color.BLACK);
            paint.setAlpha(70);
        }

        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public ImageModel getItem(int position) {
            return imageList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {

            ImageModel imageUri = getItem(position);
            ViewHolderItem viewHolderItem;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.select_grid_image_item, null, false);
                viewHolderItem = new ViewHolderItem();
                viewHolderItem.imageView = (ImageView) convertView.findViewById(R.id.Image);
                viewHolderItem.icon = (ImageView) convertView.findViewById(R.id.icon);
                convertView.setTag(viewHolderItem);

            } else {
                viewHolderItem = (ViewHolderItem) convertView.getTag();
                viewHolderItem.imageView.clearColorFilter();
                viewHolderItem.icon.setVisibility(View.GONE);

            }

            int sdk = android.os.Build.VERSION.SDK_INT;
            if (imageUri.isSelected()) {
                viewHolderItem.icon.setVisibility(View.VISIBLE);
                viewHolderItem.imageView.setColorFilter(Color.argb(180, 53, 53, 53), PorterDuff.Mode.SRC_ATOP);
            } else {
                viewHolderItem.icon.setVisibility(View.GONE);
                viewHolderItem.imageView.clearColorFilter();
            }

            ImageLoader.getInstance().displayImage("file://" + imageUri.getUri(), viewHolderItem.imageView, options);

            return convertView;
        }

        class ViewHolderItem {
            ImageView imageView;
            ImageView icon;
        }
    }

    public ArrayList<String> getAllSelectedImages() {
        ArrayList<String> selectedList = new ArrayList<String>();
        Iterator<String> iter = selecteditem.keySet().iterator();
        while(iter.hasNext()){
            String key = (String)iter.next();
            ImageModel value = selecteditem.get(key);
            Log.d(TAG, "selected: "+ value.getUri());
            selectedList.add(value.getUri());
        }
        return selectedList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            }
            break;
            case R.id.next: {
                Intent intent = new Intent(this,PostImageListActivity.class);
                ArrayList<String> selectedImage = getAllSelectedImages();
                intent.putExtra("selectedImage",selectedImage);
                startActivity(intent);
            }
            break;
        }
        return true;
    }

}
