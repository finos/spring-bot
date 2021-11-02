package org.finos.springbot.workflow.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import org.finos.springbot.workflow.annotations.Work;

/**
 * This is used for carrying a set of Item objects, useful for populating dropdowns.
 * 
 * @author rob@kite9.com
 *
 */
@Work(index = false)
public class DropdownList {
	
	public static class Item {
		
		String key;
		String name;
		
		public Item() {
			super();
		}
		public Item(String key, String name) {
			super();
			this.key = key;
			this.name = name;
		}
		
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(key, name);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Item other = (Item) obj;
			return Objects.equals(key, other.key) && Objects.equals(name, other.name);
		}
		
	}
	
	
	private Collection<Item> contents;

	public Collection<Item> getContents() {
		return contents;
	}

	public void setContents(Collection<Item> contents) {
		this.contents = contents;
	}

	public DropdownList() {
		this.contents = new ArrayList<Item>();
	}

	public DropdownList(Collection<Item> arg0) {
		this.contents = arg0;
	}

	public static DropdownList of(Item... i) {
		return new DropdownList(Arrays.asList(i));
	}

	public void add(Item i) {
		contents.add(i);
	}

	public int size() {
		return contents.size();
	}

	
}
