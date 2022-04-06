package com.example.gamemg.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.PictureDrawable;
import android.util.DisplayMetrics;
import android.util.Log;

import com.example.gamemg.ItemBean;
import com.example.gamemg.PuzzleActivity;
import com.example.gamemg.R;

import java.util.ArrayList;
import java.util.List;

public class ImagesUtil {
    private static ItemBean itemBean;

    /*
     * 使用options优化图片 改变图片像素密度 提高性能
     * */
    public static Bitmap loadBitmap(int bitmapId, Context context) {
        DisplayMetrics screenSize = ScreenUtil.getScreenSize(context);
        int density = (int) ScreenUtil.getDeviceDensity(context);
        //screenSize.widthPixels:   The absolute width of the available display size in pixels.
        int newWidth = screenSize.widthPixels / 2;
//        int newHeight = screenSize.heightPixels / density  ;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = true;   //只获取尺寸
        BitmapFactory.decodeResource(context.getResources(), bitmapId, options);
        //options.outWidth: 图片密度   与之后的bitmap.getWidth获取的是像素 相差density
        int imgWidth = options.outWidth;
//        int imgHeight = options.outHeight;

        int retioW = imgWidth / newWidth;
//        int retioH = imgHeight / newHeight;
        Log.i("weiSir", "newWidth: " + newWidth);
        Log.i("weiSir", "imgWidth: " + imgWidth);

        BitmapFactory.Options options1 = new BitmapFactory.Options();
        options1.inJustDecodeBounds = false;
        new ColorDrawable(Color.TRANSPARENT);
        options1.inSampleSize = retioW;
        return BitmapFactory.decodeResource(context.getResources(), bitmapId, options1);
    }


    /*
     * 切图、初始状态（正常顺序）
     * @params type      游戏类型
     * @params picSelected 选择的图片
     * @params context       context
     * */

    public static void createInitBitmap(int type, Bitmap picSelected, Context context) {
        Bitmap bitmap = null;
        List<Bitmap> bitmapItemts = new ArrayList<>();
//        每个Item的宽高
        int itemWidth = picSelected.getWidth() / type;
        int itemHeight = picSelected.getHeight() / type;
        Log.i("weiSir", "createInitBitmap: " + type);
        Log.i("weiSir", "createInitBitmap: " + itemWidth);
        Log.i("weiSir", "createInitBitmap: " + itemHeight);
        for (int i = 0; i < type; i++) {
            for (int j = 0; j < type; j++) {
                bitmap = Bitmap.createBitmap(
                        picSelected,
                        j * itemWidth,
                        i * itemHeight,
                        itemWidth,
                        itemHeight);
                bitmapItemts.add(bitmap);
                itemBean = new ItemBean(i * type + j + 1, i * type + j + 1, bitmap);
                GameUtil.mItemBeans.add(itemBean);
            }
        }
//        保存最后一个图片在拼图完成时填充
        PuzzleActivity.mLastBitmap = bitmapItemts.get(type * type - 1);

//      设置最后一个为空Item
        bitmapItemts.remove(type * type - 1);
        GameUtil.mItemBeans.remove(type * type - 1);
        Bitmap blankBitmap = loadBitmap(R.drawable.blank, context);
        blankBitmap = Bitmap.createBitmap(blankBitmap, 0, 0, itemWidth, itemHeight);
        bitmapItemts.add(blankBitmap);
        GameUtil.mItemBeans.add(new ItemBean(type * type, 0, blankBitmap));
        GameUtil.mBlankItemBean = GameUtil.mItemBeans.get(type * type - 1);
    }

    /*
     * 处理图片 放大、缩小到合适位置
     * @param newWidth 缩放后Width
     * @param newHeight 缩放后Height
     * @param bitmap bitmap
     * @return bitmap
     * */
    public static Bitmap resizeBitmap(float newWidth, float newHeight, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(
                newWidth / bitmap.getWidth(),
                newHeight / bitmap.getHeight());
        Bitmap newBitmap = Bitmap.createBitmap(
                bitmap, 0, 0,
                bitmap.getWidth(),
                bitmap.getHeight(),
                matrix, true);
        bitmap.recycle();
        return newBitmap;
    }
}

