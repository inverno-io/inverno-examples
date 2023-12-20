/*
 * Copyright 2022 Jeremy KUHN
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

package io.inverno.example.app_http_client;

import io.inverno.core.annotation.Bean;
import io.inverno.core.v1.Application;
import io.inverno.mod.configuration.ConfigurationSource;
import io.inverno.mod.configuration.source.BootstrapConfigurationSource;
import java.util.function.Supplier;
import org.jline.utils.OSUtils;

/**
 *
 * @author <a href="jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
public class Main {
	
	@Bean(name = "configurationSource")
	public static interface AppConfigurationSource extends Supplier<ConfigurationSource<?, ?, ?>> {}

	public static void main(String[] args) throws Exception {
		OSUtils.TEST_COMMAND = "test";
		App_http_client client = Application.run(new App_http_client.Builder(
				new BootstrapConfigurationSource(Main.class.getModule(), args)
			)
		);

		client.httpClientShell().join();
		System.exit(0);
	}
}
