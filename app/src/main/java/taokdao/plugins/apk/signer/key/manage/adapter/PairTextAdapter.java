package taokdao.plugins.apk.signer.key.manage.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import taokdao.api.main.IMainContext;
import taokdao.api.setting.theme.resource.ThemeColors;
import taokdao.api.setting.theme.resource.ThemeDrawables;
import taokdao.plugins.apk.signer.R;
import tiiehenry.android.view.adapter.base.InflateRecyclerAdapter;
import tiiehenry.android.view.adapter.holder.RecyclerViewHolder;

public class PairTextAdapter extends InflateRecyclerAdapter<PairTextItem> {
    private final LayoutInflater layoutInflater;
    private final ThemeColors themeColors;
    private final ThemeDrawables themeDrawables;
    private final IMainContext mainContext;

    public PairTextAdapter(LayoutInflater layoutInflater, ThemeColors themeColors, ThemeDrawables themeDrawables, IMainContext mainContext) {
        this.layoutInflater = layoutInflater;
        this.themeColors = themeColors;
        this.themeDrawables = themeDrawables;
        this.mainContext = mainContext;
    }

    @Override
    protected View inflateItemLayout(ViewGroup parent, int viewType) {
        return layoutInflater.inflate(R.layout.keystore_manage_info_item, null);
    }

    @Override
    protected void bindData(@NonNull RecyclerViewHolder holder, int position, PairTextItem item) {
        TextView title = holder.getTextView(R.id.tv_title);
        title.setText(item.title);
        title.setTextColor(themeColors.foregroundColor);
        TextView message = holder.getTextView(R.id.tv_message);
        message.setBackground(themeDrawables.getRectangleSelector(mainContext));
        message.setText(item.message);
        message.setTextColor(themeColors.foregroundColor);
    }
}
