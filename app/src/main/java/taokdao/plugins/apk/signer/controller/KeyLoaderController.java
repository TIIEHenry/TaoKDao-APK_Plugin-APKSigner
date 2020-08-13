package taokdao.plugins.apk.signer.controller;

import androidx.annotation.NonNull;

import java.security.Security;

import sun1.security.provider.JavaProvider;
import taokdao.api.main.IMainContext;
import taokdao.api.plugin.bean.PluginManifest;

public class KeyLoaderController {


    private JavaProvider javaProvider;

    public void onInit(@NonNull final IMainContext iMainContext, final PluginManifest pluginManifest) {
        javaProvider = new JavaProvider();
        Security.addProvider(javaProvider);

    }

    public void onDestroy(@NonNull IMainContext iMainContext, PluginManifest pluginManifest) {
        Security.removeProvider(javaProvider.getName());
    }
}
