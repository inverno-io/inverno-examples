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
package io.inverno.example.app_grpc_client;

import java.io.IOException;
import java.util.function.Supplier;

import examples.GreeterGrpcClient;
import examples.GroupHelloRequest;
import examples.HelloServiceGrpcClient;
import examples.HelloRequest;
import examples.SingleHelloRequest;
import io.inverno.core.annotation.Bean;
import io.inverno.core.v1.Application;
import io.inverno.mod.configuration.ConfigurationSource;
import io.inverno.mod.configuration.source.BootstrapConfigurationSource;
import io.inverno.mod.http.base.ExchangeContext;
import java.net.InetSocketAddress;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Flux;

/**
 * <p>
 * 
 * </p>
 * 
 * @author <a href="jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
public class Main {

	private static final Logger LOGGER = LogManager.getLogger(Main.class);
	
	@Bean
	public static interface App_grpc_clientConfigurationSource extends Supplier<ConfigurationSource<?, ?, ?>> {}

	public static void main(String[] args) throws IOException {
		BootstrapConfigurationSource bootstrapConfigurationSource = new BootstrapConfigurationSource(Main.class.getModule(), args);
				
		App_grpc_client app = Application.run(new App_grpc_client.Builder()
			.setApp_grpc_clientConfigurationSource(bootstrapConfigurationSource)
		);
		
		InetSocketAddress socketAddress = bootstrapConfigurationSource.get("target")
			.execute().single()
			.map(result -> result.getResult().flatMap(property -> property.asInetSocketAddress()).orElse(new InetSocketAddress("localhost", 8080)))
			.block();
		
		try {
			try(GreeterGrpcClient.Stub<ExchangeContext> stub = app.greeterGrpcClient().createStub(socketAddress)) {
				LOGGER.info("Calling {}", GreeterGrpcClient.SERVICE_NAME.methodPath("SayHello"));
				stub
					.sayHello(HelloRequest.newBuilder()
						.setName("Bill")
						.build()
					)
					.doOnNext(reply -> LOGGER.info("Received: {}", reply.getMessage()))
					.block();
			}
		
			try(HelloServiceGrpcClient.Stub<ExchangeContext> stub = app.helloServiceGrpcClient().createStub(socketAddress)) {
				LOGGER.info("Calling {}", HelloServiceGrpcClient.SERVICE_NAME.methodPath("SayHello"));
				stub.sayHello(SingleHelloRequest.newBuilder()
						.setName("Bob")
						.build()
					)
					.doOnNext(reply -> LOGGER.info("Received: {}", reply.getMessage()))
					.block();
				
				LOGGER.info("Calling {}", HelloServiceGrpcClient.SERVICE_NAME.methodPath("SayHelloToEverybody"));
				stub.sayHelloToEverybody(Flux.just(
						SingleHelloRequest.newBuilder()
							.setName("Bob")
							.build(),
						SingleHelloRequest.newBuilder()
							.setName("Alice")
							.build(),
						SingleHelloRequest.newBuilder()
							.setName("John")
							.build()
					))
					.doOnNext(reply -> LOGGER.info("Received: {}", reply.getMessage() + reply.getNamesList().stream().collect(Collectors.joining(", ")) ))
					.block();
				
				LOGGER.info("Calling {}", HelloServiceGrpcClient.SERVICE_NAME.methodPath("SayHelloToEveryone"));
				Flux.from(stub.sayHelloToEveryone(Flux.just(
						SingleHelloRequest.newBuilder()
							.setName("Bob")
							.build(),
						SingleHelloRequest.newBuilder()
							.setName("Alice")
							.build(),
						SingleHelloRequest.newBuilder()
							.setName("John")
							.build()
					)))
					.doOnNext(reply -> LOGGER.info("Received: {}", reply.getMessage()))
					.blockLast();
				
				LOGGER.info("Calling {}", HelloServiceGrpcClient.SERVICE_NAME.methodPath("SayHelloToEveryoneInTheGroup"));
				Flux.from(stub.sayHelloToEveryoneInTheGroup(GroupHelloRequest.newBuilder()
						.addNames("Bob")
						.addNames("Alice")
						.addNames("John")
						.build()
					))
					.doOnNext(reply -> LOGGER.info("Received: {}", reply.getMessage()))
					.blockLast();
				
				LOGGER.info("Calling {}", HelloServiceGrpcClient.SERVICE_NAME.methodPath("SayHelloToEveryoneInTheGroups"));
				Flux.from(stub.sayHelloToEveryoneInTheGroups(Flux.just(
						GroupHelloRequest.newBuilder()
							.addNames("Bob")
							.addNames("Alice")
							.build(),
						GroupHelloRequest.newBuilder()
							.addNames("John")
							.addNames("Bill")
							.build()
					)))
					.doOnNext(reply -> LOGGER.info("Received: {}", reply.getMessage()))
					.blockLast();
			}
		}
		finally {
			app.stop();
		}
	}
}
