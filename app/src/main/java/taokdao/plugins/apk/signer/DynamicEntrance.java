package taokdao.plugins.apk.signer;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import taokdao.api.main.IMainContext;
import taokdao.api.plugin.bean.PluginManifest;
import taokdao.api.plugin.bridge.invoke.IInvokeCallback;
import taokdao.api.plugin.entrance.BaseDynamicPluginEntrance;
import taokdao.plugins.apk.signer.controller.FileOpenerController;
import taokdao.plugins.apk.signer.controller.FileOperatorController;
import taokdao.plugins.apk.signer.controller.KeyStoreManageController;
import taokdao.plugins.apk.signer.invoke.InvokeController;

@Keep
public class DynamicEntrance extends BaseDynamicPluginEntrance {

    private FileOpenerController fileOpenerController = new FileOpenerController();
    private FileOperatorController fileOperatorController = new FileOperatorController();
    private KeyStoreManageController keyStoreManageController = new KeyStoreManageController();
    private InvokeController invokeController = new InvokeController();

    @Override
    public String onInvoke(@NonNull IMainContext main, @NonNull PluginManifest manifest, @NonNull String method, @Nullable String params, @Nullable IInvokeCallback invokeCallback) {
        return invokeController.onInvoke(main, manifest, method, params, invokeCallback);
    }

    @Override
    public void onAttach(@NonNull Context pluginContext) {
        super.onAttach(pluginContext);
        keyStoreManageController.onAttach(pluginContext);
        fileOperatorController.onAttach(pluginContext);
    }

    @Override
    public void onCreate(@NonNull IMainContext iMainContext, @NonNull PluginManifest pluginManifest) {
    }

    @Override
    public void onInit(@NonNull final IMainContext iMainContext, @NonNull PluginManifest pluginManifest) {
        keyStoreManageController.onInit(iMainContext, pluginManifest);
        fileOperatorController.onInit(iMainContext, pluginManifest);
        fileOpenerController.onInit(iMainContext, pluginManifest);
    }

    @Override
    public void onCall(@NonNull IMainContext iMainContext, @NonNull PluginManifest pluginManifest) {
        keyStoreManageController.onCall(iMainContext, pluginManifest);
    }

    @Override
    public void onDestroy(@NonNull IMainContext iMainContext, @NonNull PluginManifest pluginManifest) {
        fileOperatorController.onDestroy(iMainContext, pluginManifest);
        fileOpenerController.onDestroy(iMainContext, pluginManifest);
        keyStoreManageController.onDestroy(iMainContext, pluginManifest);
    }


}