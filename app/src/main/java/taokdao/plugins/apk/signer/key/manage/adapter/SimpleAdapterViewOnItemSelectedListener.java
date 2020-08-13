package taokdao.plugins.apk.signer.key.manage.adapter;

import android.widget.AdapterView;

public interface SimpleAdapterViewOnItemSelectedListener extends AdapterView.OnItemSelectedListener {

    @Override
    default public void onNothingSelected(AdapterView<?> parent) {

    }
}
