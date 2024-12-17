/*
 * Copyright 2022 Jeremy KUHN
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

/**
 * <p>
 * Inverno HTTP service Discovery example module.
 * </p>
 *
 * @author <a href="jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@io.inverno.core.annotation.Module
@io.inverno.core.annotation.Wire(beans="io.inverno.example.app_discovery_http:appConfiguration.http_client", into="io.inverno.example.app_discovery_http:discoveryHttpClient:httpClientConfiguration")
module io.inverno.example.app_discovery_http {
	requires io.inverno.mod.boot;
	requires io.inverno.mod.configuration;
	requires io.inverno.mod.discovery.http;
	requires io.inverno.mod.discovery.http.meta;
	requires io.inverno.mod.http.client;

	requires com.fasterxml.jackson.databind;
	requires info.picocli;
	requires org.apache.commons.lang3;
	requires org.apache.logging.log4j;

	opens io.inverno.example.app_discovery_http to info.picocli, com.fasterxml.jackson.databind;
}
