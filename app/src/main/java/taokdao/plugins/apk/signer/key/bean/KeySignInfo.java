package taokdao.plugins.apk.signer.key.bean;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class KeySignInfo {
    public String storePassword;
    public String keyPassword;
    public String keyAlias;
    public KeyStoreType keyStoreType;

    public PrivateKey privateKey;
    public X509Certificate certificate;

    public KeySignInfo() {
    }
}
