package taokdao.plugins.apk.signer.key.load;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;

import taokdao.plugins.apk.signer.key.bean.KeyAliasItem;

public class AliasLoader {

    private final KeyAliasItem alias;
    private final KeyStore keyStore;

    public AliasLoader(StoreLoader storeLoader, KeyAliasItem alias) {
        this(storeLoader.keyStore, alias);
    }

    public AliasLoader(KeyStore keyStore, KeyAliasItem alias) {
        this.keyStore = keyStore;
        this.alias = alias;
    }

    public AliasLoader load() throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
        if (alias.keyPass==null) {
            throw new KeyStoreException("keyPass null");
        }
        return load(alias.keyPass);
    }

    public AliasLoader load(String keyPass) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
        alias.privateKey = (PrivateKey) keyStore.getKey(alias.alias, keyPass.toCharArray());
        alias.certificate = (X509Certificate) keyStore.getCertificate(alias.alias);
//        alias.keyPass = keyPass;
        alias.passCorrect = true;
        return this;
    }


}
