package taokdao.plugins.apk.signer.key.manage.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

public class SpinnerUtils {

    public static ArrayAdapter<String> newSpinnerAdapter(Context context, String[] data) {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, data);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return spinnerAdapter;
    }

    public static ArrayAdapter<String> newSpinnerAdapter(Context context, List<String> data) {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, data);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return spinnerAdapter;
    }

}
