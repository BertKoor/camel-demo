package nl.bertkoor;

import nl.bertkoor.model.TeamMember;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.stereotype.Component;
import static nl.bertkoor.service.MemberService.MEMBER_SERVICE;

@Component
public class RestRouteBuilder extends RouteBuilderWithRestExceptionHandling {

    @Override
    public void configure() {
        this.configPing();
        this.configTeamMember();
        this.configSelfSigned();
    }

    private void configPing() {
        rest("/ping").get()
                .route().routeId("ping")
                .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
                .setBody(constant("Pong!"));
    }

    private void configSelfSigned() {
        rest("/selfsigned").get()
                .route().routeId("selfsigned")
                .setHeader(Exchange.HTTP_URI, constant("https://self-signed.badssl.com/"))
                .to("https4:get")
                .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
                .setBody(constant("HTTPS connection to self-signed.baddssl.com is OK !"));
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
