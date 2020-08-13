package taokdao.plugins.apk.signer.controller;

import android.content.Context;

import androidx.annotation.NonNull;

import taokdao.api.file.operate.FileOperatorPool;
import taokdao.api.main.IMainContext;
import taokdao.api.plugin.bean.PluginManifest;
import taokdao.api.plugin.entrance.BaseDynamicPluginEntrance;
import taokdao.plugins.apk.signer.file.operators.APKSignOperator;
import taokdao.plugins.apk.signer.file.operators.KeyStoreImportOperator;

public class FileOperatorController extends BaseDynamicPluginEntrance {


    private KeyStoreImportOperator keyStoreImportOperator;
    private APKSignOperator apkSignOperator;

    public void onInit(@NonNull final IMainContext iMainContext, @NonNull final PluginManifest pluginManifest) {
        keyStoreImportOperator = new KeyStoreImportOperator(iMainContext, pluginManifest);
        apkSignOperator = new APKSignOperator(pluginContext,pluginManifest);
        FileOperatorPool.getInstance().add(keyStoreImportOperator);
        FileOperatorPool.getInstance().add(apkSignOperator);
    }

    public void onDestroy(@NonNull IMainContext iMainContext, @NonNull PluginManifest pluginManifest) {
        FileOperatorPool.getInstance().remove(keyStoreImportOperator);
        FileOperatorPool.getInstance().remove(apkSignOperator);
    }

}
