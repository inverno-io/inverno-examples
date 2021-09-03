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

import org.reactivestreams.Publisher;

/**
 * 
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 *
 */
public class Item {

	private String title;
	
	private Publisher<String> content;
	
	private ZonedDateTime dateTime;
	
	private String type;
	
	public Item(String title, Publisher<String> content) {
		this(title, content, ZonedDateTime.now(), "info");
	}
	
	public Item(String title, Publisher<String> content, ZonedDateTime dateTime, String type) {
		this.title = title;
		this.content = content;
		this.dateTime = dateTime;
		this.type = type;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setContent(Publisher<String> content) {
		this.content = content;
	}
	
	public Publisher<String> getContent() {
		return content;
	}
	
	public void setDateTime(ZonedDateTime dateTime) {
		this.dateTime = dateTime;
	}
	
	public ZonedDateTime getDateTime() {
		return dateTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
