package io.winterframework.example.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavacBug {

	public JavacBug() {
		String toto = "a";
		
		test(Optional.of(Stream.of("a","b").collect(Collectors.toCollection(ArrayList::new))));
		
//		String toto = "a";
	}
	
	public void test(Optional<Collection<String>> opt) {
		
	}
}