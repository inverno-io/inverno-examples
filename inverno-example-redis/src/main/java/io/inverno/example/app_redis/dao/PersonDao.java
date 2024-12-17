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
package io.inverno.example.app_redis.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.inverno.core.annotation.Bean;
import io.inverno.example.app_redis.domain.Person;
import io.inverno.mod.redis.RedisClient;
import java.io.UncheckedIOException;
import reactor.core.publisher.Mono;

/**
 * <p>
 * Person's Data Access Object bean.
 * </p>
 * 
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Bean
public class PersonDao {

	private static final String PERSON_TYPE = "PERSON";
	private static final String KEY_PERSON = "PERSON:%d";
	
	private static final String KEY_PERSON_SEQ = PERSON_TYPE + "|SEQ";
	
	private final RedisClient<String, String> redisClient;
	private final ObjectMapper mapper;
	
	/**
	 * 
	 * @param redisClient
	 * @param mapper 
	 */
	public PersonDao(RedisClient<String, String> redisClient, ObjectMapper mapper) {
		this.redisClient = redisClient;
		this.mapper = mapper;
	}
	
	/**
	 * <p>
	 * Stores the specified person in database.
	 * </p>
	 * 
	 * @param person the person to store
	 * @return 
	 */
	public Mono<Person> store(Person person) {
		if(person.getId() == null) {
			// create
			return this.redisClient
				.incr(KEY_PERSON_SEQ)
				.flatMap(id -> {
					try {
						person.setId(id);
						return this.redisClient.set().nx().build(String.format(KEY_PERSON, id), this.mapper.writeValueAsString(person));
					} 
					catch (JsonProcessingException ex) {
						throw new UncheckedIOException(ex);
					}
				})
				.map(result -> {
					if(!"OK".equals(result)) {
						throw new RuntimeException("Error creating entry: " + result);
					}
					return person;
				});
		}
		else {
			// update
			try {
				return this.redisClient
					.set().xx().build(String.format(KEY_PERSON, person.getId()), this.mapper.writeValueAsString(person))
					.map(result -> {
						if(!"OK".equals(result)) {
							throw new RuntimeException("Error updating entry: " + result);
						}
						return person;
					});
			} 
			catch (JsonProcessingException ex) {
				return Mono.error(new UncheckedIOException(ex));
			}
		}
	}
	
	/**
	 * <p>
	 * Returns the person with the specified id.
	 * </p>
	 * 
	 * @param id the id of the person to return
	 * @return 
	 */
	public Mono<Person> get(long id) {
		return this.redisClient
			.get(String.format(KEY_PERSON, id))
			.map(s -> {
				try {
					return this.mapper.readValue(s, Person.class);
				} 
				catch (JsonProcessingException ex) {
					throw new UncheckedIOException(ex);
				}
			});
	}
	
	/**
	 * <p>
	 * Deletes the person with the specified id.
	 * </p>
	 * 
	 * @param id the id of the person to delete
	 * @return 
	 */
	public Mono<Boolean> delete(long id) {
		return this.redisClient.del(String.format(KEY_PERSON, id)).map(count -> count == 1);
	}
}
