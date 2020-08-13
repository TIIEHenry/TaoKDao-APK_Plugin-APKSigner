package tiiehenry.android.view.adapter.simple;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;

import java.util.Collection;

import tiiehenry.android.view.adapter.base.InflateRecyclerAdapter;

public abstract class SimpleIdRecyclerAdapter<T> extends InflateRecyclerAdapter<T> {

    private final int layoutId;

    public SimpleIdRecyclerAdapter(@LayoutRes int layoutId) {
        super();
        this.layoutId = layoutId;
    }

    public SimpleIdRecyclerAdapter(@LayoutRes int layoutId, Collection<T> list) {
        super(list);
        this.layoutId = layoutId;
    }

    public SimpleIdRecyclerAdapter(@LayoutRes int layoutId, T[] data) {
        super(data);
        this.layoutId = layoutId;
    }

    /**
     * 适配的布局
     *
     * @param parent
     * @param viewType
     * @return layout
     */
    protected View inflateItemLayout(ViewGroup parent, int viewType) {
        return inflateView(parent, layoutId);
    }

    @Override
    protected InflateRecyclerAdapter<T> getThis() {
        return this;
    }

}
