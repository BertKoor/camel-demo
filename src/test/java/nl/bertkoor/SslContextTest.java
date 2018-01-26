package nl.bertkoor;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.SimpleBuilder;
import org.apache.camel.component.http4.HttpComponent;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.camel.util.jsse.SSLContextParameters;
import org.apache.camel.util.jsse.TrustManagersParameters;
import org.junit.Before;
import org.junit.Test;

import javax.net.ssl.SSLContext;

import static org.junit.Assert.assertTrue;

public class SslContextTest extends CamelTestSupport {

    @Produce(uri = "direct:start")
    private ProducerTemplate producer;

    @EndpointInject(uri = "mock:result")
    private MockEndpoint resultEndpoint;

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("direct:start")
                        .setHeader(Exchange.HTTP_URI, constant("https://self-signed.badssl.com/"))
                        .to("http4:get")
                        .to("mock:result");

                SSLContextParameters sslCtxParms = new RestServlet().buildSSLContextParameters();
                CamelContext camelContext = this.getContext();
                camelContext.setSSLContextParameters(sslCtxParms);
                camelContext.getComponent("http4", HttpComponent.class).setUseGlobalSslContextParameters(true);
            }
        };
    }

    @Test
    public void testSetupSslContext() throws Exception {
        SSLContextParameters sslCtxParms = new RestServlet().buildSSLContextParameters();
        SSLContext sslContext = sslCtxParms.createSSLContext(this.context());
        assertNotNull(sslContext);
    }

    @Test // optional run with -Djavax.net.debug=ssl
    public void test_GetSelfSignedSSL() throws Exception {
        resultEndpoint.expectedMessageCount(1);
        resultEndpoint.allMessages().predicate(SimpleBuilder.simple("${header." + Exchange.HTTP_RESPONSE_CODE + "} == 200"));
        producer.sendBody(null);
        resultEndpoint.assertIsSatisfied();
    }

}