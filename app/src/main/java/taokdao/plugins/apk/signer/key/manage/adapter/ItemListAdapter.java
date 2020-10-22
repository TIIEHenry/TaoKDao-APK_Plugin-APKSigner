package taokdao.plugins.apk.signer.key.manage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import taokdao.api.main.IMainContext;
import taokdao.api.setting.theme.ThemeParts;
import taokdao.api.setting.theme.resource.ThemeColors;
import taokdao.api.setting.theme.resource.ThemeDrawables;
import taokdao.plugins.apk.signer.R;
import tiiehenry.android.view.recyclerview.adapter.InflateRecyclerAdapter;
import tiiehenry.android.view.recyclerview.holder.RecyclerViewHolder;

public class ItemListAdapter extends InflateRecyclerAdapter<CharSequence> {
    private final ThemeColors colors;
    private final ThemeDrawables drawables;
    private final Context pluginContext;
    private final IMainContext mainContext;

    public ItemListAdapter(Context pluginContext, IMainContext mainContext, List<CharSequence> items) {
        super(items);
        this.pluginContext = pluginContext;
        this.mainContext = mainContext;
        colors = mainContext.getThemeManager().getThemeColors(ThemeParts.CONTENT);
        drawables = mainContext.getThemeManager().getThemeDrawables(ThemeParts.CONTENT);
    }

    @Override
    protected void bindData(@NonNull RecyclerViewHolder holder, int position, @NonNull CharSequence item) {
        TextView tv = holder.getTextView(R.id.tv_label);
        tv.setText(item);
        tv.setTextColor(colors.foregroundColor);
        holder.itemView.setBackground(drawables.getRectangleSelector(mainContext));
    }

    @Override
    protected View inflateItemLayout(ViewGroup parent, int viewType) {
        return LayoutInflater.from(pluginContext).inflate(R.layout.keystore_manage_name_item, null);
    }
}
