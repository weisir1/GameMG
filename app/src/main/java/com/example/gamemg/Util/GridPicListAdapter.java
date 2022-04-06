package com.example.gamemg.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.gamemg.MainActivity;

import java.util.List;

public class GridPicListAdapter extends BaseAdapter {
    Context context;
    List<Bitmap> mPiclist;

    public GridPicListAdapter(Context context, List<Bitmap> mPiclist) {
        this.context = context;
        this.mPiclist = mPiclist;
    }

    @Override
    public int getCount() {
        return mPiclist.size();
    }

    @Override
    public Object getItem(int i) {
        return mPiclist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView iv_pic_item = null;
        int density = (int) ScreenUtil.getDeviceDensity(context);
        if (view == null) {
            iv_pic_item = new ImageView(context);
//                设置布局
            iv_pic_item.setLayoutParams(new GridView.LayoutParams(
                    80 * density,
                    100 * density
            ));
            iv_pic_item.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            iv_pic_item = (ImageView) view;
        }
        iv_pic_item.setBackgroundColor(Color.BLACK);
        iv_pic_item.setImageBitmap(mPiclist.get(i));
        return iv_pic_item;
    }
}
