package com.example.gamemg.Util;

import com.example.gamemg.ItemBean;
import com.example.gamemg.PuzzleActivity;

import java.util.ArrayList;
import java.util.List;

public class GameUtil {
    public static List<ItemBean> mItemBeans = new ArrayList<>();
    public static ItemBean mBlankItemBean;
    static List<Integer> data = new ArrayList<>();


    /*
     * 生成随机的Item
     * */
    public static void getPuzzleGenerator() {
        int index = 0;
        data.clear();
//        随机打乱顺序
        for (int i = 0; i < mItemBeans.size(); i++) {
            index = (int) (Math.random() * PuzzleActivity.TYPE * PuzzleActivity.TYPE);
            swapItems(mItemBeans.get(index), mBlankItemBean);
        }

        for (int i = 0; i < mItemBeans.size(); i++) {
            data.add(mItemBeans.get(i).getmBitmapId());
        }
//        判断生成是否有解
        if (canSolve(data)) {
            return;
        } else {
            getPuzzleGenerator();
        }
    }

    /*
     * 该数据是否有解
     *
     * @param data  拼图数组数据
     * @return 该数据是否有解
     * */
    public static boolean canSolve(List<Integer> data) {
//        获取空格ID
        int blankId = mBlankItemBean.getmItemId();
//        可行性原则
        if (data.size() % 2 == 1) {
            return getInversions(data) % 2 == 0;
        } else {
//            从下往上数，空格位于j奇数行
            if (((blankId - 1) / PuzzleActivity.TYPE) % 2 == 1) {
                return getInversions(data) % 2 == 0;
            } else {
//                从下往上数，空位位属于偶数行
                return getInversions(data) % 2 == 1;
            }
        }
    }

    /*
     * 计算倒置和算法
     *
     * @param data 拼图数组数据
     * @return 该序列的倒置和
     * */
    public static int getInversions(List<Integer> data) {
        int inversions = 0;
        int inversionCount = 0;
        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < data.size(); j++) {
                int index = data.get(i);
                if (data.get(j) != 0 && data.get(j) < index) {
                    inversionCount++;
                }
            }
            inversions += inversionCount;
            inversionCount = 0;
        }
        return inversions;
    }

    /*
     * 交换空格与点击Item的位置
     *
     * @param from  交换图
     * @param blank  空白图
     * */
    public static void swapItems(ItemBean from, ItemBean blank) {
        ItemBean tempItemBean = new ItemBean();
//        交换bitmapId
        tempItemBean.setmBitmapId(from.getmBitmapId());
        from.setmBitmapId(blank.getmBitmapId());
        blank.setmBitmapId(tempItemBean.getmBitmapId());
//        交换Bitmap
        tempItemBean.setmBitmap(from.getmBitmap());
        from.setmBitmap(blank.getmBitmap());
        blank.setmBitmap(tempItemBean.getmBitmap());
//        设置新得Blank
        mBlankItemBean = from;
    }

    /*
     *判断点击的Item是否可移动
     *
     * @param position position
     * @return 能否移动
     * */
    public static boolean isMoveable(int position) {
        int type = PuzzleActivity.TYPE;
//        获取空格
        int blankId = mBlankItemBean.getmItemId() - 1;
//        不同行相差为type
        if (Math.abs(blankId - position) == type) {
            return true;
        }
//        相同行相差为1
        else {
            if ((blankId / type == position / type) && Math.abs(blankId - position) == 1) {
                return true;
            }
        }
        return false;
    }

    /*
     * 是否拼图成功
     *
     * @return 是否拼图成功
     * */
    public static boolean isSuccess() {
        for (ItemBean tempBean :
                mItemBeans) {
            if (tempBean.getmBitmapId() != 0 && (tempBean.getmItemId()) == tempBean.getmBitmapId()) {  //空白不相等
                continue;
            } else if (tempBean.getmBitmapId() == 0 && tempBean.getmItemId() == PuzzleActivity.TYPE * PuzzleActivity.TYPE) { //空格在最后位置
                continue;
            } else {
                return false;
            }
        }
        return true;//返回true表示还原成功
    }
}
