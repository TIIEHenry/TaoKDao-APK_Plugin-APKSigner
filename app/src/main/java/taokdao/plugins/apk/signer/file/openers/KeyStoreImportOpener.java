package taokdao.plugins.apk.signer.file.openers;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

import taokdao.api.file.open.IFileOpener;
import taokdao.api.main.IMainContext;
import taokdao.api.plugin.bean.PluginManifest;
import taokdao.api.ui.content.manage.IContentManager;
import taokdao.plugins.apk.signer.AConstant;
import taokdao.plugins.apk.signer.file.KeyStoreImporter;
import taokdao.plugins.apk.signer.key.bean.KeyStoreType;

public class KeyStoreImportOpener implements IFileOpener {
    private final KeyStoreImporter keyStoreImporter;

    public KeyStoreImportOpener(IMainContext main, PluginManifest pluginManifest) {
        this.keyStoreImporter = new KeyStoreImporter(main, pluginManifest);
    }

    @Nullable
    @Override
    public Drawable getIcon() {
        return null;
    }

    @NonNull
    @Override
    public String getLabel() {
        return "Import KeyStore";
    }

    @Nullable
    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void click(@NonNull IMainContext main, @NonNull IContentManager manager, @NonNull String path) {
        keyStoreImporter.importKeyStore(path);
    }

    @NonNull
    @Override
    public String id() {
        return AConstant.FileOpener.IMPORT_KEYSTORE;
    }

    @Override
    public boolean isSupport(@NonNull String path) {
        KeyStoreType type = KeyStoreType.getFromFile(path);
        return type != null;
    }
}
