package com.example.gamemg;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gamemg.Util.DBHelper;
import com.example.gamemg.Util.GameUtil;
import com.example.gamemg.Util.ImagesUtil;
import com.example.gamemg.Util.PuzzleGVListAdapter;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class PuzzleActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_puzzle_main_counts;
    private TextView tv_puzzle_main_time;
    private LinearLayout ll_puzzle_main_spinner;
    private Button btn_pluzzle_main_img;
    private Button btn_pluzzle_main_restart;
    private Button btn_pluzzle_main_back;
    public static int TYPE = 3;
    private int picSelectedID = 0;
    private String cameraPath;
    private Bitmap mPicSelected;
    private String imagePath;
    public static Bitmap mLastBitmap;
    private ImageView mImageView;
    private boolean mIShowImg;
    private GridView gv_puzzle_main_detail;
    private PuzzleGVListAdapter adapter;
    private static int COUNT_INDEX = 0;

    private long ZEROTIME = -28800000;   //date默认是08点 此数字取反值 归到00点
    private static int currentTime = 0;
    private Timer timer;
    private TimerTask timerTask;
    Date date = new Date();
    public static boolean isSucessce = false;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("SimpleDateFormat")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            tv_puzzle_main_time.setText(getUseTime());
        }
    };
    private FileInputStream in;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_main);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg2));

        dataUtil();
        initView();
    }

    /*
     * 创建个人纪录menu
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //    个人纪录
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.gr:
                Intent intent = new Intent(this, RecordActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    private void dataUtil() {
        GameUtil.mItemBeans.clear();

        picSelectedID = getIntent().getIntExtra("picSelectedID", 0);
        imagePath = getIntent().getStringExtra("mPicPath");
        cameraPath = getIntent().getStringExtra("mPath");
        TYPE = getIntent().getIntExtra("mType", 3);


        if (picSelectedID > 0) {   //选择图片
            mPicSelected = ImagesUtil.loadBitmap(picSelectedID, this);
        } else if (imagePath != null) {   //图库
            mPicSelected = BitmapFactory.decodeFile(imagePath);
        } else if (cameraPath != null) {   //相机
            mPicSelected = BitmapFactory.decodeFile(cameraPath);
        }

        ImagesUtil.createInitBitmap(TYPE, mPicSelected, this);
        GameUtil.getPuzzleGenerator();                                  //打乱图片
        adapter = new PuzzleGVListAdapter(this, GameUtil.mItemBeans);
    }

    @Override
    public void onBackPressed() {
        if (mImageView != null && mImageView.getVisibility() == View.VISIBLE) {
            mImageView.setVisibility(View.GONE);
        } else {
            cleanConfig();
            super.onBackPressed();
        }
    }

    private void initView() {
        tv_puzzle_main_counts = (TextView) findViewById(R.id.tv_puzzle_main_counts);
        tv_puzzle_main_time = (TextView) findViewById(R.id.tv_puzzle_main_time);
        ll_puzzle_main_spinner = (LinearLayout) findViewById(R.id.ll_puzzle_main_spinner);
        btn_pluzzle_main_img = (Button) findViewById(R.id.btn_pluzzle_main_img);
        btn_pluzzle_main_restart = (Button) findViewById(R.id.btn_pluzzle_main_restart);
        btn_pluzzle_main_back = findViewById(R.id.btn_pluzzle_main_back);
        btn_pluzzle_main_img.setOnClickListener(this);
        btn_pluzzle_main_restart.setOnClickListener(this);
        btn_pluzzle_main_back.setOnClickListener(this);
        addImgView();

        gv_puzzle_main_detail = (GridView) findViewById(R.id.gv_puzzle_main_detail);
        gv_puzzle_main_detail.setNumColumns(TYPE);

        gv_puzzle_main_detail.setAdapter(adapter);
//       启动线程 刷新时间
        timerStart();
        gv_puzzle_main_detail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (GameUtil.isMoveable(position)) {
                    COUNT_INDEX++;
                    GameUtil.swapItems(GameUtil.mItemBeans.get(position), GameUtil.mBlankItemBean);
                    tv_puzzle_main_counts.setText(" " + COUNT_INDEX);
                    adapter.notifyDataSetChanged();
                }
                if (GameUtil.isSuccess()) {
                    isSucessce = true;
                    //保存纪录
                    RecordBean recordBean = new RecordBean(COUNT_INDEX, getUseTime());
                    if (dbHelper == null)
                        dbHelper = new DBHelper(PuzzleActivity.this);
                    dbHelper.saveR(recordBean);

                    //                    显示最后一格
                    Animation animation = AnimationUtils.loadAnimation(PuzzleActivity.this, R.anim.image_show_anim);
                    ImageView imgview = (ImageView) gv_puzzle_main_detail.getChildAt(GameUtil.mItemBeans.size() - 1);
                    imgview.startAnimation(animation);
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    Toast.makeText(PuzzleActivity.this, "厉害了,成功还原!!!", Toast.LENGTH_LONG).show();
                    gv_puzzle_main_detail.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            显示原图按钮点击事件
            case R.id.btn_pluzzle_main_img:
                Animation showAnimation = AnimationUtils.loadAnimation(PuzzleActivity.this, R.anim.image_show_anim);
                Animation hideAnimation = AnimationUtils.loadAnimation(PuzzleActivity.this, R.anim.image_hide_anim);
                if (mIShowImg) {
                    mImageView.startAnimation(hideAnimation);
                    mImageView.setVisibility(View.GONE);
                    mIShowImg = false;
                } else {
                    mImageView.startAnimation(showAnimation);
                    mImageView.setVisibility(View.VISIBLE);
                    mIShowImg = true;
                }
                break;
//                重置按钮点击事件
            case R.id.btn_pluzzle_main_restart:
                cleanConfig();
                GameUtil.getPuzzleGenerator();
                adapter.notifyDataSetChanged();
                gv_puzzle_main_detail.setEnabled(true);
                break;
            case R.id.btn_pluzzle_main_back:
//                返回按钮点击事件
                PuzzleActivity.this.finish();
                break;
        }
    }

    //    原图添加
    private void addImgView() {
        RelativeLayout relativeLayout = findViewById(R.id.rl_puzzle_main_layout);
        mImageView = new ImageView(this);
        mImageView.setImageBitmap(mPicSelected);
        int x = (int) (mPicSelected.getWidth() * 0.9f);
        int y = (int) (mPicSelected.getHeight() * 0.9f);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(x, y);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mImageView.setLayoutParams(layoutParams);
        relativeLayout.addView(mImageView);
        mImageView.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

    private void timerStart() {
        if (timer != null) {
            timer.cancel();
            timer = null;

        }
        if (timer == null) {
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(0);
                }
            };
        }
        timer.schedule(timerTask, 0, 1000);
    }

    private String getUseTime() {
        currentTime += 1000;
        date.setTime(currentTime + ZEROTIME);
        return new SimpleDateFormat("HH:mm:ss").format(date);
    }

    private void cleanConfig() {
        mIShowImg = false;
        isSucessce = false;
        currentTime = 0;
        COUNT_INDEX = 0;
        mImageView.setVisibility(View.GONE);
        tv_puzzle_main_counts.setText("0");
        tv_puzzle_main_time.setText("0");
        timerStart();
    }
}
