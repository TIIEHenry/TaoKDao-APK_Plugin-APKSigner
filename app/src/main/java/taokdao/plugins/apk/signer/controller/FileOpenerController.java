package taokdao.plugins.apk.signer.controller;

import androidx.annotation.NonNull;

import taokdao.api.file.open.FileOpenerPool;
import taokdao.api.main.IMainContext;
import taokdao.api.plugin.bean.PluginManifest;
import taokdao.api.plugin.entrance.BaseDynamicPluginEntrance;
import taokdao.plugins.apk.signer.file.openers.KeyStoreImportOpener;
import taokdao.plugins.apk.signer.key.load.KeyStoreManager;

public class FileOpenerController extends BaseDynamicPluginEntrance {
    private KeyStoreImportOpener keyStoreImportOpener;

    public FileOpenerController(){
    }
    public void onInit(@NonNull final IMainContext iMainContext, final PluginManifest pluginManifest) {
        keyStoreImportOpener = new KeyStoreImportOpener(iMainContext,pluginManifest);
        FileOpenerPool.getInstance().add(keyStoreImportOpener);
    }

    public void onDestroy(@NonNull IMainContext iMainContext, @NonNull PluginManifest pluginManifest) {
        FileOpenerPool.getInstance().remove(keyStoreImportOpener);
    }
}
