package com.example.gamemg;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.gamemg.Util.GridPicListAdapter;
import com.example.gamemg.Util.ImagesUtil;
import com.example.gamemg.Util.ScreenUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private GridView mGvPicList;
    private PopupWindow mPopupWindow;

    private List<Bitmap> mPiclist = new ArrayList<>();
    //返回码: 本地图库
    private static final int RESULT_IMAGE = 100;
    //    相机放回码
    private static final int RESULT_CAMERA = 200;
    //      IMAGE_TYPE
    private static final String IMAGE_TYPE = "image/*";
    //    相机图片路径
    private static final String TEMP_IMAGE_PATH = Environment.getExternalStorageDirectory().getPath() + "/temp.png";
    private Button btn1;
    private Button btn2;
    private TextView selectType;
    private TextView tv_puzzle_main_type_selected;
    private LinearLayout ll_puzzle_main_spinner;
    private View popWindow = null;
    private AlertDialog dialog;
    //        初始化Bitmap数据
    private int[] mResPicId = new int[]{
            R.drawable.a1, R.drawable.a2,
            R.drawable.a3, R.drawable.a4,
            R.drawable.a5, R.drawable.a6,
            R.drawable.a7, R.drawable.a8,
            R.drawable.a9, R.drawable.a10,
            R.drawable.a11, R.drawable.a12,
            R.drawable.a13, R.drawable.a14,
            R.drawable.a15, R.drawable.a16,

    };  //放图片


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg2));
        requestPermission();
        initView();
        mGvPicList.setAdapter(new GridPicListAdapter(this, mPiclist));
        mGvPicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == mResPicId.length - 1) {
//                    选择本地图库,相机
                    showDialogCustom();
                } else {
//                    选择默认图片
                    Intent intent = new Intent(
                            MainActivity.this, PuzzleActivity.class
                    );
                    intent.putExtra("picSelectedID", mResPicId[position]);
                    intent.putExtra("mType", myType);
                    Log.i("weiSir", "onItemClick: " + myType);
                    startActivity(intent);
                }
            }
        });
    }

    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)//缺少权限，进行权限申请
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
    }

    private void initView() {
        mGvPicList = (GridView) findViewById(R.id.gv_xpuzzle_main_pic_list);

        for (int i = 0; i < mResPicId.length; i++) {
            Bitmap bitmap = ImagesUtil.loadBitmap(mResPicId[i], this);
            Log.i("weiSir", "getWidth: " + bitmap.getWidth());
            Log.i("weiSir", "getHeight: " + bitmap.getHeight());
            mPiclist.add(bitmap);
        }

        selectType = (TextView) findViewById(R.id.selectType);
        selectType.setOnClickListener(this);
        tv_puzzle_main_type_selected = (TextView) findViewById(R.id.tv_puzzle_main_type_selected);
        ll_puzzle_main_spinner = (LinearLayout) findViewById(R.id.ll_puzzle_main_spinner);
    }

    private void popupShow(View view) {
        int density = (int) ScreenUtil.getDeviceDensity(this);
//        显示popup window
        mPopupWindow = new PopupWindow(view, 300 * density, 400 * density);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
//       透明背景
        Drawable drawable = new ColorDrawable(Color.TRANSPARENT);
        mPopupWindow.setBackgroundDrawable(drawable);
//        获取位置
        int location[] = new int[2];
//        获取view在屏幕上的位置
        selectType.getLocationOnScreen(location);
        mPopupWindow.showAtLocation(
                selectType,
                Gravity.CENTER,
                0,
                0
        );
    }

    private void showDialogCustom() {
        View view = View.inflate(this, R.layout.dialogview, null);
        btn1 = (Button) view.findViewById(R.id.btn1);
        btn1.setOnClickListener(this);
        btn2 = (Button) view.findViewById(R.id.btn2);
        btn2.setOnClickListener(this);
        dialog = new AlertDialog.Builder(this)
                .setMessage("选择图片来源:")
                .setView(view).setCancelable(true)
                .create();
        dialog.show();
    }

    private int myType = 3;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
//                本地图库
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_TYPE);
                startActivityForResult(intent, RESULT_IMAGE);
                dialog.dismiss();
                break;
            case R.id.btn2:
//                  相机
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                }
                Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri photoUri = Uri.fromFile(new File(TEMP_IMAGE_PATH));
                intent1.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                此处报错的原因是Android 7.0及以上不允许intent带有file://的URI离开自身的应用。
                startActivityForResult(intent1, RESULT_CAMERA);
                dialog.dismiss();
                break;
            case R.id.selectType:
                if (popWindow == null) {
                    popWindow = View.inflate(this, R.layout.typeselect, null);
                    ViewHolder h = new ViewHolder(popWindow);
                }
                popupShow(popWindow);
                break;
            case R.id.one:
                myType = 3;
                tv_puzzle_main_type_selected.setText("难度: 3 x 3");
                mPopupWindow.dismiss();
                break;
            case R.id.two:
                myType = 4;
                tv_puzzle_main_type_selected.setText("难度: 4 x 4");
                mPopupWindow.dismiss();
                break;
            case R.id.three:
                myType = 5;
                tv_puzzle_main_type_selected.setText("难度: 5 x 5");
                mPopupWindow.dismiss();
                break;
            case R.id.four:
                myType = 6;
                tv_puzzle_main_type_selected.setText("难度: 6 x 6");
                mPopupWindow.dismiss();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dialog.dismiss();

        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_IMAGE && data != null) {
//                相册
                Cursor cursor = this.getContentResolver().query(data.getData(), null, null, null);
                cursor.moveToFirst();
                String imagePath = cursor.getString(
                        cursor.getColumnIndex("_data"));
                Intent intent = new Intent(
                        MainActivity.this,
                        PuzzleActivity.class);
                intent.putExtra("mPicPath", imagePath);
                intent.putExtra("mType", myType);
                cursor.close();
                startActivity(intent);
            } else if (requestCode == RESULT_CAMERA) {
//              相机
                Intent intent = new Intent(MainActivity.this, PuzzleActivity.class);
                intent.putExtra("mPath", TEMP_IMAGE_PATH);
                intent.putExtra("mType", myType);
                startActivity(intent);
            }
        }
    }

    public class ViewHolder {
        public View rootView;
        public TextView one;
        public TextView two;
        public TextView three;
        public TextView four;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.one = (TextView) rootView.findViewById(R.id.one);
            this.two = (TextView) rootView.findViewById(R.id.two);
            this.three = (TextView) rootView.findViewById(R.id.three);
            this.four = (TextView) rootView.findViewById(R.id.four);

            this.one.setOnClickListener(MainActivity.this);
            this.two.setOnClickListener(MainActivity.this);
            this.three.setOnClickListener(MainActivity.this);
            this.four.setOnClickListener(MainActivity.this);
        }
    }
}
