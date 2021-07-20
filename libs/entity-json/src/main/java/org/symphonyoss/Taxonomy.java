package org.symphonyoss;

import java.util.List;

public class Taxonomy {

	private List<TaxonomyElement> id;

	public Taxonomy(List<TaxonomyElement> id) {
		super();
		this.id = id;
	}

	public Taxonomy() {
		super();
	}

	public List<TaxonomyElement> getId() {
		return id;
	}

	public void setId(List<TaxonomyElement> id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Taxonomy other = (Taxonomy) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	protected String fromTaxonomy(Class<?> class1) {
		return getId().stream()
			.filter(t -> class1.isAssignableFrom(t.getClass()))
			.findFirst()
			.map(te -> te.getValue())
			.orElse(null);
	}

	
}
