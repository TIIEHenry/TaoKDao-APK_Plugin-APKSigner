package tiiehenry.android.view.adapter.holder;

import android.view.View;

/**
 * 布局内控件点击事件
 */
public interface OnViewItemClickListener<T> {
    /**
     * 控件被点击
     *
     * @param view     被点击的控件
     * @param item     数据
     * @param position 索引
     */
    void onViewItemClick(View view, T item, int position);
}
