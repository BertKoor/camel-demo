package nl.bertkoor;

import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RestServlet extends RouteBuilderWithRestExceptionHandling {

    @Value("${camel.springboot.name}")
    String servletName;

    public static final String ERROR_URI = "direct:error";

    @Autowired
    private SslConfigFactory sslConfigFactory;

    @Override
    public void configure() {
        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json_xml)
                .dataFormatProperty("prettyPrint", "true")
                .apiContextPath("/api-doc") // also /api-doc/swagger.yaml
                .apiProperty("api.version", "1.0")
                .apiProperty("api.title", "camel-demo")
                .apiProperty("api.description", "This is a RESTful demo application with Apache Camel under Spring Boot")
                .apiProperty("api.contact.name", "BertKoor");

        from(ERROR_URI)
                .transform().simple("${exception.message}");
    }

    @Bean
    ServletRegistrationBean camelServlet() {
        // use a @Bean to register the Camel servlet which we need to do
        // because we want to use the camel-servlet component for the Camel REST service
        ServletRegistrationBean mapping = new ServletRegistrationBean();
        mapping.setName(servletName);
        mapping.setLoadOnStartup(1);
        // CamelHttpTransportServlet is the name of the Camel servlet to use
        mapping.setServlet(new CamelHttpTransportServlet());
        mapping.addUrlMappings("/*");
        return mapping;
    }

}
