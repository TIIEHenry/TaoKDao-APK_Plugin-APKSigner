package tiiehenry.android.view.adapter.base;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.Collection;

import tiiehenry.android.view.adapter.holder.RecyclerViewHolder;


public abstract class InflateRecyclerAdapter<T> extends AbstractRecyclerAdapter<T, RecyclerViewHolder, InflateRecyclerAdapter<T>> {

    public InflateRecyclerAdapter() {
        super();
    }

    public InflateRecyclerAdapter(Collection<T> list) {
        super(list);
    }

    public InflateRecyclerAdapter(T[] data) {
        super(data);
    }

    /**
     * 适配的布局
     *
     * @param parent
     * @param viewType
     * @return layout
     */
    protected abstract View inflateItemLayout(ViewGroup parent, int viewType);

    @NonNull
    @Override
    protected RecyclerViewHolder getViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(inflateItemLayout(parent, viewType));
    }

    @Override
    protected InflateRecyclerAdapter<T> getThis() {
        return this;
    }
}