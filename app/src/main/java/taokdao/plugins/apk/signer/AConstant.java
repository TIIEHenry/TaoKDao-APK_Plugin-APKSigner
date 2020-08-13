package taokdao.plugins.apk.signer;

import androidx.annotation.Keep;

@Keep
public class AConstant {
    public static String Project_Template_ID = AConstant.class.getPackage().getName();

    public static class FileOpener {
        public static final String IMPORT_KEYSTORE = "taokdao.opener.apk.signer.import";
    }

    public static class FileOperator {
        public static final String IMPORT_KEYSTORE = FileOpener.IMPORT_KEYSTORE;
        public static final String SIGN_APK = "taokdao.opener.apk.signer.sign";
    }


    public static class Invoker {
        public static final String PARAM_SIGN_APK = "sign_apk";
    }
}
