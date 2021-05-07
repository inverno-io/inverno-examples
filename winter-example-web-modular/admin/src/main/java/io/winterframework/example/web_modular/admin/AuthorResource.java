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
package io.winterframework.example.web_modular.admin;

import java.time.LocalDate;
import java.util.Optional;

import io.winterframework.core.annotation.Bean;
import io.winterframework.mod.base.resource.MediaTypes;
import io.winterframework.mod.http.base.Method;
import io.winterframework.mod.web.annotation.FormParam;
import io.winterframework.mod.web.annotation.WebController;
import io.winterframework.mod.web.annotation.WebRoute;
import reactor.core.publisher.Mono;

/**
 * 
 * @author <a href="mailto:jeremy.kuhn@winterframework.io">Jeremy Kuhn</a>
 *
 */
@Bean
@WebController(path = "/author")
public class AuthorResource {

	@WebRoute(method = Method.POST, consumes = MediaTypes.APPLICATION_X_WWW_FORM_URLENCODED)
	public Mono<Void> createAuthor(@FormParam String forename, @FormParam Optional<String> middlename, @FormParam String surname, @FormParam LocalDate birthdate, @FormParam Optional<LocalDate> deathdate, @FormParam String nationality) {
		// Store a new author...
		return Mono.empty();
	}
}
