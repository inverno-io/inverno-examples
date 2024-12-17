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
package io.inverno.example.web_server_modular.admin;


import io.inverno.core.annotation.Bean;
import io.inverno.mod.base.resource.MediaTypes;
import io.inverno.mod.http.base.Method;
import io.inverno.mod.web.base.annotation.FormParam;
import io.inverno.mod.web.server.annotation.WebController;
import io.inverno.mod.web.server.annotation.WebRoute;
import java.time.LocalDate;
import java.util.Optional;
import reactor.core.publisher.Mono;

/**
 * 
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
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
