import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;

public class SSLServerContext {

    private final SSLContext sslContext;

    public SSLServerContext(String keyStoreFile, String keyStorePassword, String trustStoreFile, String trustStorePassword) throws Exception {
        // Load the key store
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream("./Juan.jks"), "Ruggieri2733".toCharArray());

        // Load the trust store
        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(new FileInputStream("./juan_public_cert.cer"), "Ruggieri2733".toCharArray());

        // Set up key manager factory to use our key store
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

        // Set up trust manager factory to use our trust store
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(trustStore);

        // Initialize SSL context
        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
    }

    public SSLContext getSSLContext() {
        return sslContext;
    }

}
