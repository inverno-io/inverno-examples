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
 * This is a sample Book API which demonstrates Winter Web module capabilities.
 * 
 * @author <a href="mailto:jeremy.kuhn@winterframework.io">Jeremy Kuhn</a>
 * 
 * @version 1.2.3
 */
@io.winterframework.core.annotation.Module( excludes = { "io.winterframework.mod.web" } )
module io.winterframework.example.web_modular.book {
    requires io.winterframework.core;
    requires io.winterframework.mod.web;
    
    exports io.winterframework.example.web_modular.book to io.winterframework.example.web_modular.app;
    exports io.winterframework.example.web_modular.book.dto to com.fasterxml.jackson.databind;
}