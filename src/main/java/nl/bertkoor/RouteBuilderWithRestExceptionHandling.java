package nl.bertkoor;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.http.HttpStatus;

import javax.xml.bind.UnmarshalException;

/**
 * Each instance of a <code>RouteBuilder</code> needs to declare <code>onException</code> clauses.
 * See http://camel.apache.org/exception-clause.html
 * So all our REST routeBuilders need to extend from this class.
 */
public abstract class RouteBuilderWithRestExceptionHandling extends RouteBuilder {

    @Override
    public void configure() {
        onException(UnrecognizedPropertyException.class, UnmarshalException.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.SC_BAD_REQUEST))
                .log(LoggingLevel.ERROR, "Bad request: ${exception.message}")
                .to(RestServlet.ERROR_URI);

        onException(Exception.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.SC_INTERNAL_SERVER_ERROR))
                .log(LoggingLevel.ERROR, "Error: ${exception.stacktrace}")
                .to(RestServlet.ERROR_URI);
    }

}
