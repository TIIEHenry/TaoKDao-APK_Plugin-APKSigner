package tiiehenry.android.view.adapter.holder;

import android.view.View;

/**
 * 列表条目长按监听
 */
public interface OnItemLongClickListener<T> {
    /**
     * 条目长按
     *
     * @param itemView 条目
     * @param item     数据
     * @param position 索引
     */
    void onItemLongClick(View itemView, T item, int position);
}
