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

import examples.GroupHelloReply;
import examples.GroupHelloRequest;
import examples.HelloServiceGrpcRoutesConfigurer;
import examples.SingleHelloReply;
import examples.SingleHelloRequest;
import io.inverno.core.annotation.Bean;
import io.inverno.mod.grpc.server.GrpcServer;
import io.inverno.mod.http.base.ExchangeContext;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * <p>
 * {@code HelloService} implementation.
 * </p>
 * 
 * @author <a href="jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Bean( visibility = Bean.Visibility.PRIVATE )
public class HelloServiceController extends HelloServiceGrpcRoutesConfigurer<ExchangeContext> {

	public HelloServiceController(GrpcServer grpcServer) {
		super(grpcServer);
	}

	@Override
	public Mono<SingleHelloReply> sayHello(SingleHelloRequest request) {
		return Mono.just(SingleHelloReply.newBuilder().setMessage("Hello " + request.getName()).build());
	}

	@Override
	public Mono<GroupHelloReply> sayHelloToEverybody(Publisher<SingleHelloRequest> request) {
		// Aggregate then process
		/*return Flux.from(request)
			.map(SingleHelloRequest::getName)
			.collectList()
			.map(names -> GroupHelloReply.newBuilder().setMessage("Hello ").addAllNames(names).build());*/
		
		// Aggregate and process
		return Flux.from(request)
			.reduceWith(
				() -> GroupHelloReply.newBuilder().setMessage("Hello "), 
				(reply, helloRequest) -> reply.addNames(helloRequest.getName())
			)
			.map(GroupHelloReply.Builder::build);
	}

	@Override
	public Publisher<SingleHelloReply> sayHelloToEveryone(Publisher<SingleHelloRequest> request) {
		return Flux.from(request)
			.map(SingleHelloRequest::getName)
			.map(name -> SingleHelloReply.newBuilder().setMessage("Hello " + name).build());
	}

	@Override
	public Publisher<SingleHelloReply> sayHelloToEveryoneInTheGroup(GroupHelloRequest request) {
		return Flux.fromIterable(request.getNamesList())
			.map(name -> SingleHelloReply.newBuilder().setMessage("Hello " + name).build());
	}

	// Cancel on timeout
	/*@Override
	public void sayHelloToEveryoneInTheGroup(GrpcExchange.ServerStreaming<ExchangeContext, GroupHelloRequest, SingleHelloReply> grpcExchange) {
		Duration timeout = grpcExchange.request().metadata().getTimeout().orElse(Duration.ofSeconds(20));
		grpcExchange.response().stream(Flux.interval(Duration.ofSeconds(1))
			.map(index -> SingleHelloReply.newBuilder().setMessage("Hello " + index).build())
			.doOnCancel(grpcExchange::cancel)
			.take(timeout)
		);
	}*/
	
	@Override
	public Publisher<SingleHelloReply> sayHelloToEveryoneInTheGroups(Publisher<GroupHelloRequest> request) {
		return Flux.from(request)
			.map(GroupHelloRequest::getNamesList)
			.flatMap(Flux::fromIterable)
			.map(name -> SingleHelloReply.newBuilder().setMessage("Hello " + name).build());
	}
}
