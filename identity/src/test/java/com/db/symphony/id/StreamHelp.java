package com.db.symphony.id;

import java.io.InputStream;
import java.util.Scanner;

public class StreamHelp {

	public static String asString(InputStream is) {
		try (Scanner scanner = new Scanner(is, "UTF-8")) {
			return scanner.useDelimiter("\\A").next();
		}	
	}
}
