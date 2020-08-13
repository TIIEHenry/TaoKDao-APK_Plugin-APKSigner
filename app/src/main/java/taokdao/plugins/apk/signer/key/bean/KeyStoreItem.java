package taokdao.plugins.apk.signer.key.bean;

import java.io.File;
import java.security.KeyStore;
import java.util.ArrayList;

public class KeyStoreItem {
    public File storeFile;
    public String storePass;
    public ArrayList<KeyAliasItem> keyAliasItems = new ArrayList<>();
    public KeyStore keyStore;


    public KeyStoreItem(File storeFile) {
        this.storeFile = storeFile;
    }
}
