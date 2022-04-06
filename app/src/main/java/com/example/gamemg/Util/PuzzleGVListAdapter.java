package com.example.gamemg.Util;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.gamemg.ItemBean;
import com.example.gamemg.PuzzleActivity;

import java.util.List;

public class PuzzleGVListAdapter extends BaseAdapter {
    Context context;
    List<ItemBean> mPiclist;

    public PuzzleGVListAdapter(Context context, List<ItemBean> mPiclist) {
        this.context = context;
        this.mPiclist = mPiclist;
    }

    @Override
    public int getCount() {
        Log.i("weiSir", "getCount: " + mPiclist.size());
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
        DisplayMetrics screenSize = ScreenUtil.getScreenSize(context);
        int density = (int) ScreenUtil.getDeviceDensity(context);
        if (view == null) {
            iv_pic_item = new ImageView(context);
//                设置布局
            iv_pic_item.setLayoutParams(new GridView.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    (screenSize.heightPixels - (300 * density)) / PuzzleActivity.TYPE
            ));
            iv_pic_item.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            iv_pic_item = (ImageView) view;
        }
        iv_pic_item.setBackgroundColor(Color.BLACK);

        iv_pic_item.setImageBitmap(mPiclist.get(i).getmBitmap());
        if (PuzzleActivity.isSucessce){
            if (i == mPiclist.size() -1){
                iv_pic_item.setImageBitmap(PuzzleActivity.mLastBitmap);
            }
        }
        return iv_pic_item;
    }
}
