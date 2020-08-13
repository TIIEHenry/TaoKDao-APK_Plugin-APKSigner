package tiiehenry.android.view.adapter.holder;

import android.view.View;

/**
 * 列表条目点击监听
 */
public interface OnItemClickListener<T> {
    /**
     * 条目点击
     *
     * @param itemView 条目
     * @param item     数据
     * @param position 索引
     */
    void onItemClick(View itemView, T item, int position);
}
