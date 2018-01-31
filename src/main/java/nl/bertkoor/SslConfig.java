package nl.bertkoor;

import org.apache.camel.CamelContext;
import org.apache.camel.SSLContextParametersAware;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.camel.util.jsse.SSLContextParameters;
import org.apache.camel.util.jsse.TrustManagersParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class SslConfig {

//    @Value("trust.store")
    String trustStoreFileName = "/myTrustStore.jks";

//    @Value("trust.pwd")
    String trustStorePwd = "secret";

    private final CamelContext camelContext;

    public SslConfig(@Autowired CamelContext context) {
        this.camelContext = context;

        SSLContextParameters params = this.buildSSLContextParameters();
        context.setSSLContextParameters(params);

        // alas the Components don't exist yet at the moment of invocation, we have to ask for them explicitly
        String[] sslAwareComponentNames = {"http4"};
        for (String componentName: sslAwareComponentNames) {
            SSLContextParametersAware component = (SSLContextParametersAware)context.getComponent(componentName);
            component.setUseGlobalSslContextParameters(true);
        }
    }

    private SSLContextParameters buildSSLContextParameters() {
        KeyStoreParameters trustStore = new KeyStoreParameters();
        trustStore.setResource(trustStoreFileName);
        trustStore.setPassword(trustStorePwd);

        TrustManagersParameters trustMgr = new TrustManagersParameters();
        trustMgr.setKeyStore(trustStore);

        SSLContextParameters sslCtxParms = new SSLContextParameters();
        sslCtxParms.setTrustManagers(trustMgr);
        return sslCtxParms;
    }

    private String getResourceFileName(Resource resource) {
        try {
            return resource.getFile().getPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
