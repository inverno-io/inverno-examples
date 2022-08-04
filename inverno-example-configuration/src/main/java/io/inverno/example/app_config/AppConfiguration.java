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
package io.inverno.example.app_config;

import java.net.URI;
import java.time.LocalDate;
import java.util.Set;

import io.inverno.mod.configuration.Configuration;

/**
 * <p>
 * Interface defining the application configuration.
 * </p>
 * 
 * <p>
 * This interface is processed by the compiler to generate a configuration
 * loader.
 * </p>
 * 
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 *
 */
@Configuration
public interface AppConfiguration {

	default String message() {
		return "Default message";
	}

	int id();
	
	URI uri();
	
	LocalDate date();
	
	default Set<URI> uris() {
		return Set.of();
	}
	
	SubConfiguration sub_configuration();
}
