/*
 * Copyright 2024 Jeremy Kuhn
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
package io.inverno.example.app_discovery_http_k8s;

import io.inverno.core.v1.Application;
import io.inverno.mod.configuration.source.BootstrapConfigurationSource;
import java.io.IOException;

/**
 * <p>
 * Demonstrates HTTP service discovery in a Kubernetes cluster.
 * </p>
 *
 * <p>
 * The Kubernetes HTTP Discovery module currently only resolves services by looking at the environment variables defined in the container. It is possible to emulate this locally by defining the
 * following environment variables before running the application:
 * </p>
 *
 * <pre>{@code
 * $ export TEST_SERVICE_SERVICE_HOST=127.0.0.1
 * $ export TEST_SERVICE_SERVICE_PORT_HTTP=8080
 * }</pre>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
public class Main {

	public static void main(String[] args) throws IOException {
		Application.run(new App_discovery_http_k8s.Builder()
			.setAppConfigurationSource(new BootstrapConfigurationSource(Main.class.getModule(), args))
		);
	}
}
