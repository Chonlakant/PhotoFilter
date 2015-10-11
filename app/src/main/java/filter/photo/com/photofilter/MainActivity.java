package filter.photo.com.photofilter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import me.isming.tools.cvfilter.library.CvHDR;
import me.isming.tools.cvfilter.library.FilterFactory;
import me.isming.tools.cvfilter.library.ICVFilter;
import me.isming.tools.cvfilter.library.ImageData;

public class MainActivity extends Activity {

    public static final String TAG = "MainActivity";
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLARY = 2;
    Bitmap bitmapCAMERA;
    Bitmap bitmapGALLAR;
    private HorizontalListView mListView;
    private FilterPreviewAdapter mAdapter;
    private int rotate;
    private Bitmap mOriginBitmap, mResultBitmap;
    private int mCurrentFilterPosition;
    private ImageView mRotateView, mHDRView;
    private ICVFilter mHDRFilter;

    ImageView mContentView;
    private ImageData mImageData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContentView = (ImageView) findViewById(R.id.imageView);

        mListView = (HorizontalListView) findViewById(R.id.listview);
        mAdapter = new FilterPreviewAdapter(this, FilterFactory.createFilters(this));
        mListView.setAdapter(mAdapter);

        mRotateView = (ImageView) findViewById(R.id.rotate);
        mRotateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotate += 90;
                mImageData.rotate(rotate);
                mImageData.createResult();
                mResultBitmap = mImageData.getResult();
                mContentView.setImageBitmap(mResultBitmap);
            }
        });

        mHDRView = (ImageView) findViewById(R.id.hdr);
        mHDRView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHDRFilter == null) {
                    mHDRFilter = new CvHDR();
                }
                //todo

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item_camera:
                Toast.makeText(this, "รูปภาพ Clicked", Toast.LENGTH_SHORT).show();
                takePicture();
                return true;
            case R.id.item_gallery:
                Toast.makeText(this, "กล้อง Clicked", Toast.LENGTH_SHORT).show();
                openGallery();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openGallery() {
        startActivityForResult(new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), PICK_FROM_GALLARY);

    }

    private void takePicture() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case PICK_FROM_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    bitmapCAMERA = (Bitmap) intent.getExtras().get("data");
                    mImageData = new ImageData(bitmapCAMERA);
                    mContentView.setImageBitmap(bitmapCAMERA);
                }
                break;

            case PICK_FROM_GALLARY:
                if (resultCode == Activity.RESULT_OK) {
                    Uri mImageCaptureUri = intent.getData();
                    try {
                        bitmapGALLAR = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageCaptureUri);
                        mImageData = new ImageData(bitmapGALLAR);
                        mContentView.setImageBitmap(bitmapGALLAR);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }


    }

    public class FilterPreviewAdapter extends BaseAdapter {

        //private List<FilterContent> mFilters;
        private ICVFilter[] mFilters;
        private Context mContext;
        private int mSelectItem;

        public FilterPreviewAdapter(Context context, ICVFilter[] filters) {
            mContext = context;
            mFilters = filters;
        }

        @Override
        public int getCount() {
            return mFilters == null ? 0 : mFilters.length;
        }

        @Override
        public Object getItem(int position) {
            return mFilters == null ? null : mFilters[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.filter_preview, null);
                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.image);
                holder.titleView = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final ICVFilter filterContent = (ICVFilter) getItem(position);
            final int nowItem = position;
            holder.imageView.setImageResource(filterContent.getResId());
            holder.titleView.setText(filterContent.getName());
            if (mSelectItem == position) {
                holder.titleView.setTextColor(mContext.getResources().getColor(R.color.join_group_text_color));
            } else {
                holder.titleView.setTextColor(mContext.getResources().getColor(R.color.youdao_background_color));
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectItem = nowItem;
                    mCurrentFilterPosition = nowItem;
                    ImageData d = filterContent.convert(mImageData);
                    Toast.makeText(getBaseContext(), filterContent.getName(), Toast.LENGTH_LONG).show();
                    d.createResult();
                    mResultBitmap = d.getResult();
                    mContentView.setImageBitmap(mResultBitmap);
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }

        public void selectItem(int position) {
            final ICVFilter filterContent = (ICVFilter) getItem(position);
            mSelectItem = position;
            mCurrentFilterPosition = position;
            ImageData d = filterContent.convert(mImageData);
            d.createResult();
            mResultBitmap = d.getResult();
            mContentView.setImageBitmap(mResultBitmap);
            notifyDataSetChanged();
        }
    }

    class ViewHolder {
        TextView titleView;
        ImageView imageView;
    }
}
