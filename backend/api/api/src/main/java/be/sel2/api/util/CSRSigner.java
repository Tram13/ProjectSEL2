package be.sel2.api.util;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

public class CSRSigner {

    private CSRSigner() {
    }

    /**
     * Signs and returns a {@link X509Certificate} from the CSR, read by pemCsr.
     *
     * @param pemCsr   The reader reading the CSR
     * @param validity How long the Certificate should be valid, in days
     * @param keystore The keystore used to sign the Certificate
     * @param alias    The alias of the private key used to sign the Certificate
     * @param password The password of the private key used to sign the Certificate
     */
    public static X509Certificate sign(Reader pemCsr, int validity, KeyStore keystore, String alias, char[] password)
            throws NoSuchAlgorithmException,
            NoSuchProviderException, IOException,
            OperatorCreationException, CertificateException, UnrecoverableKeyException, KeyStoreException {
        /*get the key out of the p12 file*/
        PrivateKey cakey = (PrivateKey) keystore.getKey(alias, password);
        /*get the certificate out of the p12 file*/
        X509Certificate cacert = (X509Certificate) keystore.getCertificate(alias);

        /* parse the csr, which is in pem format*/
        PEMParser reader = new PEMParser(pemCsr);
        Object parsedObj = reader.readObject();
        PKCS10CertificationRequest csr;
        if (parsedObj instanceof PKCS10CertificationRequest) {
            csr = (PKCS10CertificationRequest) parsedObj;
        } else {
            return null;
        }

        /*uit de docs*/
        /*Find the signature algorithm identifier that matches with the passed in signature algorithm name.*/
        AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find("SHA512withRSA");
        AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlgId);

        AsymmetricKeyParameter foo = PrivateKeyFactory.createKey(cakey.getEncoded());

        BigInteger serial = new BigInteger(32, new SecureRandom());
        Date from = new Date();
        /*make cert valid for a year*/
        Date to = new Date(System.currentTimeMillis() + (validity * 86400000L));
        X500Name issuer = new X500Name(cacert.getSubjectX500Principal().getName());

        X509v3CertificateBuilder myCertificateGenerator = new X509v3CertificateBuilder(issuer, serial, from, to, csr.getSubject(), csr.getSubjectPublicKeyInfo());

        /* keyusage, for what can the key be used, no ca permissions given, encryption and sign*/
        myCertificateGenerator.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.keyAgreement | KeyUsage.digitalSignature | KeyUsage.keyEncipherment | KeyUsage.dataEncipherment));

        ContentSigner sigGen = new BcRSAContentSignerBuilder(sigAlgId, digAlgId).build(foo);

        X509CertificateHolder holder = myCertificateGenerator.build(sigGen);
        org.bouncycastle.asn1.x509.Certificate eeX509CertificateStructure = holder.toASN1Structure();

        CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");

        // Read Certificate
        InputStream is1 = new ByteArrayInputStream(eeX509CertificateStructure.getEncoded());
        X509Certificate theCert = (X509Certificate) cf.generateCertificate(is1);
        is1.close();
        return theCert;
    }
}
