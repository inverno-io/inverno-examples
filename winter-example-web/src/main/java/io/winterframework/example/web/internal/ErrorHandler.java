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

import java.util.function.Supplier;

import io.winterframework.mod.web.ErrorExchange;
import io.winterframework.mod.web.ExchangeHandler;
import io.winterframework.mod.web.ResponseBody;
import io.winterframework.mod.web.Status;
import io.winterframework.mod.web.router.Router;

/**
 * @author jkuhn
 *
 */
//@Bean(visibility = Visibility.PRIVATE)
//@Wrapper
public class ErrorHandler implements Supplier<ExchangeHandler<Void, ResponseBody, ErrorExchange<ResponseBody, Throwable>>> {

	@Override
	public ExchangeHandler<Void, ResponseBody, ErrorExchange<ResponseBody, Throwable>> get() {
		return Router.error()
			.route().handler(exchange -> {
				exchange.response()
					.headers(h -> h.status(Status.INTERNAL_SERVER_ERROR).contentType("application/json"))
					.body().raw().data("{\"type\":\"" + exchange.getError().getClass() + "\",\"message\":\"" + exchange.getError().getMessage() + "\"}");
			});
	}

}
