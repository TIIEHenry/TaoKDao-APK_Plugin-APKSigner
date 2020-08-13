package taokdao.plugins.apk.signer.key.generate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import sun1.security.x509.X500Name;
import taokdao.plugins.apk.signer.key.bean.KeyStoreType;
import taokdao.plugins.apk.signer.key.bean.KeyTool;

public class KeyParam {
    @NonNull
    @KeyTool(cmd = "-file", comment = "结合-export，指定导出的证书位置及证书名称")
    public String storePath;

    @NonNull
    @KeyTool(cmd = "-storepass", comment = "操作密钥库所需的密码")
    public String storePass;

    @Nullable
    @KeyTool(cmd = "-alias", comment = "别名")
    public String keyAlias = "mykey";

    public String keyAliasType = "RSA";

    public String keyAliasAgr = "SHA512withRSA";

    @Nullable
    public String certPath;

    @NonNull
    @KeyTool(cmd = "-dname", comment = "指定证书拥有者信息")
    public UserInfo userInfo;

    @KeyTool(cmd = "-keysize", comment = "指定密钥长度")
    public int keySize = 1024;
    @NonNull
    @KeyTool(cmd = "-keypass", comment = "指定别名条目的密码（私钥的密码）")
    public String keyPass;

    @NonNull
    public KeyStoreType keyStoreType;

    @KeyTool(cmd = "-validity", comment = "指定创建的证书有效期多少天")
    public long validity = 1;

    public KeyParam() {

    }

    public KeyParam(@NonNull String storePath, long validity, int keySize, @NonNull String keyPass, @NonNull String storePass, @NonNull KeyStoreType keyStoreType, @NonNull UserInfo userInfo) {
        this.storePath = storePath;
        this.validity = validity;
        this.keySize = keySize;
        this.keyPass = keyPass;
        this.storePass = storePass;
        this.keyStoreType = keyStoreType;
        this.userInfo = userInfo;
    }

    /**
     * CN=名字与姓氏,OU=组织单位名称,O=组织名称,L=城市或区域名称,ST=州或省份名称,C=单位的两字母国家代码
     */
    public static class UserInfo {
        @NonNull
        @KeyTool(cmd = "CN", comment = "名字与姓氏")
        public String commonName;

        @NonNull
        @KeyTool(cmd = "OU", comment = "组织单位名称")
        public String organizationUnit;

        @NonNull
        @KeyTool(cmd = "O", comment = "组织名称")
        public String organizationName;

        @NonNull
        @KeyTool(cmd = "L", comment = "城市或区域名称")
        public String localityName;

        @NonNull
        @KeyTool(cmd = "ST", comment = "州或省份名称")
        public String stateName;

        @NonNull
        @KeyTool(cmd = "C", comment = "单位的两字母国家代码")
        public String country;

        public UserInfo(@NonNull String commonName, @NonNull String organizationUnit, @NonNull String organizationName, @NonNull String localityName, @NonNull String stateName, @NonNull String country) {
            this.commonName = commonName;
            this.organizationUnit = organizationUnit;
            this.organizationName = organizationName;
            this.localityName = localityName;
            this.stateName = stateName;
            this.country = country;
        }

        public X500Name toX500Name() throws IOException {
            return new X500Name(this.commonName, this.organizationUnit
                    , this.organizationName, this.localityName, this.stateName
                    , this.country);
        }
    }
}