package taokdao.plugins.apk.signer.key.generate;

import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Random;

import sun1.security.x509.AlgorithmId;
import sun1.security.x509.CertificateAlgorithmId;
import sun1.security.x509.CertificateExtensions;
import sun1.security.x509.CertificateIssuerName;
import sun1.security.x509.CertificateSerialNumber;
import sun1.security.x509.CertificateSubjectName;
import sun1.security.x509.CertificateValidity;
import sun1.security.x509.CertificateVersion;
import sun1.security.x509.CertificateX509Key;
import sun1.security.x509.KeyIdentifier;
import sun1.security.x509.PrivateKeyUsageExtension;
import sun1.security.x509.SubjectKeyIdentifierExtension;
import sun1.security.x509.X500Name;
import sun1.security.x509.X509CertImpl;
import sun1.security.x509.X509CertInfo;
import taokdao.plugins.apk.signer.key.bean.KeyStoreType;

public class KeyGenerator {
    private final KeyParam keyParam;

    public KeyGenerator(@NonNull KeyParam keyParam) {
        this.keyParam = keyParam;
    }

    private static KeyPair generateKeyPair(int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator instance = KeyPairGenerator.getInstance("RSA");
        instance.initialize(keySize, SecureRandom.getInstance("SHA1PRNG"));
        return instance.generateKeyPair();
    }

    private static CertificateExtensions getCertificateExtensions(PublicKey publicKey, Date startDate, Date endDate) throws IOException {
        CertificateExtensions certificateExtensions = new CertificateExtensions();
        byte[] keyIdentifier = new KeyIdentifier(publicKey).getIdentifier();
        certificateExtensions.set("SubjectKeyIdentifier", new SubjectKeyIdentifierExtension(keyIdentifier));
        certificateExtensions.set("PrivateKeyUsage", new PrivateKeyUsageExtension(startDate, endDate));
        return certificateExtensions;
    }

    private static Date getNotAfterDate(Date startDate, long validity) {
        long duration = validity * 24 * 3600000;
        Date endDate = new Date();
        endDate.setTime(duration + startDate.getTime());
        return endDate;
    }

    public void generate() throws NoSuchAlgorithmException, IOException, CertificateException, NoSuchProviderException, InvalidKeyException, SignatureException, KeyStoreException {
        KeyPair generateKeyPair = generateKeyPair(keyParam.keySize);
        PublicKey publicKey = generateKeyPair.getPublic();
        PrivateKey privateKey = generateKeyPair.getPrivate();

        Date notBeforeDate = new Date();
        Date notAfterDate = getNotAfterDate(notBeforeDate, keyParam.validity);
        CertificateExtensions certificateExtensions = getCertificateExtensions(publicKey, notBeforeDate, notAfterDate);

        X500Name x500Name = keyParam.userInfo.toX500Name();
        X509Certificate certificate = generateCertificate(privateKey, publicKey, x500Name, notBeforeDate, notAfterDate, certificateExtensions, keyParam.keyAliasAgr);

        write(privateKey, certificate, keyParam);

    }


    private static X509Certificate generateCertificate(PrivateKey privateKey, PublicKey publicKey, X500Name x500Name, Date notBeforeDate, Date notAfterDate, @Nullable CertificateExtensions certificateExtensions, String keyAliasAgr) throws CertificateException, IOException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, InvalidKeyException {
        X509CertInfo x509CertInfo = new X509CertInfo();
        x509CertInfo.set(X509CertInfo.VERSION, new CertificateVersion(2));
        x509CertInfo.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber((new Random()).nextInt() & Integer.MAX_VALUE));
        x509CertInfo.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(AlgorithmId.get(keyAliasAgr)));
        x509CertInfo.set(X509CertInfo.SUBJECT, new CertificateSubjectName(x500Name));
        x509CertInfo.set(X509CertInfo.KEY, new CertificateX509Key(publicKey));
        x509CertInfo.set(X509CertInfo.VALIDITY, new CertificateValidity(notBeforeDate, notAfterDate));
        x509CertInfo.set(X509CertInfo.ISSUER, new CertificateIssuerName(x500Name));
        if (certificateExtensions != null) {
            x509CertInfo.set(X509CertInfo.EXTENSIONS, certificateExtensions);
        }

        X509CertImpl x509CertImpl = new X509CertImpl(x509CertInfo);

        x509CertImpl.sign(privateKey, keyAliasAgr);
        return x509CertImpl;
    }

    private static KeyStore getKeyStoreInstance(KeyStoreType keyStoreType) throws KeyStoreException {
        return KeyStore.getInstance(keyStoreType.name);
    }

    private static void writePK8x509_Pem(File keyFile, PrivateKey privateKey, X509Certificate x509Certificate, String certPath) throws CertificateEncodingException, IOException {
        FileOutputStream key = new FileOutputStream(keyFile);
        key.write(privateKey.getEncoded());
        key.close();
        FileOutputStream cert = new FileOutputStream(certPath);
        cert.write("-----BEGIN CERTIFICATE-----\n".getBytes());
        cert.write(Base64.encode(x509Certificate.getEncoded(), Base64.DEFAULT));
        cert.write("\n-----END CERTIFICATE-----\n".getBytes());
        cert.flush();
        cert.close();
    }

    private static void writeKeyStoreFile(File keyFile, PrivateKey privateKey, X509Certificate x509Certificate, KeyParam keyParam) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore keyStore = getKeyStoreInstance(keyParam.keyStoreType);
        if (keyFile.exists()) {
            FileInputStream keyInputStream = new FileInputStream(keyFile);
            keyStore.load(keyInputStream, keyParam.storePass.toCharArray());
            try {
                keyInputStream.close();
            } catch (IOException e) {
            }
        } else {
            keyStore.load(null);
        }

        FileOutputStream keyOutputStream = new FileOutputStream(keyFile);
        keyStore.setKeyEntry(keyParam.keyAlias, privateKey, keyParam.keyPass.toCharArray(), new Certificate[]{x509Certificate});
        keyStore.store(keyOutputStream, keyParam.storePass.toCharArray());

        try {
            keyOutputStream.close();
        } catch (IOException e) {
        }
    }

    private static void write(PrivateKey privateKey, X509Certificate x509Certificate, KeyParam keyParam) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        File keyFile = new File(keyParam.storePath);
        keyFile.getParentFile().mkdirs();

        if (keyParam.keyStoreType == KeyStoreType.PK8_X509_PEM) {
            writePK8x509_Pem(keyFile, privateKey, x509Certificate, keyParam.certPath);
            return;
        }
        writeKeyStoreFile(keyFile, privateKey, x509Certificate, keyParam);

    }
}
