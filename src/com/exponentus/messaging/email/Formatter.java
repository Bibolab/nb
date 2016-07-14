package com.exponentus.messaging.email;

import org.stringtemplate.v4.ST;

public class Formatter {

	public static void main(String[] args) {
		ST hello = new ST("Hello, <name>!");
		hello.add("name", "World");
		String output = hello.render();
		System.out.println(output);

	}

}
