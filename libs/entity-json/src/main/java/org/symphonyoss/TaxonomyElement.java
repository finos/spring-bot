package org.symphonyoss;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Base class for hashtags, cashtags, mentions in symphony.
 * 
 * @author Rob Moffat
 *
 */
public abstract class TaxonomyElement {
	
	public TaxonomyElement() {
	}

	public TaxonomyElement(String value) {
		super();
		this.value = value;
	}
	
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		TaxonomyElement other = (TaxonomyElement) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	@JsonIgnore
	public abstract String getSymbolPrefix();

	
}
