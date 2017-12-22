package nl.bertkoor.route;

import lombok.extern.slf4j.Slf4j;
import nl.bertkoor.model.TeamMember;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import static nl.bertkoor.service.MemberService.MEMBER_SERVICE;

@Slf4j
@Component
public class RestRoute extends RouteBuilder {

    @Value("${camel.springboot.name}")
    String servletName;

    @Override
    public void configure() throws Exception {
        this.configRestServer();
        this.configPing();
        this.configTeamMember();
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

    private void configRestServer() {
        // https://github.com/camelinaction/camelinaction2/blob/master/chapter10/undertow-swagger/src/main/java/camelinaction/OrderRoute.java
        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json_xml)
                .dataFormatProperty("prettyPrint", "true")
                .apiContextPath("/api-doc") // also /api-doc/swagger.yaml
                .apiProperty("api.version", "1.0")
                .apiProperty("api.title", "camel-demo")
                .apiProperty("api.description", "This is a RESTful demo application with Apache Camel under Spring Boot")
                .apiProperty("api.contact.name", "The Commitments");

        onException(Exception.class)
                .log(LoggingLevel.ERROR, "${exception.stacktrace}")
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .setBody(constant(""));
    }

    private void configPing() {
        rest("/ping")
                .get()
                    .route().routeId("ping")
                    .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
                    .setBody(constant("Pong!"));
    }

    private void configTeamMember() {
        final String MEMBER = "direct:teammember";
        final String PARAM_NAME = "name";

        rest("/teamMember")
                .get()
                    .outType(TeamMember.class)
                    .description("Find team member by name")
                    .param()
                        .name(PARAM_NAME)
                        .type(RestParamType.query)
                        .description("Team member name")
                        .dataType("string")
                    .endParam()
                .route().routeId(MEMBER)
                    .to(MEMBER);

        Predicate isMemberRequestOK = this.header(PARAM_NAME).isNotNull();

        from(MEMBER)
                .choice()
                    .when(isMemberRequestOK)
                        .to("bean:" + MEMBER_SERVICE + "?method=getMember(header." + PARAM_NAME + ")")
                    .otherwise()
                        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                        .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
                        .transform().constant("Required parameter 'foobar' is missing")
                .endChoice();
    }
}
