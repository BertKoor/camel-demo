package nl.bertkoor;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.SimpleBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = {SslContextTest.CamelSpringTestConfig.class, SslConfigFactory.class})
@TestPropertySource(properties = {"trust.pwd = secret", "trust.store = /myTrustStore.jks"})
public class SslContextTest {

    @Produce(uri = "direct:start")
    private ProducerTemplate producer;

    @EndpointInject(uri = "mock:result")
    private MockEndpoint resultEndpoint;

    @TestConfiguration
    public static class CamelSpringTestConfig extends SingleRouteCamelConfiguration {

        @Override
        public RouteBuilder route() {
            return new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("direct:start")
                            .to("https4://self-signed.badssl.com/")
                            .to("mock:result");
                }
            };
        }
    }

    @Test // optional run with -Djavax.net.debug=ssl
    public void test_GetSelfSignedSSL() throws Exception {
        resultEndpoint.expectedMessageCount(1);
        resultEndpoint.allMessages().predicate(SimpleBuilder.simple("${header." + Exchange.HTTP_RESPONSE_CODE + "} == 200"));
        producer.sendBody(null);
        resultEndpoint.assertIsSatisfied();
    }

}
