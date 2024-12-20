/*
 * Copyright 2024 Jeremy Kuhn
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
package io.inverno.example.app_grpc_server;

import io.inverno.core.annotation.NestedBean;
import io.inverno.mod.configuration.Configuration;
import io.inverno.mod.grpc.server.GrpcServerConfiguration;
import io.inverno.mod.web.server.WebServerConfiguration;

/**
 * <p>
 * The application configuration providing gRPC server and Web server configurations.
 * </p>
 * 
 * @author <a href="jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Configuration
public interface AppConfiguration {

	@NestedBean
	GrpcServerConfiguration grpc_server();
	
	@NestedBean
	WebServerConfiguration web_server();
}