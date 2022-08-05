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

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.inverno.core.annotation.Bean;
import io.inverno.core.v1.Application;
import io.inverno.mod.configuration.ConfigurationKey.Parameter;
import io.inverno.mod.configuration.ConfigurationSource;
import io.inverno.mod.configuration.source.BootstrapConfigurationSource;

/**
 * <p>
 * Uses the configuration module to bootstrap the application.
 * </p>
 * 
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
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
	 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
	 */
	@Bean
	public static interface AppConfigurationSource extends Supplier<ConfigurationSource<?, ?, ?>> {}
	
	/**
	 * <p>
	 * Socket bean used to inject the parameters defining the context for which the
	 * module's configuration must be loaded.
	 * </p>
	 * 
	 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
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
		// Parameters should be sorted from the most general to the least general: environment, node
		List<Parameter> appConfigurationParameters = new ArrayList<>();
		String environment = System.getProperty("environment");
		if(environment != null) {
			appConfigurationParameters.add(Parameter.of("environment", environment));
		}
		String node = System.getProperty("node");
		if(node != null) {
			appConfigurationParameters.add(Parameter.of("node", node));
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
		LOGGER.info(" * uris: " + app_config.appConfiguration().uris().stream().map(URI::toString).collect(Collectors.joining(", ")));
		LOGGER.info(" * date: " + app_config.appConfiguration().date());
		LOGGER.info(" * sub_configuration.param: " + app_config.appConfiguration().sub_configuration().param());
	}
}
