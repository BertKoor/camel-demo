/*
 * Copyright 2017 Ivo Woltring <WebMaster@ivonet.nl>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.bertkoor.route.file;

import lombok.extern.slf4j.Slf4j;
import nl.bertkoor.context.CamelDemoContext;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

/**
 * Exercise 1:
 *
 * In this exercise you will familiarize yourself with a basic Camel functionality:
 * <a href="http://camel.apache.org/file2.html">File handling</a>
 * <a href="http://camel.apache.org/simple.html">Simple Expression Language</a>
 *
 * @author Ivo Woltring
 */
@Slf4j
//@Component
public class FileCopyRoute extends RouteBuilder {

    private final CamelDemoContext context;

    @Autowired
    public FileCopyRoute(final CamelDemoContext context) {
        this.context = context;
    }

    @Override
    public void configure() throws Exception {
        final String projectBaseLocation = this.context.projectBaseLocation();
        final String name = this.getClass().getSimpleName();

        Predicate isXml = this.header("CamelFileName").regex(".*\\.(xml|XML)$");
        Predicate isTxt = this.header("CamelFileName").regex(".*\\.(txt|TXT)$");

        from(format("file://%s/test-data/startingPoint/?noop=true", projectBaseLocation))
                .routeId(name)


// Now copy all xml files from the 'from' location to the <projectBaseLocation>/target/xml folder
// The from location is already provided in this exercise and the route is also given an id.
// Copy all text files to <projectBaseLocation>/target/txt folder
// also log what you are doing and try to use the 'simple' language
// hint: header("CamelFileName")

        //implement here...
                .log("Processing ${header.CamelFileName}")
                .to("direct:process")
                .log("--- copied to ${header.CamelFileNameProduced}")
        ;

        from("direct:process")
                .choice()
                    .when(isXml)
                        .to("direct:xml")
                    .when(isTxt)
                        .to("direct:txt")
                    .otherwise().log("... is unknown")
                .endChoice();

        from("direct:xml")
                .log("... is xml")
                .to("file:target/xml");

        from("direct:txt")
                .log("... is txt")
                .to("file:target/txt");
// (Bonus) Question(s):
// - what would you need to change to make this copy into a move?
    // from("...?noop=true --> ?delete=true
    }
}
