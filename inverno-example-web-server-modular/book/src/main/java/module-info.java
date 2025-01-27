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

/**
 * <p>
 * This is a sample Book API which demonstrates Inverno Web server module capabilities.
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 * 
 * @version 1.2.3
 */
@io.inverno.core.annotation.Module( excludes = { "io.inverno.mod.web.server" } )
module io.inverno.example.web_server_modular.book {
    requires io.inverno.core;
    requires io.inverno.mod.web.server;

    exports io.inverno.example.web_server_modular.book to io.inverno.example.web_server_modular.app;
    exports io.inverno.example.web_server_modular.book.dto to com.fasterxml.jackson.databind;
}
