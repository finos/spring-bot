package org.finos.springbot.entityjson;

import java.util.Arrays;
import java.util.Objects;
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
		this(EntityJson.getEntityJsonTypeName(toUse), toUse, writeVersion, readVersions);
	}
	
	public VersionSpace(Class<?> toUse) {
		this(EntityJson.getEntityJsonTypeName(toUse), toUse, "1.0");
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(readVersions);
		result = prime * result + Objects.hash(toUse, typeName, writeVersion);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VersionSpace other = (VersionSpace) obj;
		return Arrays.equals(readVersions, other.readVersions) && Objects.equals(toUse, other.toUse)
				&& Objects.equals(typeName, other.typeName) && Objects.equals(writeVersion, other.writeVersion);
	}

}