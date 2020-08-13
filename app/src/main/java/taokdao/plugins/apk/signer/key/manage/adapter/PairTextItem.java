package taokdao.plugins.apk.signer.key.manage.adapter;

import android.view.View;

public abstract class PairTextItem implements View.OnClickListener,View.OnLongClickListener {
    public String title;
    public String message;

    public PairTextItem(String title, String message) {
        this.title = title;
        this.message = message;
    }

    @Override
    public abstract void onClick(View v);

    @Override
    public boolean onLongClick(View v) {
        return false;
    }
}
