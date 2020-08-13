package tiiehenry.android.view.adapter.base;

import android.view.View;
import android.view.ViewGroup;

import java.util.Collection;

public abstract class BaseIdRecyclerAdapter<T> extends InflateRecyclerAdapter<T> {

    public BaseIdRecyclerAdapter() {
        super();
    }

    public BaseIdRecyclerAdapter(Collection<T> list) {
        super(list);
    }

    public BaseIdRecyclerAdapter(T[] data) {
        super(data);
    }

    /**
     * 适配的布局
     *
     * @param viewType
     * @return
     */
    protected abstract int getItemLayoutId(int viewType);

    @Override
    protected View inflateItemLayout(ViewGroup parent, int viewType) {
        return inflateView(parent, getItemLayoutId(viewType));
    }

    @Override
    protected InflateRecyclerAdapter<T> getThis() {
        return this;
    }
}
