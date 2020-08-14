package taokdao.plugins.apk.signer.key.load;

import taokdao.api.data.mmkv.IMMKV;

public class StorePassManager {
    private String storeName;
    private final IMMKV mmkv;

    public StorePassManager(IMMKV mmkv, String storeName) {
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
            mmkv.removeValueForKey(storeName);
    }

    public String getAliasPass(String aliasName) {
        return mmkv.decodeString(storeName + "." + aliasName);
    }

    public void setAliasPass(String aliasName, String password) {
        if (password != null)
            mmkv.encode(storeName + "." + aliasName, password);
        else
            mmkv.removeValueForKey(storeName + "." + aliasName);
    }

    public void clearStore(){
        setStorePass(null);
        String[] keys = mmkv.allKeys();
        for (String key : keys) {
            if (key.startsWith(storeName + ".")) {
                mmkv.removeValueForKey(key);
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
                String pass = mmkv.decodeString(key,null);
                newStorePassManager.setAliasPass(alias,pass);
                mmkv.removeValueForKey(key);
            }
        }
        storeName=newName;
    }
}
