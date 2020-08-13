package taokdao.plugins.apk.signer.controller;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.security.Security;

import sun1.security.provider.JavaProvider;
import taokdao.api.main.IMainContext;
import taokdao.api.plugin.bean.PluginManifest;
import taokdao.api.plugin.entrance.BaseDynamicPluginEntrance;
import taokdao.plugins.apk.signer.key.manage.KeyStoreManageDialog;

public class KeyStoreManageController extends BaseDynamicPluginEntrance {

    private JavaProvider javaProvider;

    public void onInit(@NonNull IMainContext iMainContext, @NonNull PluginManifest pluginManifest) {
        javaProvider = new JavaProvider();
        Security.addProvider(javaProvider);
    }

    public void onCall(@NonNull IMainContext iMainContext, @NonNull PluginManifest pluginManifest) {
        new KeyStoreManageDialog(iMainContext, pluginManifest, pluginContext).show();
    }

    public void onDestroy(@NonNull IMainContext iMainContext, @NonNull PluginManifest pluginManifest) {
        Security.removeProvider(javaProvider.getName());
    }


}
