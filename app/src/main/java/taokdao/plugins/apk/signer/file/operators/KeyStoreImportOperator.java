package taokdao.plugins.apk.signer.file.operators;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import taokdao.api.file.operate.IFileOperator;
import taokdao.api.main.IMainContext;
import taokdao.api.plugin.bean.PluginManifest;
import taokdao.plugins.apk.signer.PluginConstant;
import taokdao.plugins.apk.signer.file.KeyStoreImporter;
import taokdao.plugins.apk.signer.key.bean.KeyStoreType;

public class KeyStoreImportOperator implements IFileOperator {


    private final PluginManifest pluginManifest;

    public KeyStoreImportOperator(IMainContext main, PluginManifest pluginManifest) {
        this.pluginManifest =pluginManifest;
    }

    @Nullable
    @Override
    public Drawable getIcon() {
        return null;
    }

    @NonNull
    @Override
    public String getLabel() {
        return "Import Keystore";
    }

    @Nullable
    @Override
    public String getDescription() {
        return "Import Keystore From File";
    }

    @NonNull
    @Override
    public String id() {
        return PluginConstant.FileOperator.IMPORT_KEYSTORE;
    }

    @Override
    public boolean call(IMainContext main, String path) {
        new KeyStoreImporter(main, pluginManifest).importKeyStore(path);
        return true;
    }

    @Override
    public boolean isSupport(@NonNull String path) {
        KeyStoreType type = KeyStoreType.getFromFile(path);
        return type != null;
    }

}
