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
		// this returns a fixed value, since equals uses part-matching
		return 0;
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
		} else if (!taxonomyPartMatch(id, other.id)) 
			return false;
		return true;
	}
	
	protected boolean taxonomyPartMatch(List<TaxonomyElement> a, List<TaxonomyElement> b) {
		for (TaxonomyElement tea : a) {
			for (TaxonomyElement teb : b) {
				if ((tea != null) && (teb != null) && (tea.getClass().equals(teb.getClass()))) {
					return tea.equals(teb);
				}
			}
		}
		
		return false;
	}

	protected String fromTaxonomy(Class<?> class1) {
		return getId().stream()
			.filter(t -> t != null)
			.filter(t -> class1.isAssignableFrom(t.getClass()))
			.findFirst()
			.map(te -> te.getValue())
			.orElse(null);
	}

	
}
