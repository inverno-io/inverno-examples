/*
 * Copyright 2021 Jeremy KUHN
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
package io.inverno.example.app_irt.model;

import java.time.ZonedDateTime;

/**
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>@author jkuhn
 */
public class Event {

	public enum Type {
		INFO,
		WARN,
		ERROR
	}
	
	private final Type type;
	
	private final ZonedDateTime dateTime;
	
	private final String message;
	
	public Event(Event.Type type, ZonedDateTime dateTime, String message) {
		this.type = type;
		this.dateTime = dateTime;
		this.message = message;
	}
	
	public Type getType() {
		return type;
	}
	
	public ZonedDateTime getDateTime() {
		return dateTime;
	}
	
	public String getMessage() {
		return message;
	}
}
