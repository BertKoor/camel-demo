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

package nl.bertkoor.context;

import org.springframework.stereotype.Component;

import java.io.File;

/**
 * To be injected where we need context stuff.
 *
 * @author Ivo Woltring
 */
@Component
public class CamelDemoContext {

    /**
     * To determine what the location of this project is on your machine.
     * This way it eliminates the need of providing a property for it.
     *
     * @return base path to this project as a String
     */
    public String projectBaseLocation() {
        String abspath = new File(".").getAbsolutePath();
        abspath = abspath.substring(0, abspath.length() - 1);
        return new File(abspath).getAbsolutePath();
    }
}
