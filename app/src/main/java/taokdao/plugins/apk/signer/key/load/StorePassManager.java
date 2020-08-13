package taokdao.plugins.apk.signer.key.load;

import com.tencent.mmkv.MMKV;

import java.io.File;

public class StorePassManager {
    private String storeName;
    private final MMKV mmkv;

    public StorePassManager(MMKV mmkv, String storeName) {
        this.mmkv = mmkv;
        this.storeName = storeName;
    }

    public String getStorePass() {
        return mmkv.decodeString(storeName);
    }

    public void setStorePass(String password) {
        if (password != null)
            mmkv.encode(storeName, password);
        else
            mmkv.remove(storeName);
    }

    public String getAliasPass(String aliasName) {
        return mmkv.decodeString(storeName + "." + aliasName);
    }

    public void setAliasPass(String aliasName, String password) {
        if (password != null)
            mmkv.encode(storeName + "." + aliasName, password);
        else
            mmkv.remove(storeName + "." + aliasName);
    }

    public void clearStore(){
        setStorePass(null);
        String[] keys = mmkv.allKeys();
        for (String key : keys) {
            if (key.startsWith(storeName + ".")) {
                mmkv.remove(key);
            }
        }
    }

    public void renameTo(String newName) {
        StorePassManager newStorePassManager = new StorePassManager(mmkv, newName);
        newStorePassManager.setStorePass(getStorePass());
        String[] keys = mmkv.allKeys();
        for (String key : keys) {
            if (key.startsWith(storeName + ".")) {
                String alias = key.substring(storeName.length()+1, keys.length);
                String pass = mmkv.getString(key,null);
                newStorePassManager.setAliasPass(alias,pass);
                mmkv.remove(key);
            }
        }
        storeName=newName;
    }
}
