/*
 * Copyright 2025 Jeremy KUHN
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
package io.inverno.example.app_web_server_security_session_jwt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * The session data.
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppSessionData {

	private final AtomicInteger counter;

	public AppSessionData() {
		this(0);
	}

	@JsonCreator
	public AppSessionData(@JsonProperty("counter") int counter) {
		this.counter = new AtomicInteger(counter);
	}

	@JsonProperty("counter")
	public int getCounter() {
		return counter.get();
	}

	public int incrementAndGetCounter() {
		return this.counter.incrementAndGet();
	}
}
