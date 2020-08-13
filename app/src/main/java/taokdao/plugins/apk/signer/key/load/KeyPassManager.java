package taokdao.plugins.apk.signer.key.load;

import com.tencent.mmkv.MMKV;

import java.io.File;

import taokdao.api.main.IMainContext;
import taokdao.api.plugin.bean.PluginManifest;

public class KeyPassManager {
    public final MMKV mmkv;

    private KeyPassManager(MMKV mmkv) {
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

    public static KeyPassManager fromMMKV(MMKV mmkv) {
        return new KeyPassManager(mmkv);
    }

    public static KeyPassManager from(IMainContext main, PluginManifest pluginManifest) {
        MMKV mmkv = main.getMMKVManager().getPluginMMKV(pluginManifest);
        return fromMMKV(mmkv);
    }
}
