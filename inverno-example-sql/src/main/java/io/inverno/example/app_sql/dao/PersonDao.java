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
package io.inverno.example.app_sql.dao;

import io.inverno.core.annotation.Bean;
import io.inverno.example.app_sql.domain.Person;
import io.inverno.mod.sql.SqlClient;
import reactor.core.publisher.Mono;

/**
 * <p>
 * Person's Data Access Object bean.
 * </p>
 * 
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 *
 */
@Bean
public class PersonDao {

	private final SqlClient sqlClient;
	
	/**
	 * 
	 * @param sqlClient 
	 */
	public PersonDao(SqlClient sqlClient) {
		this.sqlClient = sqlClient;
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
		return Mono.from(this.sqlClient
			.preparedStatement("INSERT INTO person (firstname, lastname, age) VALUES ($1, $2, $3) RETURNING id")
			.bind(person.getFirstname(), person.getLastName(), person.getAge())
			.execute())
			.flatMap(result -> Mono.from(result.rows()))
			.map(row -> {
				person.setId(row.getLong("id"));
				return person;
			});
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
		return this.sqlClient.queryForObject(
			"SELECT * FROM person where id = $1", 
			row -> new Person(row.getLong("id"), row.getString("firstname"), row.getString("lastname"), row.getInteger("age")),
			id);
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
		return Mono.from(this.sqlClient.query("DELETE FROM person WHERE id = $1 RETURNING id", id)).map(row -> true).defaultIfEmpty(false);
	}
}
