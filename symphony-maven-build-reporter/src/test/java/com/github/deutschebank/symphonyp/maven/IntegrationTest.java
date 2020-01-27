package com.github.deutschebank.symphonyp.maven;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.github.deutschebank.symphonyp.maven.SymphonyBuildReporter;

public class IntegrationTest {

	
	public static void main(String[] args) throws IOException {
		SymphonyBuildReporter sbr = new SymphonyBuildReporter();
		Map<String, Object> out = new HashMap<>();
		sbr.sendMessage(out);
		
	}
}
