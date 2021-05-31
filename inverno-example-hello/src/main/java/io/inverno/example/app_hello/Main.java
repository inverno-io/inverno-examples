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
package io.inverno.example.app_hello;

import java.util.function.Supplier;

import io.inverno.core.annotation.Bean;

/**
 * <p>
 * Starts an Inverno module and invoke a method on a public bean.
 * </p>
 * 
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 *
 */
public class Main {
	
	@Bean(name = "greetingMessage")
	public static interface GreetingMessageSocketBean extends Supplier<String> {}
    
    public static void main(String[] args) {
    	if(args.length != 1) {
    		System.out.println("Usage: hello NAME");
    	}
    	
    	// Creates the module builder
		App_hello.Builder builder = new App_hello.Builder().setGreetingMessage("how are you today?");
		
		// Builds the module
		App_hello app_hello = builder.build();

		try {
			// Starts the module and creates the module's beans
			app_hello.start();
			
			// Gets the 'helloService' bean and invoke method 'sayHello()'
			app_hello.helloService().sayHello(args[0]);
		} 
		finally {
			app_hello.stop();
		}
    }
}
