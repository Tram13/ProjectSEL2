package be.sel2.api.util_tests;

import be.sel2.api.AbstractTest;
import be.sel2.api.util.CSRSigner;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class CSRSignerTest extends AbstractTest {

    @Value("${server.ssl.key-store-password}")
    private String keyStorePassword;
    @Value("${server.ssl.key-alias}")
    private String keyStoreAlias;
    @Value("${server.ssl.key-store}")
    private String keyStorePath;

    @Test
    void testCSRSigning(@Autowired ResourceLoader loader) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        Security.addProvider(new BouncyCastleProvider());


        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        if (keyStorePath.startsWith("classpath:")){
            Resource keyStoreResource = loader.getResource(keyStorePath);
            keyStore.load(keyStoreResource.getInputStream(), keyStorePassword.toCharArray());
        } else {
            keyStore.load(new FileInputStream(keyStorePath), keyStorePassword.toCharArray());
        }

        AtomicReference<X509Certificate> cert = new AtomicReference<>();

        assertDoesNotThrow(() ->
                cert.set(CSRSigner.sign(new MockedCSRFileReader(), 365,
                        keyStore, keyStoreAlias, keyStoreAlias.toCharArray())), "Sign should succeed");

        X509Certificate certificate = cert.get();

        assertNotNull(certificate);

        assertEquals("OU=ovo765848,ST=Oost Vlaanderen,O=ovo197635,L=Gent,CN=sel2-2.com,C=BE",
                certificate.getSubjectX500Principal().getName());

        assertDoesNotThrow((Executable) certificate::checkValidity, "Certificate should be valid now");

        Date now = new Date();

        Calendar c = Calendar.getInstance();
        c.setTime(now);
        c.add(Calendar.DATE, 1);

        Date tomorrow = c.getTime();

        assertDoesNotThrow(() -> certificate.checkValidity(tomorrow), "Certificate should be valid now");

        c.add(Calendar.DATE, 365);
        Date nextYear = c.getTime();

        assertThrows(CertificateExpiredException.class, () -> certificate.checkValidity(nextYear), "Certificate should no longer be valid");
    }

    /** Mocked CSR Reader containing fake CSR with following data:
     *  + Country: BE
     *  + State: Oost Vlaanderen
     *  + Locality: Gent
     *  + Organisation: ovo197635
     *  + Organizational Unit: ovo765848
     *  + Common Name: sel2-2.com
     *  + Key Size: 2048
     */
    private static class MockedCSRFileReader extends StringReader {
        public MockedCSRFileReader() {
            super(
                    "-----BEGIN CERTIFICATE REQUEST-----\n" +
                            "MIICuDCCAaACAQAwczELMAkGA1UEBhMCQkUxEzARBgNVBAMMCnNlbDItMi5jb20x\n" +
                            "DTALBgNVBAcMBEdlbnQxEjAQBgNVBAoMCW92bzE5NzYzNTEYMBYGA1UECAwPT29z\n" +
                            "dCBWbGFhbmRlcmVuMRIwEAYDVQQLDAlvdm83NjU4NDgwggEiMA0GCSqGSIb3DQEB\n" +
                            "AQUAA4IBDwAwggEKAoIBAQC6x6uKJ3lpDdqocbozPKzE/DBBIcZZw9dCf5izuVQb\n" +
                            "Wl5ssLU4rtyLgCupJxeNX35Nl6FKVk5m3huRCJ1Qq5GUrLqDb1kjPiXCPhK59iM5\n" +
                            "g56JA3e11rnyTFMHD30PLmkrpeyB+jF+d1O997N/58edp2NMQaQYpsnpLDnFCw02\n" +
                            "t/4cglVTeVpb41EhZ4fecWfGsjxfcr9UrOoJ3KdWST65ZdhOQYzb4d6TPtbkuQfG\n" +
                            "jddf5W+j2YV//qfNJgkyIVIyfwuwZnhMa/Ak/A9Hc8Y26HcGRbwKASbc2M/urVnM\n" +
                            "GTgW9Tz51qTYdsbiGfVJmPVhcDPSkZ5e8EbA9PkpFKj/AgMBAAGgADANBgkqhkiG\n" +
                            "9w0BAQsFAAOCAQEACiqGRdUgRRHuTBqc2Y5Gba3NJSFlqcsINFOh4XnygpUeUkr2\n" +
                            "94roqARgIbTjQWK+fTZSlPDNmm6iexAF0vOu8YRNMwR9GNlzAd+orXGrp2Sz1pBq\n" +
                            "3kdt0CN4Y2k0JLiQXwfxYkIvX7QRpkUxwHVYVIhK+rVbZYNDL5Jjgt8RjYQuolaH\n" +
                            "ibuRKzakOqTvl2GLZSkWI26DWyJGzMiAqoifVbVj6UZH1MZ7BWeind4VmmVeeiRx\n" +
                            "AW2ioA8yZvECsTMmZlNREUeoDGKnuzI/9Hr5JpLQrKVeUeEUs1tFuX48mPb8WbzS\n" +
                            "f2lYUcFX9rqP11RP166zorUlcWTt4FoJVMbPdA==\n" +
                            "-----END CERTIFICATE REQUEST-----\n"
            );
        }
    }
}
