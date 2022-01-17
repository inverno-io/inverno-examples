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
package io.inverno.example.app_redis;

import io.inverno.example.app_redis.domain.Person;
import io.inverno.core.annotation.Bean;
import io.inverno.core.v1.Application;
import io.inverno.example.app_redis.dao.PersonDao;
import io.inverno.mod.configuration.ConfigurationSource;
import io.inverno.mod.configuration.source.BootstrapConfigurationSource;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Flux;

/**
 * <p>
 * Connects to Redis server, inserts couple of entries, read entries and deletes entries.
 * </p>
 * 
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 *
 */
public class Main {
	
	private static final Logger LOGGER = LogManager.getLogger(Main.class);
	
	@Bean
	public static interface App_redisConfigurationSource extends Supplier<ConfigurationSource<?, ?, ?>> {}
	
	public static void main(String[] args) throws IOException {
		App_redis app_redis = Application.run(new App_redis.Builder().setApp_redisConfigurationSource(new BootstrapConfigurationSource(Main.class.getModule(), args)));
		
		try {
			PersonDao personDao = app_redis.personDao();

			Person jsmith = new Person("John", "Smith", 42);
			Person ljohnson = new Person("Linda", "Johnson", 25);

			// Store
			List<Person> storedPersons = Flux.merge(personDao.store(jsmith), personDao.store(ljohnson)).doOnNext(person -> LOGGER.info("Store {}", person)).collectList().block();

			// Get
			Flux.fromStream(storedPersons.stream().map(Person::getId)).flatMap(personDao::get).doOnNext(person -> LOGGER.info("Get {}", person)).collectList().block();

			// Delete
			Flux.fromStream(storedPersons.stream().map(Person::getId)).flatMap(personDao::delete).doOnNext(del -> LOGGER.info("Delete {}", del)).collectList().block();
		}
		finally {
			app_redis.stop();
		}
	}
}
