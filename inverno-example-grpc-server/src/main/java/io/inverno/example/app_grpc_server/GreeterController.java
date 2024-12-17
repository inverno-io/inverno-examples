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

import examples.GreeterGrpcRoutesConfigurer;
import examples.HelloReply;
import examples.HelloRequest;
import io.inverno.core.annotation.Bean;
import io.inverno.core.annotation.Bean.Visibility;
import io.inverno.mod.grpc.server.GrpcServer;
import io.inverno.mod.http.base.ExchangeContext;
import reactor.core.publisher.Mono;

/**
 * <p>
 * Implementation of gRPC {@code Greeter} service specified in {@code src/main/proto/helloworld/helloworld.proto}.
 * </p>
 *
 * <p>
 * This is the official gRPC <a href="https://github.com/grpc/grpc-java/blob/master/examples/src/main/proto/helloworld.proto">helloworld example</a>.
 * </p>
 * 
 * @author <a href="jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Bean( visibility = Visibility.PRIVATE )
public class GreeterController extends GreeterGrpcRoutesConfigurer<ExchangeContext> {

	public GreeterController(GrpcServer grpcServer) {
		super(grpcServer);
	}

	@Override
	public Mono<HelloReply> sayHello(HelloRequest request) {
		return Mono.just(HelloReply.newBuilder().setMessage("Hello " + request.getName()).build());
	}
}
