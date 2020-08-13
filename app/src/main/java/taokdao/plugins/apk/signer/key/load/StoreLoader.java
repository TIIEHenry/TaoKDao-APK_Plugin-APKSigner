package taokdao.plugins.apk.signer.key.load;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import taokdao.plugins.apk.signer.key.bean.KeyAliasItem;
import taokdao.plugins.apk.signer.key.bean.KeyStoreType;

public class StoreLoader {
    public final File storeFile;
    public final StorePassManager storePassManager;
    public KeyStore keyStore;
    public ArrayList<KeyAliasItem> aliasItemList;

    public StoreLoader(File storeFile, StorePassManager storePassManager) {
        this.storeFile = storeFile;
        this.storePassManager = storePassManager;
    }

    public StoreLoader load() throws IOException, KeyStoreException {
        load(storePassManager.getStorePass());
        return this;
    }

    public StoreLoader load(String storePass) throws IOException, KeyStoreException {
        if (storePass == null)
            throw new IOException("storePass null");
        KeyStoreType storeType = KeyStoreType.getFromFile(storeFile);
        if (storeType == null)
            throw new IOException("unsupported type");
        keyStore = getKeyStoreFromFile(storePass, storeType);
        if (keyStore == null)
            throw new IOException("keyStore null");
        aliasItemList = loadAliasItemList();
        return this;
    }


    private ArrayList<KeyAliasItem> loadAliasItemList() throws KeyStoreException {
        ArrayList<KeyAliasItem> keyAliasItems = new ArrayList<>();
        ArrayList<String> aliasList = KeyStoreUtils.getAliasListFromKeyStore(keyStore);
        for (String alias : aliasList) {
            KeyAliasItem keyAliasItem = new KeyAliasItem(alias);
            keyAliasItem.keyPass = storePassManager.getAliasPass(alias);
            keyAliasItems.add(keyAliasItem);
        }
        return keyAliasItems;
    }

    @Nullable
    public KeyStore getKeyStoreFromFile(String storePass, KeyStoreType keyStoreType) throws IOException {
        return getKeyStoreFromFile(storeFile, storePass, keyStoreType);
    }


    @Nullable
    public static KeyStore getKeyStoreFromFile(File storeFile, String storePass, KeyStoreType keyStoreType) throws IOException {
        InputStream in = new FileInputStream(storeFile);
        KeyStore keyStore = getKeyStoreFromInputStream(in, storePass, keyStoreType);
        in.close();
        return keyStore;
    }


    @Nullable
    public static KeyStore getKeyStoreFromInputStream(InputStream storeInput, String storePass, KeyStoreType keyStoreType) {
        try {
            KeyStore keyStore = KeyStore.getInstance(keyStoreType.id);
            keyStore.load(storeInput, storePass.toCharArray());
            return keyStore;
        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }

}
