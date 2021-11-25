/**
 * 
 */
package org.finos.symphony.toolkit.spring.api.properties;

/**
 * default configuration properties for Symphony
 * @author sureshrupnar
 *
 */
public class DefaultConfigProperties {

	private Boolean localPOD;

	/**
	 * @return the localPOD
	 */
	public Boolean isLocalPOD() {
		return localPOD;
	}

	/**
	 * @param localPOD the localPOD to set
	 */
	public void setLocalPOD(Boolean localPOD) {
		this.localPOD = localPOD;
	}
	
}
