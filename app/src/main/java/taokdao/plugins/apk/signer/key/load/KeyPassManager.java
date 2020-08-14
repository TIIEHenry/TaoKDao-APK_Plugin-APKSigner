package taokdao.plugins.apk.signer.key.load;

import java.io.File;

import taokdao.api.data.mmkv.IMMKV;
import taokdao.api.main.IMainContext;
import taokdao.api.plugin.bean.PluginManifest;

public class KeyPassManager {
    public final IMMKV mmkv;

    private KeyPassManager(IMMKV mmkv) {
        this.mmkv = mmkv;
    }

    public StorePassManager getStorePassManager(String storeName) {
        return new StorePassManager(mmkv,storeName);
    }
    public StorePassManager getStorePassManager(File storeFile) {
        return new StorePassManager(mmkv,storeFile.getName());
    }

    public void setStorePass(String name, String password) {
        mmkv.encode(name, password);
    }

    public void setStoreAliasPass(String storeName, String aliasName, String password) {
        mmkv.encode(storeName + "." + aliasName, password);
    }

    public static KeyPassManager fromMMKV(IMMKV mmkv) {
        return new KeyPassManager(mmkv);
    }

    public static KeyPassManager from(IMainContext main, PluginManifest pluginManifest) {
        IMMKV mmkv = main.getMMKVManager().getPluginMMKV(pluginManifest);
        return fromMMKV(mmkv);
    }
}
