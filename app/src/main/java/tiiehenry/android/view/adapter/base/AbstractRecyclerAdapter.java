package tiiehenry.android.view.adapter.base;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import tiiehenry.android.view.adapter.holder.OnItemClickListener;
import tiiehenry.android.view.adapter.holder.OnItemLongClickListener;


public abstract class AbstractRecyclerAdapter<DATATYPE, VH extends RecyclerView.ViewHolder, ADAPTER extends AbstractRecyclerAdapter<?, ?, ?>> extends RecyclerView.Adapter<VH> {

    /**
     * 数据源
     */
    protected final List<DATATYPE> mData = new ArrayList<>();
    /**
     * 当前点击的条目
     */
    protected int mSelectPosition = -1;
    /**
     * 点击监听
     */
    private OnItemClickListener<DATATYPE> mClickListener;
    /**
     * 长按监听
     */
    private OnItemLongClickListener<DATATYPE> mLongClickListener;

    public AbstractRecyclerAdapter() {

    }

    public AbstractRecyclerAdapter(Collection<DATATYPE> list) {
        if (list != null) {
            mData.addAll(list);
        }
    }

    public AbstractRecyclerAdapter(DATATYPE[] data) {
        if (data != null && data.length > 0) {
            mData.addAll(Arrays.asList(data));
        }
    }

    protected abstract ADAPTER getThis();

    /**
     * 构建自定义的ViewHolder
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    protected abstract VH getViewHolder(@NonNull ViewGroup parent, int viewType);

    /**
     * 绑定数据
     *
     * @param holder
     * @param position 索引
     * @param item     列表项
     */
    protected abstract void bindData(@NonNull VH holder, int position, DATATYPE item);

    /**
     * 加载布局获取控件
     *
     * @param parent   父布局
     * @param layoutId 布局ID
     * @return
     */
    protected View inflateView(ViewGroup parent, @LayoutRes int layoutId) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final VH holder = getViewHolder(parent, viewType);
        if (mClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onItemClick(holder.itemView, getItem(holder.getLayoutPosition()), holder.getLayoutPosition());
                }
            });
        }
        if (mLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mLongClickListener.onItemLongClick(holder.itemView, getItem(holder.getLayoutPosition()), holder.getLayoutPosition());
                    return true;
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        bindData(holder, position, mData.get(position));
    }

    /**
     * 获取列表项
     *
     * @param position
     * @return
     */
    public DATATYPE getItem(int position) {
        return checkPosition(position) ? mData.get(position) : null;
    }

    private boolean checkPosition(int position) {
        return position >= 0 && position < mData.size();
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * @return 数据源
     */
    public List<DATATYPE> getData() {
        return mData;
    }

    /**
     * 给指定位置添加一项
     *
     * @param pos
     * @param item
     * @return
     */
    public ADAPTER add(int pos, DATATYPE item) {
        if (pos >= 0 && pos <= getItemCount()) {
            mData.add(pos, item);
            notifyItemInserted(pos);
        }
        return getThis();
    }

    /**
     * 在列表末端增加一项
     *
     * @param item
     * @return
     */
    public ADAPTER add(DATATYPE item) {
        mData.add(item);
        notifyItemInserted(mData.size() - 1);
        return getThis();
    }

    /**
     * 删除列表中指定索引的数据
     *
     * @param pos
     * @return
     */
    public ADAPTER delete(int pos) {
        if (checkPosition(pos)) {
            mData.remove(pos);
            notifyItemRemoved(pos);
        }
        return getThis();
    }

    /**
     * 刷新列表中指定位置的数据
     *
     * @param pos
     * @param item
     * @return
     */
    public ADAPTER refresh(int pos, DATATYPE item) {
        if (checkPosition(pos)) {
            mData.set(pos, item);
            notifyItemChanged(pos);
        }
        return getThis();
    }

    /**
     * 刷新列表数据
     *
     * @param collection
     * @return
     */
    public ADAPTER refresh(Collection<DATATYPE> collection) {
        if (collection != null) {
            mData.clear();
            mData.addAll(collection);
            mSelectPosition = -1;
            notifyDataSetChanged();
        }
        return getThis();
    }

    /**
     * 刷新列表数据
     *
     * @param array
     * @return
     */
    public ADAPTER refresh(DATATYPE[] array) {
        if (array != null && array.length > 0) {
            mData.clear();
            mData.addAll(Arrays.asList(array));
            mSelectPosition = -1;
            notifyDataSetChanged();
        }
        return getThis();
    }

    /**
     * 加载更多
     *
     * @param collection
     * @return
     */
    public ADAPTER loadMore(Collection<DATATYPE> collection) {
        if (collection != null) {
            mData.addAll(collection);
            notifyDataSetChanged();
        }
        return getThis();
    }

    /**
     * 加载更多
     *
     * @param array
     * @return
     */
    public ADAPTER loadMore(DATATYPE[] array) {
        if (array != null && array.length > 0) {
            mData.addAll(Arrays.asList(array));
            notifyDataSetChanged();
        }
        return getThis();
    }

    /**
     * 添加一个
     *
     * @param item
     * @return
     */
    public ADAPTER load(DATATYPE item) {
        if (item != null) {
            mData.add(item);
            notifyDataSetChanged();
        }
        return getThis();
    }

    /**
     * 设置列表项点击监听
     *
     * @param listener
     * @return
     */
    public ADAPTER setOnItemClickListener(OnItemClickListener<DATATYPE> listener) {
        mClickListener = listener;
        return getThis();
    }

    /**
     * 设置列表项长按监听
     *
     * @param listener
     * @return
     */
    public ADAPTER setOnItemLongClickListener(OnItemLongClickListener<DATATYPE> listener) {
        mLongClickListener = listener;
        return getThis();
    }

    /**
     * @return 当前列表的选中项
     */
    public int getSelectPosition() {
        return mSelectPosition;
    }

    /**
     * 设置当前列表的选中项
     *
     * @param selectPosition
     * @return
     */
    public ADAPTER setSelectPosition(int selectPosition) {
        mSelectPosition = selectPosition;
        notifyDataSetChanged();
        return getThis();
    }

    /**
     * 获取当前列表选中项
     *
     * @return 当前列表选中项
     */
    public DATATYPE getSelectItem() {
        return getItem(mSelectPosition);
    }

    /**
     * 清除数据
     */
    public void clear() {
        if (!isEmpty()) {
            mData.clear();
            mSelectPosition = -1;
            notifyDataSetChanged();
        }
    }

}
