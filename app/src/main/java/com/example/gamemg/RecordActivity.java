package com.example.gamemg;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gamemg.Util.DBHelper;
import com.example.gamemg.Util.GameUtil;
import com.example.gamemg.Util.ImagesUtil;

import java.util.List;

public class RecordActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private List<RecordBean> record;
    private ListView list_item;
    private TextView empty;
    private Adapter adapter;
    private TextView gameTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg2));
        dbHelper = new DBHelper(this);
        record = dbHelper.getRecord();
        initView();
    }

    private void initView() {
        list_item = (ListView) findViewById(R.id.list_item);
        empty = (TextView) findViewById(R.id.empty);
        list_item.setEmptyView(empty);
        adapter = new Adapter();
        list_item.setAdapter(adapter);
        gameTitle = (TextView) findViewById(R.id.gameTitle);
//        设置中文仿粗体  默认只能是英文
        gameTitle.getPaint().setFakeBoldText(true);
//        gameTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts.ttf"));
    }

    class Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return record.size();
        }

        @Override
        public Object getItem(int position) {
            return record.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder h;
            int step = record.get(position).getStep();
            String time = record.get(position).getTime();
            if (convertView == null) {
                convertView = View.inflate(RecordActivity.this, R.layout.r_item, null);
                h = new ViewHolder(convertView);
                convertView.setTag(h);
            } else {
                h = (ViewHolder) convertView.getTag();
            }
            if (position > 2) {
                h.iconTop.setVisibility(View.GONE);
                h.numbTop.setText(position + 1 + "");
            } else {
                h.iconTop.setVisibility(View.VISIBLE);
                h.timeTv.setTextSize(35);
                h.stepTv.setTextSize(35);
                switch (position) {
                    case 0:
                        h.iconTop.setImageResource(R.drawable.no1);
                        convertView.setBackgroundResource(R.drawable.bgno1);
                        break;
                    case 1:
                        h.iconTop.setImageResource(R.drawable.no2);
                        convertView.setBackgroundResource(R.drawable.bgno2);
                        break;
                    case 2:
                        h.iconTop.setImageResource(R.drawable.no3);
                        convertView.setBackgroundResource(R.drawable.bgno3);
                        break;
                }
            }
            h.stepTv.setText(step+"");
            h.timeTv.setText(time);
            return convertView;
        }
    }

    public class ViewHolder {
        public View rootView;
        public ImageView iconTop;
        public TextView numbTop;
        public TextView stepTv;
        public TextView timeTv;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.iconTop = (ImageView) rootView.findViewById(R.id.iconTop);
            this.numbTop = (TextView) rootView.findViewById(R.id.numbTop);
            this.stepTv = (TextView) rootView.findViewById(R.id.stepTv);
            this.timeTv = (TextView) rootView.findViewById(R.id.timeTv);
        }
    }
}
