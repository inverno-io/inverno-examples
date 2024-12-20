/*
 * Copyright 2024 Jeremy KUHN
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

package io.inverno.example.app_discovery_http;

import io.inverno.core.annotation.Bean;
import io.inverno.mod.configuration.ConfigurationSource;
import java.util.function.Supplier;

/**
 * <p>
 * Define the module sockets.
 * </p>
 *
 * @author <a href="jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
public final class ModuleSockets {

	private ModuleSockets() {}

	/**
	 * The application configuration source socket.
	 */
	@Bean
	public interface AppConfigurationSource extends Supplier<ConfigurationSource> {}

}