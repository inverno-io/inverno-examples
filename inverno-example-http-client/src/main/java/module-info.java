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
 *
 * @author <a href="jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@io.inverno.core.annotation.Module
module io.inverno.example.app_http_client {
	requires io.inverno.mod.boot;
	requires io.inverno.mod.http.client;
	
	requires java.logging;
	requires org.apache.logging.log4j;

	requires org.jline;
	requires info.picocli;

	opens io.inverno.example.app_http_client to info.picocli;
	opens io.inverno.example.app_http_client.internal.command to info.picocli;
	opens io.inverno.example.app_http_client.internal.picocli to info.picocli;
}
