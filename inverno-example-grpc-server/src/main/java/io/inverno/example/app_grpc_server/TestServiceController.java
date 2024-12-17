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

import com.google.protobuf.ByteString;
import io.grpc.testing.integration.EmptyProtos;
import io.grpc.testing.integration.Messages;
import io.grpc.testing.integration.TestServiceGrpcRoutesConfigurer;
import io.inverno.mod.grpc.base.GrpcException;
import io.inverno.mod.grpc.server.GrpcExchange;
import io.inverno.mod.grpc.server.GrpcServer;
import io.inverno.mod.http.base.ExchangeContext;
import java.util.Random;
import reactor.core.publisher.Mono;

/**
 * <p>
 * Work-in-progress implementation of gRPC <a href="https://github.com/grpc/grpc-java/blob/master/interop-testing/src/main/java/io/grpc/testing/integration/TestServiceImpl.java">test service</a>.
 * </p>
 * 
 * @author <a href="jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
//@Bean( visibility = Visibility.PRIVATE )
public class TestServiceController extends TestServiceGrpcRoutesConfigurer<ExchangeContext> {

	private final Random random;
	
	private final ByteString compressableBuffer;
	
	public TestServiceController(GrpcServer grpcServer) {
		super(grpcServer);
		this.random = new Random();
		this.compressableBuffer = ByteString.copyFrom(new byte[1024]);
	}

	@Override
	public Mono<EmptyProtos.Empty> emptyCall(EmptyProtos.Empty request) {
		return Mono.just(EmptyProtos.Empty.getDefaultInstance());
	}

	@Override
	public void unaryCall(GrpcExchange.Unary<ExchangeContext, Messages.SimpleRequest, Messages.SimpleResponse> exchange) {
		exchange.response().value(
			exchange.request().value()
				.map(request -> {
					Messages.SimpleResponse.Builder responseBuilder = Messages.SimpleResponse.newBuilder();
					if(request.hasResponseCompressed() && request.getResponseCompressed().getValue()) {
						// how to activate compression for a single message only?
						exchange.response().metadata(metadata -> metadata.messageEncoding("gzip"));
					}
					else {
						exchange.response().metadata(metadata -> metadata.messageEncoding("identity"));
					}

					if(request.getResponseSize() != 0) {
						// For consistency with the c++ TestServiceImpl, use a random offset for unary calls.
						// TODO(wonderfly): whether or not this is a good approach needs further discussion.
						int offset = this.random.nextInt(this.compressableBuffer.size());
						ByteString payload = this.generatePayload(compressableBuffer, offset, request.getResponseSize());
						responseBuilder.setPayload(Messages.Payload.newBuilder().setBody(payload));
					}

					if(request.hasResponseStatus()) {
						throw new GrpcException(request.getResponseStatus().getCode(), request.getResponseStatus().getMessage());
					}

					return responseBuilder.build();
				})
		);
	}

	/**
	 * Generates a payload of desired type and size. Reads compressableBuffer or uncompressableBuffer as a circular buffer.
	 */
	private ByteString generatePayload(ByteString dataBuffer, int offset, int size) {
		ByteString payload = ByteString.EMPTY;
		// This offset would never pass the array boundary.
		int begin = offset;
		int end = 0;
		int bytesLeft = size;
		while (bytesLeft > 0) {
			end = Math.min(begin + bytesLeft, dataBuffer.size());
			// ByteString.substring returns the substring from begin, inclusive, to end, exclusive.
			payload = payload.concat(dataBuffer.substring(begin, end));
			bytesLeft -= (end - begin);
			begin = end % dataBuffer.size();
		}
		return payload;
	}
}
