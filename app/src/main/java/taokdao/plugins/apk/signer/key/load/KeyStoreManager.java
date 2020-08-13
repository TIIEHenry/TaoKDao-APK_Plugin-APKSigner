package taokdao.plugins.apk.signer.key.load;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.List;

import taokdao.api.main.IMainContext;
import taokdao.api.plugin.bean.PluginManifest;
import taokdao.plugins.apk.signer.key.bean.KeyStoreItem;

public class KeyStoreManager {
    public static File getKeysDir(IMainContext iMainContext, PluginManifest pluginManifest) {
        File workDir = iMainContext.getFileSystem().getPluginWorkDir(pluginManifest);
        File keysDir = new File(workDir, "keys");
        keysDir.mkdirs();
        return keysDir;
    }

    public static List<KeyStoreItem> loadItemListFromDir(KeyPassManager keyPassManager, File keysDir) {
        List<KeyStoreItem> keyStoreItemList = new ArrayList<>();
        File[] files = keysDir.listFiles();
        if (files != null) {
            for (File file : files) {
                StorePassManager storePassManager = keyPassManager.getStorePassManager(file.getName());
                KeyStoreItem keyStoreItem=new KeyStoreItem(file);
                try {
                    StoreLoader storeLoader = new StoreLoader(file, storePassManager).load();
                    keyStoreItem.keyStore=storeLoader.keyStore;
                    keyStoreItem.storePass=storePassManager.getStorePass();
                    keyStoreItem.keyAliasItems=storeLoader.aliasItemList;
                } catch (KeyStoreException | IOException e) {
                    e.printStackTrace();
                }
                keyStoreItemList.add(keyStoreItem);
            }
        }
        return keyStoreItemList;
    }
}
