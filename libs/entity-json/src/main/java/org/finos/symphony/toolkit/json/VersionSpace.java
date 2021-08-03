package org.finos.symphony.toolkit.json;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * This declares a class that will be supported in symphony, and the versions it supports for writing and reading.
 * 
 * This means we can correctly set the "version" : "xxx" part of the JSON format for any given class, 
 * and also makes sure that we can read the versions provided.
 * 
 * @author Rob Moffat
 *
 */
public class VersionSpace {
	
	public final String typeName;
	public final String writeVersion;
	private final String[] readVersions;
	private final Class<?> toUse;
	
	public Class<?> getToUse() {
		return toUse;
	}

	public VersionSpace(String typeName, Class<?> toUse, String writeVersion, String... readVersions) {
		super();
		this.typeName = typeName;
		this.writeVersion = writeVersion;
		this.readVersions = readVersions;
		this.toUse = toUse;
	}
	
	public VersionSpace(Class<?> toUse, String writeVersion, String... readVersions) {
		this(EntityJson.getSymphonyTypeName(toUse), toUse, writeVersion, readVersions);
	}
	
	public VersionSpace(Class<?> toUse) {
		this(EntityJson.getSymphonyTypeName(toUse), toUse, "1.0");
	}
	
	
	public Predicate<String> toPattern(String version) {
		String converted = version
			.replace(".", "\\.")
			.replace("*", "[0-9]+");
		return Pattern.compile(converted).asPredicate();
	}
	
	public boolean typeMatches(String in) {
		return typeName.equalsIgnoreCase(in) || toUse.getName().equalsIgnoreCase(in);
	}
	
	public boolean typeMatches(Object in) {
		if (in instanceof Class) {
			return typeMatches(((Class<?>) in).getName());
		} else {
			return typeMatches(in.getClass());
		}
	}
	
	public boolean versionMatches(String in) {
		return writeVersion.equals(in) || Arrays.stream(readVersions).anyMatch(x -> toPattern(x).test(in));
	}
	
	public String getVersions() {
		return writeVersion+ ", "+Arrays.stream(readVersions).reduce("", (a, b) -> a+", "+b);
	}

	@Override
	public String toString() {
		return "VersionSpace [typeName=" + typeName + ", writeVersion=" + writeVersion + ", readVersions="
				+ Arrays.toString(readVersions) + ", toUse=" + toUse + "]";
	}

}