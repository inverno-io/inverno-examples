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

/**
 * <p>This is an example application to demonstrate Winter Web router module <a href="test.html" title="toto">test link</a>.</p>
 * 
 * <p>It especially shows how to create {@link io.winterframework.mod.web.annotation.WebController WebController} beans.</p>
 * 
 * <p><img src="toto.png" alt="toto alt" title="toto title"/></p>
 * 
 * @author <a href="mailto:jeremy.kuhn@winterframework.io">Jeremy Kuhn</a>
 * 
 * @version 1.2.3
 * 
 */
@io.winterframework.core.annotation.Module

@io.winterframework.core.annotation.Wire(beans="io.winterframework.mod.boot:jsonByteBufConverter", into="io.winterframework.example.web:jsonHandler:jsonConverter")

@io.winterframework.core.annotation.Wire(beans="io.winterframework.example.web:webRouterConfigurer", into="io.winterframework.mod.web:WebRouterConfigurer")
module io.winterframework.example.web {
	requires io.winterframework.core;

	requires io.winterframework.mod.boot;
	
	requires io.winterframework.mod.web;
	
	requires com.fasterxml.jackson.databind;
	requires io.netty.common;
	requires io.netty.codec.http;
	
	exports io.winterframework.example.web;
	exports io.winterframework.example.web.dto;
}
