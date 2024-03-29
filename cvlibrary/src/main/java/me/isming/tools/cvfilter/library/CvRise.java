package me.isming.tools.cvfilter.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.lofter.android.widget.filters.LibCvFilter;

/**
 * Created by sam on 14/11/28.
 */
public class CvRise implements ICVFilter{

    Context mContext;

    public CvRise(Context context) {
        mContext = context;
    }


    @Override
    public ImageData convert(ImageData source) {
        int width = source.getWidth();
        int height = source.getHeight();
        Bitmap localBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.filter_bb1024);
        if ((localBitmap.getWidth() != width) || (localBitmap.getHeight() != height))
            localBitmap = Bitmap.createScaledBitmap(localBitmap, width, height, false);
        int[] arrayOfInt1 = new int[width * height];
        localBitmap.getPixels(arrayOfInt1, 0, width, 0, 0, width, height);
        localBitmap.recycle();
        System.gc();
        source.readByte();
        int[] arrayOfInt2 = source.getDataByte();
        LibCvFilter.CvRise(arrayOfInt2, arrayOfInt1, width, height, source.a);
        source.setDataBytes(arrayOfInt2);
        System.gc();
        return source;
    }

    @Override
    public String getName() {
        return "Rise";
    }

    @Override
    public int getResId() {
        return R.drawable.filter_poxiao;
    }
}
