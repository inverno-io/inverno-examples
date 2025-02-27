/*
 * Copyright 2025 Jeremy KUHN
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
package io.inverno.example.app_web_server_security_session;

import io.inverno.core.v1.Application;
import io.inverno.mod.configuration.source.BootstrapConfigurationSource;
import java.io.IOException;

/**
 * <p>
 * Demonstrates how to secure a Web application storing authentication in a basic session.
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
public class Main {

	public static void main(String[] args) throws IOException {
		Application.run(new App_web_server_security_session.Builder().setAppConfigurationSource(new BootstrapConfigurationSource(Main.class.getModule(), args)));
	}
}
