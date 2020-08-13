package taokdao.plugins.apk.signer.key.load;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Enumeration;

public class KeyStoreUtils {

    public static ArrayList<String> getAliasListFromKeyStore(KeyStore keyStore) throws KeyStoreException {
        ArrayList<String> aliasList = new ArrayList<>();
        Enumeration<String> aliases = keyStore.aliases();

        while (aliases.hasMoreElements()) {
            aliasList.add(aliases.nextElement());
        }
        return aliasList;
    }
}
