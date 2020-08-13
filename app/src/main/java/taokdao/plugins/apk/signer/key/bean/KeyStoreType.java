package taokdao.plugins.apk.signer.key.bean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import taokdao.api.file.util.FileUtils;

public enum KeyStoreType {
    KEYSTORE_JKS("JKS", new String[]{"jks"}),
    KEYSTORE_PKCS12("PKCS12", new String[]{"pfx", "p12", "pk8"}),
    KEYSTORE_BKS("BKS", new String[]{"bks"}),
    PK8_X509_PEM("pk8+x509.pem", new String[]{"pem"}),
    ;

    public final String name;
    public final String id;
    public final List<String> suffixList = new ArrayList<>();

    KeyStoreType(@NonNull String name, String[] suffixes) {
        this.name = name;
        this.id = name;
        suffixList.addAll(Arrays.asList(suffixes));
    }

    KeyStoreType(@NonNull String name, String id, String[] suffixes) {
        this.name = name;
        this.id = id;
        suffixList.addAll(Arrays.asList(suffixes));
    }

    @Nullable
    public static KeyStoreType getFromSuffix(String suffix) {
        for (KeyStoreType value : KeyStoreType.values()) {
            if (value.suffixList.contains(suffix))
                return value;
        }
        return null;
    }

    @Nullable
    public static KeyStoreType getFromFile(File file) {
        String suffix = FileUtils.getSuffix(file);
        KeyStoreType type = getFromSuffix(suffix);
        if (type == KEYSTORE_PKCS12) {
            if (new File(file.getParent(), FileUtils.getNameWithoutExtension(file) + ".x509.pem").exists())
                return PK8_X509_PEM;
        } else if (type == PK8_X509_PEM) {
            if (new File(file.getParent(), FileUtils.getNameWithoutExtension(file) + ".pk8").exists())
                return PK8_X509_PEM;
            else
                return null;
        }
        return type;
    }

    @Nullable
    public static KeyStoreType getFromFile(String file) {
        return getFromFile(new File(file));
    }

}
