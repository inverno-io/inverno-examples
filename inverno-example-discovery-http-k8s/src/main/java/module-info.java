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
 * Inverno HTTP service Discovery Kubernetes example module.
 * </p>
 *
 * @author <a href="jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@io.inverno.core.annotation.Module
module io.inverno.example.app_discovery_http_k8s {
	requires io.inverno.mod.boot;
	requires io.inverno.mod.configuration;
	requires io.inverno.mod.discovery.http;
	requires io.inverno.mod.discovery.http.k8s;
	requires io.inverno.mod.http.client;
	requires io.inverno.mod.web.client;
	requires io.inverno.mod.web.server;

	requires org.apache.logging.log4j;
	requires org.apache.commons.lang3;
}
