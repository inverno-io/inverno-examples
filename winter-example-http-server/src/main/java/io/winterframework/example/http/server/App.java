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
package io.winterframework.example.http.server;

import java.io.IOException;
import java.util.List;

import io.winterframework.core.v1.Application;
import io.winterframework.mod.configuration.ConfigurationKey.Parameter;
import io.winterframework.mod.configuration.source.ApplicationConfigurationSource;

/**
 * <p>
 * Winter HTTP server example application.
 * </p>
 * 
 * @author <a href="jeremy.kuhn@winterframework.io">Jeremy Kuhn</a>
 */
public class App {
	
	/**
	 * <p>
	 * Starts the example HTTP server application.
	 * </p>
	 * 
	 * <p>
	 * HTTPS can be activated by specifying {@code profile} system property as
	 * follows: {@code -Dprofile=https}.
	 * </p>
	 * 
	 * @param args
	 * 
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 */
	public static void main(String[] args) throws IllegalStateException, IllegalArgumentException, IOException {
		Application.with(new Server.Builder()
				.setAppConfigurationSource(new ApplicationConfigurationSource(App.class.getModule(), args))
				.setAppConfigurationParameters(List.of(Parameter.of("profile", System.getProperty("profile", "default"))))
			).run();
	}
}
