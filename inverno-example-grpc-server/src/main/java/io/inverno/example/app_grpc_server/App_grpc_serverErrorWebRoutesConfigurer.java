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

import io.inverno.core.annotation.Bean;
import io.inverno.core.annotation.Bean.Visibility;
import io.inverno.mod.base.resource.MediaTypes;
import io.inverno.mod.grpc.server.GrpcServer;
import io.inverno.mod.http.base.ExchangeContext;
import io.inverno.mod.web.server.ErrorWebRoutable;
import io.inverno.mod.web.server.ErrorWebRoutesConfigurer;

/**
 * 
 * @author <a href="jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Bean(visibility = Visibility.PRIVATE)
public class App_grpc_serverErrorWebRoutesConfigurer implements ErrorWebRoutesConfigurer<ExchangeContext> {
	
	private final GrpcServer grpcServer;
	
	public App_grpc_serverErrorWebRoutesConfigurer(GrpcServer grpcServer) {
		this.grpcServer = grpcServer;
	}
	
	@Override
	public void configure(ErrorWebRoutable<ExchangeContext, ?> errorRoutes) {
		errorRoutes
			.route()
			.consumes(MediaTypes.APPLICATION_GRPC)
			.consumes(MediaTypes.APPLICATION_GRPC_JSON)
			.consumes(MediaTypes.APPLICATION_GRPC_PROTO)
			.handler(this.grpcServer.errorHandler());
	}
}
