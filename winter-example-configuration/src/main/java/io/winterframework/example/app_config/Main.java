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
package io.winterframework.example.app_config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.winterframework.core.annotation.Bean;
import io.winterframework.core.v1.Application;
import io.winterframework.mod.configuration.ConfigurationKey.Parameter;
import io.winterframework.mod.configuration.ConfigurationSource;
import io.winterframework.mod.configuration.source.BootstrapConfigurationSource;

/**
 * <p>
 * Uses the configuration module to bootstrap the application.
 * </p>
 * 
 * @author <a href="mailto:jeremy.kuhn@winterframework.io">Jeremy Kuhn</a>
 *
 */
public class Main {

	private static Logger LOGGER =  LogManager.getLogger(Main.class);
	
	/**
	 * <p>
	 * Socket bean used to inject the configuration source used to load the module's
	 * configuration.
	 * </p>
	 * 
	 * @author <a href="mailto:jeremy.kuhn@winterframework.io">Jeremy Kuhn</a>
	 */
	@Bean
	public static interface AppConfigurationSource extends Supplier<ConfigurationSource<?, ?, ?>> {}
	
	/**
	 * <p>
	 * Socket bean used to inject the parameters defining the context for which the
	 * module's configuration must be loaded.
	 * </p>
	 * 
	 * @author <a href="mailto:jeremy.kuhn@winterframework.io">Jeremy Kuhn</a>
	 *
	 */
	@Bean
	public static interface AppConfigurationParameters extends Supplier<List<Parameter>> {}
	
	public static void main(String[] args) throws IOException {
		// Creates the bootstrap configuration source
		BootstrapConfigurationSource appConfigurationSource = new BootstrapConfigurationSource(Main.class.getModule(), args);
		
		// Uses it first to get the parameters which can then be defined in any source
		// composed in the bootstrap configuration source (command line arguments,
		// system properties, system environment, cprops files), in our example the node
		// and the environment.
		// Parameters should be sorted from the least general to the most general: node, environment
		List<Parameter> appConfigurationParameters = new ArrayList<>();
		String node = System.getProperty("node");
		if(node != null) {
			appConfigurationParameters.add(Parameter.of("node", node));
		}
		String environment = System.getProperty("environment");
		if(environment != null) {
			appConfigurationParameters.add(Parameter.of("environment", environment));
		}
		
		// Runs the application with the bootstrap source and parameters
		App_config app_config = Application.run(new App_config.Builder()
			.setAppConfigurationSource(appConfigurationSource)
			.setAppConfigurationParameters(appConfigurationParameters)
		);
		
		// Shows resulting configuration
		LOGGER.info("App Configuration [ " + appConfigurationParameters.stream().map(parameter -> parameter.getKey() + "=" + parameter.getValue()).collect(Collectors.joining(", ")) + " ]:");
		LOGGER.info(" * message: " + app_config.appConfiguration().message());
		LOGGER.info(" * id: " + app_config.appConfiguration().id());
		LOGGER.info(" * uri: " + app_config.appConfiguration().uri());
		LOGGER.info(" * date: " + app_config.appConfiguration().date());
	}
}
