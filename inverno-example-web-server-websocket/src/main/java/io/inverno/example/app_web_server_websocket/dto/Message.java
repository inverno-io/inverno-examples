/*
 * Copyright 2022 Jeremy KUHN
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
package io.inverno.example.app_web_server_websocket.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * Represents a chat message send or received from/to the WebSocket client.
 * </p>
 * 
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 *
 */
public class Message {

	@JsonIgnore
	private final String nickname;

	@JsonIgnore
	private final String message;
	
	@JsonIgnore
	private final String color;
	
	@JsonCreator
	public Message(@JsonProperty( value = "nickname", required = true ) String nickname, @JsonProperty( value = "message", required = true ) String message, @JsonProperty( value = "color", required = true ) String color) {
		this.nickname = nickname;
		this.message = message;
		this.color = color;
	}

	@JsonProperty( "nickname" )
	public String getNickname() {
		return nickname;
	}
	
	@JsonProperty( "message" )
	public String getMessage() {
		return message;
	}

	@JsonProperty( "color" )
	public String getColor() {
		return color;
	}
}
