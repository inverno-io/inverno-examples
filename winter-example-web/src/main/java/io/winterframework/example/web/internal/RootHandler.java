/*
 * Copyright 2020 Jeremy KUHN
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
package io.winterframework.example.web.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalTime;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.util.AsciiString;
import io.winterframework.core.annotation.Bean;
import io.winterframework.core.annotation.Bean.Visibility;
import io.winterframework.core.annotation.Wrapper;
import io.winterframework.example.web.ServerConfiguration;
import io.winterframework.mod.commons.resource.ResourceException;
import io.winterframework.mod.commons.resource.ResourceService;
import io.winterframework.mod.web.Charsets;
import io.winterframework.mod.web.Exchange;
import io.winterframework.mod.web.ExchangeHandler;
import io.winterframework.mod.web.Headers;
import io.winterframework.mod.web.Method;
import io.winterframework.mod.web.Parameter;
import io.winterframework.mod.web.RequestBody;
import io.winterframework.mod.web.Response;
import io.winterframework.mod.web.ResponseBody;
import io.winterframework.mod.web.ServiceUnavailableException;
import io.winterframework.mod.web.handler.StaticHandler;
import io.winterframework.mod.web.router.Router;
import io.winterframework.mod.web.router.WebExchange;
import io.winterframework.mod.web.router.WebRouter;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author jkuhn
 *
 */
@Bean(visibility = Visibility.PRIVATE)
@Wrapper
public class RootHandler implements Supplier<ExchangeHandler<RequestBody, ResponseBody, Exchange<RequestBody, ResponseBody>>> {

	private ServerConfiguration configuration;
	
	private ResourceService resourceService;
	
	public RootHandler(ServerConfiguration configuration, ResourceService resourceService) {
		this.configuration = configuration;
		this.resourceService = resourceService;
	}
	
	@Override
	public ExchangeHandler<RequestBody, ResponseBody, Exchange<RequestBody, ResponseBody>> get() {
		return plaintext();
//		return plaintextRouter();
//		return configuration4();
	}
	
	private static final byte[] STATIC_PLAINTEXT = "Hello, World!".getBytes(Charsets.UTF_8);
	private static final ByteBuf STATIC_PLAINTEXT_BYTEBUF;
	private static final int STATIC_PLAINTEXT_LEN = STATIC_PLAINTEXT.length;

	private static final AsciiString STATIC_SERVER = AsciiString.cached("winter");
	
	static {
		ByteBuf tmpBuf = Unpooled.directBuffer(STATIC_PLAINTEXT_LEN);
		tmpBuf.writeBytes(STATIC_PLAINTEXT);
		STATIC_PLAINTEXT_BYTEBUF = Unpooled.unreleasableBuffer(tmpBuf);
	}
	
	private static final CharSequence PLAINTEXT_CLHEADER_VALUE = AsciiString.cached(String.valueOf(STATIC_PLAINTEXT_LEN));
	
	private static final Mono<ByteBuf> PLAIN_TEXT_MONO = Mono.just(STATIC_PLAINTEXT_BYTEBUF);
	
	private ExchangeHandler<RequestBody, ResponseBody, Exchange<RequestBody, ResponseBody>> plaintext() {
		return exchange -> {
			exchange.response().headers(h -> h.add(HttpHeaderNames.CONTENT_LENGTH, PLAINTEXT_CLHEADER_VALUE).add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN).add(HttpHeaderNames.SERVER, STATIC_SERVER)).body().raw().data(PLAIN_TEXT_MONO.map(ByteBuf::duplicate));
		};
	}
	
	private ExchangeHandler<RequestBody, ResponseBody, Exchange<RequestBody, ResponseBody>> plaintextString() {
		return exchange -> {
			exchange.response().headers(h -> h.contentType("text/plain").add("server", "winter")).body().raw().data(PLAIN_TEXT_MONO.map(ByteBuf::duplicate));
		};
	}
	
	private WebRouter<RequestBody, ResponseBody, WebExchange<RequestBody, ResponseBody>> plaintextRouter() {
		return Router.web()
				.route()
					.path("/plaintext", false)
					.method(Method.GET)
					.handler(
						this.plaintext()
					);
	}
	
	private WebRouter<RequestBody, ResponseBody, WebExchange<RequestBody, ResponseBody>> configuration0() {
		return Router.web()
			.route()
				.path("/hello", true)
				.method(Method.GET)
				.handler(
					exchange -> exchange.response()
						.headers(h -> h.contentType("text/plain"))
						.body().raw().data("This is an example server.\n")
				);
	}
	
	private WebRouter<RequestBody, ResponseBody, WebExchange<RequestBody, ResponseBody>> configuration1() {
		return Router.web()
			.route().path("/toto", true).method(Method.GET).handler(simple())
			.route().path("/tata", true).method(Method.POST).handler(echo())
			.route().path("/json", true).method(Method.POST).handler(json(new ObjectMapper()))
			.route().path("/toto/{param1}/tata/{param2}", true).handler(a())
			.route().path("/toto/titi/tata/{param2}", true).handler(b())
			.route().path("/toto/{param1}/tata/titi", true).handler(c());
	}
	
	private WebRouter<RequestBody, ResponseBody, WebExchange<RequestBody, ResponseBody>> configuration2() {
		return Router.web()
			.route().path("/toto", true).method(Method.POST).consumes("application/json").handler(echo())
			.route().path("/toto", true).method(Method.POST).consumes("text/*").handler(printRequest());
	}
	
	private WebRouter<RequestBody, ResponseBody, WebExchange<RequestBody, ResponseBody>> configuration3() {
		WebRouter<RequestBody, ResponseBody, WebExchange<RequestBody, ResponseBody>> router = Router.web()
			.route().path("/toto", true).method(Method.GET).produces("application/json").handler(
				exchange -> {
					exchange.response().headers(headers -> headers.contentType("application/json")).body().raw().data(Mono.just(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("{\"toto\":5}", Charsets.DEFAULT))));
				}
			)
			.route().path("/toto", true).method(Method.GET).produces("text/plain").handler(
				exchange -> {
					exchange.response().headers(headers -> headers.contentType("text/plain")).body().raw().data(Mono.just(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("toto 5", Charsets.DEFAULT))));
				}
			)
			.route().path("/tata", true).method(Method.POST).consumes("application/json").produces("application/json").handler(
					exchange -> {
					exchange.response().headers(headers -> headers.contentType("application/json")).body().raw().data(Mono.just(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("{\"toto\":5}", Charsets.DEFAULT))));
				}
			)
			.route().path("/tutu", true).method(Method.GET).handler(
					exchange -> {
					exchange.response().headers(headers -> headers.contentType("application/json")).body().raw().data(Mono.just(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("{\"toto\":5}", Charsets.DEFAULT))));
				}
			)
			.route().path("/error", true).method(Method.GET).handler(
					exchange -> {
					throw new ServiceUnavailableException(120);
				}
			);
		return router;
	}
	
	private WebRouter<RequestBody, ResponseBody, WebExchange<RequestBody, ResponseBody>> configuration4() {
		try {
			return Router.web()
				.route()
					.path("/static/{.*}", true)
					.method(Method.GET)
					.handler(new StaticHandler(this.resourceService.get(this.configuration.web_root().toUri()), "/static/"));
		} 
		catch (IllegalArgumentException | ResourceException | IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private ExchangeHandler<RequestBody, ResponseBody, Exchange<RequestBody, ResponseBody>> hello() {
		return exchange -> exchange.response()
			.headers(headers -> headers
				.status(200)
				.contentType("text/html; charset=\"UTF-8\"")
			)
			.body().raw().data(
				Mono.just(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("<html><head><title>Winter Web</title></head><body><h1>Hello</h1></body></html>", Charsets.UTF_8)))
			);
	}
	
	private ExchangeHandler<RequestBody, ResponseBody, Exchange<RequestBody, ResponseBody>> simple() {
		return exchange -> exchange.response()
			.headers(headers -> headers
				.status(200)
				.contentType("text/plain; charset=\"UTF-8\"")
				.add("test", "1235")
			)
			.body().raw().data(
				Mono.just(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Server Response : Version - HTTP/2", Charsets.UTF_8)))
			);
	}
	
	private ExchangeHandler<RequestBody, ResponseBody, Exchange<RequestBody, ResponseBody>> echo() {
		return exchange -> exchange.response()
			.headers(headers -> headers.status(200).contentType("text/plain"))
			.body().raw().data(exchange.request().body()
				.map(body -> body.raw().data().doOnNext(chunk -> chunk.retain()))	
				.orElse(Flux.just(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("=== Empty ===", Charsets.UTF_8))))
			);
	}
	
	private ExchangeHandler<RequestBody, ResponseBody, Exchange<RequestBody, ResponseBody>> a() {
		return exchange -> exchange.response()
			.headers(headers -> headers
				.status(200)
				.contentType("text/plain; charset=\"UTF-8\"")
			)
			.body().raw().data(
				Mono.just(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("= A =", Charsets.UTF_8)))
			);
	}
	
	private ExchangeHandler<RequestBody, ResponseBody, Exchange<RequestBody, ResponseBody>> b() {
		return exchange -> exchange.response()
			.headers(headers -> headers
				.status(200)
				.contentType("text/plain; charset=\"UTF-8\"")
			)
			.body().raw().data(
				Mono.just(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("= B =", Charsets.UTF_8)))
			);
	}
	
	private ExchangeHandler<RequestBody, ResponseBody, Exchange<RequestBody, ResponseBody>> c() {
		return exchange -> exchange.response()
			.headers(headers -> headers
				.status(200)
				.contentType("text/plain; charset=\"UTF-8\"")
			)
			.body().raw().data(
				Mono.just(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("= C =", Charsets.UTF_8)))
			);
	}
	
	private ExchangeHandler<RequestBody, ResponseBody, Exchange<RequestBody, ResponseBody>> interval() {
		return exchange -> {
			exchange.response().headers(headers -> {
				headers.status(200)
					.contentType("text/plain; charset=\"UTF-8\"")
					.add("test", "1235");
			});
			
			Charset requestCharset = exchange.request().headers().<Headers.ContentType>getHeader(Headers.CONTENT_TYPE).map(Headers.ContentType::getCharset).orElse(Charsets.DEFAULT);
			exchange.request().body().ifPresentOrElse(
				body ->	body.raw().data().subscribe(
					buffer -> {
						System.out.println("=================================");
				        System.out.println(buffer.toString(requestCharset));
				        System.out.println("=================================");
					},
					ex -> {
						ex.printStackTrace();
					},
					() -> {
						System.out.println("Body complete");
						
							//response.body().empty();						
							//response.body().data(Mono.just(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Server Response : Version - HTTP/2", Charsets.UTF_8))));
						
						exchange.response().body().raw().data(Flux.interval(Duration.ofMillis(500)).map(index -> Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Response " + index + ", \r\n", Charsets.UTF_8))));
						
//						response.body().data().data(Flux.just(
//								Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Response A, \r\n", Charsets.UTF_8)),
//								Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Response B, \r\n", Charsets.UTF_8)),
//								Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Response C, \r\n", Charsets.UTF_8)),
//								Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Response D, \r\n", Charsets.UTF_8)),
//								Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Response E \r\n", Charsets.UTF_8))
//							)
//							.delayElements(Duration.ofMillis(500)) 
//						);
						
						// Why do I loose first message? 
						// We are using one single thread, when next is invoked the chunk is submit to the eventloopgroup whose thread is already busy right here so the chunk is placed in a queue until the thread gets back to the eventloopgroup 
						// This is not good for several reasons:
						// - it blocks the event loop group
						// - potentially we can loose messages if the data flux replay buffer in the response gets full => at least we should have a clear error explaining what went terribly wrong 
//						response.body().data(Flux.create(emitter -> {
//							try {
//								emitter.next(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Response A, \r\n", Charsets.UTF_8)));
//								System.out.println("Emit A");
//								Thread.sleep(1000);
//								emitter.next(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Response B, \r\n", Charsets.UTF_8)));
//								System.out.println("Emit B");
//								Thread.sleep(1000);
//								emitter.next(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Response C, \r\n", Charsets.UTF_8)));
//								System.out.println("Emit C");
//								Thread.sleep(1000);
//								emitter.next(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Response D \r\n", Charsets.UTF_8)));
//								System.out.println("Emit D");
//								emitter.complete();
//							} 
//							catch (InterruptedException e) {
//								e.printStackTrace();
//							}
//						}));
						
//						response.body().data(Flux.create(emitter -> {
//							new Thread(() -> {
//								try {
//									for(int i=0;i<100;i++) {
//										emitter.next(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Response A, \r\n", Charsets.UTF_8)));
//										Thread.sleep(100);
//									}
//									emitter.complete();
//								} 
//								catch (InterruptedException e) {
//									e.printStackTrace();
//								}
//							}).start();
//						}));
					}),
				() -> exchange.response().body().raw().data(Mono.just(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("=== Empty ===", Charsets.UTF_8))))
			);
		};
	}
	
	private ExchangeHandler<RequestBody, ResponseBody, Exchange<RequestBody, ResponseBody>> urlEncoded() {
		return exchange -> exchange.response(
	)		.headers(headers -> headers
				.status(200)
				.contentType("text/plain;charset=utf-8")
			)
			.body().raw()
			.data(
				exchange.request().body()
					.map(body -> body.urlEncoded()
						.parameters()
						.collectList()
						.map(parameters -> "Received parameters: " + parameters.stream().map(param -> param.getName() + " = " + param.getValue()).collect(Collectors.joining(", ")))
						.map(result -> Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(result, Charsets.UTF_8)))
					)
					.orElse(Mono.just(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("=== Empty ===", Charsets.UTF_8))))
			);
	}
	
	private ExchangeHandler<RequestBody, ResponseBody, Exchange<RequestBody, ResponseBody>> multipartEcho() {
		return exchange -> {
			Charset requestCharset = exchange.request().headers().<Headers.ContentType>getHeader(Headers.CONTENT_TYPE).map(Headers.ContentType::getCharset).orElse(Charsets.DEFAULT);
			Flux<ByteBuf> responseData = exchange.request().body()
				.map(body -> body.multipart().parts().flatMapSequential(part -> {
					ByteBuf buf = Unpooled.unreleasableBuffer(Unpooled.buffer(256));
					
					buf.writeCharSequence("================================================================================\n", Charsets.UTF_8);
					buf.writeCharSequence("name: " + part.getName() + "\n", Charsets.UTF_8);
					part.getFilename().ifPresent(filename -> buf.writeCharSequence("filename: " + filename + "\n", Charsets.UTF_8));
					buf.writeCharSequence("content-type: " + part.headers().getContentType() + "\n", Charsets.UTF_8);
					buf.writeCharSequence("charset: " + requestCharset + "\n", Charsets.UTF_8);
					buf.writeCharSequence("size: " + part.headers().getSize() + "\n", Charsets.UTF_8);
					String headers = "headers:\n";
					
					headers += part.headers().getAllHeader().entrySet().stream()
						.flatMap(e -> {
							return e.getValue().stream();
						})
						.map(h -> "  - " + h.getHeaderValue())
						.collect(Collectors.joining("\n"));
					buf.writeCharSequence(headers + "\n", Charsets.UTF_8);
					buf.writeCharSequence("data: \n[", Charsets.UTF_8);
					
					return Flux.concat(
						Mono.<ByteBuf>just(buf),
						part.data().doOnNext(chunk -> chunk.retain()),
						Mono.<ByteBuf>just(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("]\n================================================================================\r\n", Charsets.UTF_8)))
					);
				}))
				.orElse(Flux.just(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("=== Empty ===", Charsets.UTF_8))));
			
			exchange.response()
				.headers(headers -> headers
					.status(200)
					.contentType("text/plain;charset=utf-8")
				)
				.body().raw().data(responseData);
		};
	}
	
	// TODO FileRegion
	// TODO we should provide a specific FilePart with adhoc methods to manipulate the File data flux or some utilities to make this simpler (especially regarding error handling, size limits...): a Part to Mono<File> mapper would be interesting as it would allow to chain the flux to the response data
	// TODO progressive upload can also be done: sse can do the trick but we should see other client side tricks for this as well
	// TODO it seems the size of the resulting file doesn't match the source why?
	private ExchangeHandler<RequestBody, ResponseBody, Exchange<RequestBody, ResponseBody>> multipartSaveFile() {
		return exchange -> 
			exchange.request().body().ifPresentOrElse(
				body -> body.multipart().parts().filter(p -> p.getFilename().isPresent()).subscribe(
					filePart -> {
						try {
							SeekableByteChannel byteChannel = Files.newByteChannel(Paths.get("uploads/" + filePart.getFilename().get()), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
							
							filePart.data().subscribe(
								chunk -> {
									System.out.println("File chunk: " + chunk.readableBytes());
									try {
										byteChannel.write(chunk.nioBuffer());
									} catch (IOException e) {
										e.printStackTrace();
									}
								},
								ex -> {
									
								},
								() -> {
									System.out.println("Saved file: " + filePart.getFilename().get());
									try {
										byteChannel.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							);
							
						} catch (IOException e) {
							e.printStackTrace();
						}
					},
					ex -> {
						exchange.response()
							.headers(headers -> headers
								.status(500)
								.contentType("text/plain;charset=utf-8")
							)
							.body().empty();
					},
					() -> {
						exchange.response()
							.headers(headers -> headers
								.status(200)
								.contentType("text/plain;charset=utf-8")
							)
							.body().empty();
					}
				),
			() -> exchange.response().body().raw().data(Mono.just(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("=== Empty ===", Charsets.UTF_8))))
		);
	}
	
	private ExchangeHandler<RequestBody, ResponseBody, Exchange<RequestBody, ResponseBody>> echoParameters() {
		return exchange -> exchange.response()
			.headers(headers -> headers
				.status(200)
				.contentType("text/plain;charset=UTF-8")
				.add("test", "1235")
			)
			.body().raw().data(
				//Mono.just(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Received parameters: " + request.parameters().getAll().entrySet().stream().map(e -> e.getKey() + "=" + e.getValue().stream().map(Parameter::getValue).collect(Collectors.joining(", "))).collect(Collectors.joining(", ")), Charsets.UTF_8)))
				Mono.just(Unpooled.copiedBuffer("Received parameters: " + exchange.request().parameters().getAll().entrySet().stream().map(e -> e.getKey() + "=" + e.getValue().stream().map(Parameter::getValue).collect(Collectors.joining(", "))).collect(Collectors.joining(", ")), Charsets.UTF_8))
			);
	}
	
	private ExchangeHandler<RequestBody, ResponseBody, Exchange<RequestBody, ResponseBody>> stream() {
		return exchange -> exchange.response()
			.headers(headers -> headers
				.status(200)
				.contentType("text/plain; charset=\"UTF-8\"")
				.add("test", "1235")
			)
			.body().raw().data(
				Flux.just(
					Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Response A, \r\n", Charsets.UTF_8)),
					Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Response B, \r\n", Charsets.UTF_8)),
					Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Response C, \r\n", Charsets.UTF_8)),
					Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Response D, \r\n", Charsets.UTF_8)),
					Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Response E \r\n", Charsets.UTF_8))
				)
				.delayElements(Duration.ofMillis(500)) 
			);
	}
	
	private ExchangeHandler<RequestBody, ResponseBody, Exchange<RequestBody, ResponseBody>> sse() {
		return exchange -> exchange.response()
			.body().sse().events(
				Flux.interval(Duration.ofSeconds(1))
					.map(sequence -> exchange.response().body().sse().create(configurator -> configurator
							.id(Long.toString(sequence))
							.event("periodic-event")
							.comment("some comment \n on mutliple lines")
							.data(Mono.just(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("SSE - " + LocalTime.now().toString() + "\r\n", Charsets.UTF_8))))
						)
					)
					.doOnNext(evt -> System.out.println("Emit sse"))
			);
	}
	
	private ExchangeHandler<RequestBody, ResponseBody, Exchange<RequestBody, ResponseBody>> sse2() {
		return exchange -> exchange.response()
			.body().sse().events(
				Flux.range(0, 10)
					.map(sequence -> exchange.response().body().sse().create(configurator -> configurator
							.id(Long.toString(sequence))
							.event("periodic-event")
							.comment("some comment")
							.data(Mono.just(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("SSE - " + LocalTime.now().toString() + "\r\n", Charsets.UTF_8))))
						)
					)
					.doOnNext(evt -> System.out.println("Emit sse"))
					.delayElements(Duration.ofSeconds(1))
			);
	}
	
	private ExchangeHandler<RequestBody, ResponseBody, Exchange<RequestBody, ResponseBody>> printRequest() {
		return exchange -> {
			Charset requestCharset = exchange.request().headers().<Headers.ContentType>getHeader(Headers.CONTENT_TYPE).map(Headers.ContentType::getCharset).orElse(Charsets.DEFAULT);
			
			ByteBuf buf = Unpooled.unreleasableBuffer(Unpooled.buffer(256));
			
			buf.writeCharSequence("authority: " + exchange.request().headers().getAuthority() + "\n", Charsets.UTF_8);
			buf.writeCharSequence("path: " + exchange.request().headers().getPath() + "\n", Charsets.UTF_8);
			buf.writeCharSequence("method: " + exchange.request().headers().getMethod() + "\n", Charsets.UTF_8);
			buf.writeCharSequence("scheme: " + exchange.request().headers().getScheme() + "\n", Charsets.UTF_8);
			buf.writeCharSequence("content-type: " + exchange.request().headers().getContentType() + "\n", Charsets.UTF_8);
			buf.writeCharSequence("charset: " + requestCharset + "\n", Charsets.UTF_8);
			buf.writeCharSequence("size: " + exchange.request().headers().getSize() + "\n", Charsets.UTF_8);
			String headers = "headers:\n";
			headers += exchange.request().headers().getAllHeader().entrySet().stream()
				.flatMap(e -> {
					return e.getValue().stream();
				})
				.map(h -> "  - " + h.getHeaderValue())
				.collect(Collectors.joining("\n"));
			buf.writeCharSequence(headers + "\n", Charsets.UTF_8);
			
			String cookies = "cookies:\n";
			cookies += exchange.request().cookies().getAll().entrySet().stream()
				.flatMap(e -> e.getValue().stream())
				.map(c -> "  - " + c.getName() + "=" + c.getValue())
				.collect(Collectors.joining("\n"));
			buf.writeCharSequence(cookies + "\n", Charsets.UTF_8);
			
			String parameters = "parameters:\n";
			parameters += exchange.request().parameters().getAll().entrySet().stream()
				.map(e -> "  - " + e.getKey() + "=" + e.getValue().stream().map(Parameter::getValue).collect(Collectors.joining(", ")))
				.collect(Collectors.joining("\n"));
			buf.writeCharSequence(parameters + "\n", Charsets.UTF_8);
			
			exchange.response().headers(configurator -> configurator
					.status(200)
					.contentType("text/plain;charset=utf-8")
				)
				.body().raw().data(Mono.just(buf));
		};
	}
	
	private ExchangeHandler<RequestBody, ResponseBody, Exchange<RequestBody, ResponseBody>> printRequestSetCookieBefore() {
		return printRequest().map(handler -> exchange -> {
			exchange.response().cookies(cookies -> cookies.addCookie("test-cookie", "123465"));
			handler.handle(exchange);
		});
	}
	
	private ExchangeHandler<RequestBody, ResponseBody, Exchange<RequestBody, ResponseBody>> printRequestSetCookieAfter() {
		return printRequest().map(handler -> exchange -> {
			handler.handle(exchange);
			exchange.response().cookies(cookies -> cookies.addCookie("test-cookie", "123465"));
		});
	}
	
	private ExchangeHandler<RequestBody, ResponseBody, Exchange<RequestBody, ResponseBody>> setCookie() {
		return exchange -> exchange.response()
			.headers(headers -> headers.status(200).contentType("text/plain"))
			.cookies(cookies -> cookies.addCookie("test-cookie", "123465"))
			.body().raw().data(Mono.just(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Set cookie", Charsets.DEFAULT))));
	}
	
	public static class JsonRequest {
		private String field1;
		
		private String field2;

		public String getField1() {
			return field1;
		}

		public void setField1(String field1) {
			this.field1 = field1;
		}

		public String getField2() {
			return field2;
		}

		public void setField2(String field2) {
			this.field2 = field2;
		}
	}
	
	public static class JsonResponse {
		private String message;
		
		public String getMessage() {
			return message;
		}
		
		public void setMessage(String message) {
			this.message = message;
		}
	}
	
	public static class EntityResponseBody<T> {
		
		private Response<ResponseBody> response;
		
		private T entity;
		
		private EntityResponseBody(Response<ResponseBody> response) {
			this.response = response;
		}
		
		public Response<EntityResponseBody<T>> entity(T entity) {
			this.entity = entity;
			return this.response.map(ign -> this);
		}
		
		private T getEntity() {
			return entity;
		}
	}
	
	private ExchangeHandler<RequestBody, ResponseBody, Exchange<RequestBody, ResponseBody>> json(ObjectMapper mapper) {
		
		Function<JsonRequest, JsonResponse> handler0 = request -> {
			JsonResponse response = new JsonResponse();
			response.setMessage("Received request with field1 " + request.field1 + " and field2 " + request.field2);
			return response;
		};
		
		ExchangeHandler<JsonRequest, EntityResponseBody<JsonResponse>, Exchange<JsonRequest, EntityResponseBody<JsonResponse>>> handler1 = exchange -> {
			exchange.response().headers(headers -> headers.contentType("application/json")).body().entity(handler0.apply(exchange.request().body().get())); 
		};
		
		/*RequestHandler<JsonRequest, EntityResponseBody<JsonResponse>, Void> handler1 = (request, response) -> {
			response.headers(headers -> headers.contentType("application/json")).body().entity(handler0.apply(request.body().get())); 
		};*/
		
		return handler1.map(handler -> {
			return exchange -> {
				if(exchange.request().headers().<Headers.ContentType>getHeader(Headers.CONTENT_TYPE).get().getMediaType().equals("application/json")) {
					// convert json
				}
				else if(exchange.request().headers().<Headers.ContentType>getHeader(Headers.CONTENT_TYPE).get().getMediaType().equals("application/xml")) {
					// convert xml
				}
				
				
				exchange.response().body().raw().data(exchange.request().body().get().raw().data()
					.reduce(new ByteArrayOutputStream(), (out, chunk) -> {
						try {
							chunk.getBytes(chunk.readerIndex(), out, chunk.readableBytes());
						} 
						catch (IOException e) {
							throw Exceptions.propagate(e);
						}
						return out;
					})
					.map(ByteArrayOutputStream::toByteArray)
					.map(bytes -> {
						try {
							return mapper.readValue(bytes, JsonRequest.class);
						}
						catch (IOException e) {
							throw Exceptions.propagate(e);
						}
					}).map(jsonRequest -> {
						EntityResponseBody<JsonResponse> entityJsonResponse = new EntityResponseBody<>(exchange.response());
						handler.handle(exchange.map(ign -> jsonRequest, body -> entityJsonResponse));
						
						// response entity can come in an asynchronous way so we must delegate the whole process to the other handler
						// if we want to chain things we need to use publishers
						// handler1 is actually synchronous since there are no publisher accessible in handler1
						
						return entityJsonResponse.getEntity();
					}).flatMap(jsonResponse -> {
						try {
							return Mono.just(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(mapper.writeValueAsBytes(jsonResponse))));
						} 
						catch (JsonProcessingException e) {
							throw Exceptions.propagate(e);
						}
					})
				);
			};
		});
	}

}
