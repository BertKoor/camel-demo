package nl.bertkoor;

import org.apache.camel.CamelContext;
import org.apache.camel.SSLContextParametersAware;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.camel.util.jsse.SSLContextParameters;
import org.apache.camel.util.jsse.TrustManagersParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SslConfigFactory {

    @Value("${trust.store}")
    private String trustStoreFileName;

    @Value("${trust.pwd}")
    private String trustStorePwd;

    @Autowired
    CamelContext camelContext;

    @Bean
    public SslConfigFactory buildSslConfig() {
        SSLContextParameters sslCtxParms = this.buildSSLContextParameters();
        camelContext.setSSLContextParameters(sslCtxParms);
        this.setSslConfigForComponents("https4");
        return this;
    }

    public void setSslConfigForComponents(String ... componentNames) {
        for (String componentName: componentNames) {
            SSLContextParametersAware component = (SSLContextParametersAware)camelContext.getComponent(componentName);
            component.setUseGlobalSslContextParameters(true);
        }
    }

    private SSLContextParameters buildSSLContextParameters() {
        KeyStoreParameters trustStore = new KeyStoreParameters();
        trustStore.setResource(this.trustStoreFileName);
        trustStore.setPassword(this.trustStorePwd);

        TrustManagersParameters trustMgr = new TrustManagersParameters();
        trustMgr.setKeyStore(trustStore);

        SSLContextParameters sslCtxParms = new SSLContextParameters();
        sslCtxParms.setTrustManagers(trustMgr);

        return sslCtxParms;
    }

}
