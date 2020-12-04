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
package io.winterframework.example.web;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

import io.winterframework.core.annotation.Bean;
import io.winterframework.core.v1.Application;
import io.winterframework.mod.configuration.ConfigurationKey.Parameter;
import io.winterframework.mod.configuration.ConfigurationSource;
import io.winterframework.mod.configuration.source.ApplicationConfigurationSource;

/**
 * @author jkuhn
 *
 */
public class App {
	
	@Bean
	public static interface ConfigurationParameters extends Supplier<List<Parameter>> {}

	@Bean
	public static interface AppConfigurationSource extends Supplier<ConfigurationSource<?, ?, ?>> {}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
	public static void main(String[] args) throws IllegalStateException, IOException {
		Application.with(new Web.Builder()
			.setAppConfigurationSource(new ApplicationConfigurationSource(App.class.getModule(), args))
			.setConfigurationParameters(List.of(Parameter.of("profile", System.getProperty("profile", "default"))))
//			.setRootHandler(configuration0())
//			.setErrorHandler(error())
		).run();
	}
}
