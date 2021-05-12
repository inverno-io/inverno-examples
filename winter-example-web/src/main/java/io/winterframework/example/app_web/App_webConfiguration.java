/*
 * Copyright 2021 Jeremy KUHN
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.winterframework.example.app_web;

import java.net.URI;
import java.nio.file.Paths;

import io.winterframework.core.annotation.NestedBean;
import io.winterframework.mod.boot.NetConfiguration;
import io.winterframework.mod.configuration.Configuration;
import io.winterframework.mod.web.WebConfiguration;

/**
 * 
 * @author <a href="mailto:jeremy.kuhn@winterframework.io">Jeremy Kuhn</a>
 *
 */
@Configuration
public interface App_webConfiguration {

	default URI web_root() {
		return Paths.get("web-root/").toUri();
	}
	
    @NestedBean
    NetConfiguration net();

    @NestedBean
    WebConfiguration web();
}