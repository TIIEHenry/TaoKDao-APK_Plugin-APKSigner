package taokdao.plugins.apk.signer.key.bean;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class KeyAliasItem {
    public String alias;
    public String keyPass;
    public boolean passCorrect = false;
    public PrivateKey privateKey;
    public X509Certificate certificate;

    public KeyAliasItem(String alias) {
        this.alias = alias;
    }

    public KeyAliasItem(String alias, String keyPass) {
        this.alias = alias;
        this.keyPass = keyPass;
    }

    @Override
    public String toString() {
        return "KeyAliasItem{" +
                "alias='" + alias + '\'' +
                ", keyPass='" + keyPass + '\'' +
                ", passCorrect=" + passCorrect +
                ", privateKey=" + privateKey +
                ", x509Certificate=" + certificate +
                '}';
    }
}
