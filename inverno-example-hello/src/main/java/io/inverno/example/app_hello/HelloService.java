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

import io.inverno.core.annotation.Bean;

/**
 * <p>
 * A simple module bean.
 * <p>
 * 
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 *
 */
@Bean
public class HelloService {
	
	private String greetingMessage;

    public HelloService() { }
    
    public void setGreetingMessage(String greetingMessage) {
    	this.greetingMessage = greetingMessage;
    }

    public void sayHello(String name) {
    	StringBuilder message = new StringBuilder();
    	
    	message.append("Hello ").append(name);
    	if(this.greetingMessage != null) {
    		message.append(", ").append(this.greetingMessage);
    	}
        System.out.println(message.toString());
    }
}
