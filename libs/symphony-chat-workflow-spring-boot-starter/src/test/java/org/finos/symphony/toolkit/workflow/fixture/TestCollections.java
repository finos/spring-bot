package org.finos.symphony.toolkit.workflow.fixture;

import java.util.List;

import org.finos.symphony.toolkit.workflow.annotations.Work;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.HashTag;

@Work(name="Test Object 4", instructions="sundry other fields")
public class TestCollections {

	public static class MiniBean {
		
		public MiniBean() {
		
		}
		
		public MiniBean(String someString, Integer someInteger, List<String> someMoreStrings) {
			super();
			this.someString = someString;
			this.someInteger = someInteger;
			this.someMoreStrings = someMoreStrings;
		}

		String someString;
		
		Integer someInteger;
		
		List<String> someMoreStrings;
	
		public String getSomeString() {
			return someString;
		}

		public void setSomeString(String someString) {
			this.someString = someString;
		}

		public Integer getSomeInteger() {
			return someInteger;
		}

		public void setSomeInteger(Integer someInteger) {
			this.someInteger = someInteger;
		}

		public List<String> getSomeMoreStrings() {
			return someMoreStrings;
		}

		public void setSomeMoreStrings(List<String> someMoreStrings) {
			this.someMoreStrings = someMoreStrings;
		}
		
	}
	
	
	public enum Choice { A, B, C };
	
	List<String> stringList;
	
	List<Choice> enumList;
	
	List<MiniBean> minBeanList;
	
	List<HashTag> someHashTags;
	
	public TestCollections() {
		
	}

	public TestCollections(List<String> stringList, List<Choice> enumList, List<MiniBean> minBeanList, List<HashTag> hashTags) {
		super();
		this.stringList = stringList;
		this.enumList = enumList;
		this.minBeanList = minBeanList;
		this.someHashTags = hashTags;

	}
	
	public List<HashTag> getSomeHashTags() {
		return someHashTags;
	}

	public void setSomeHashTags(List<HashTag> someHashTags) {
		this.someHashTags = someHashTags;
	}

	public List<String> getStringList() {
		return stringList;
	}

	public void setStringList(List<String> stringList) {
		this.stringList = stringList;
	}

	public List<Choice> getEnumList() {
		return enumList;
	}

	public void setEnumList(List<Choice> enumList) {
		this.enumList = enumList;
	}

	public List<MiniBean> getMinBeanList() {
		return minBeanList;
	}

	public void setMinBeanList(List<MiniBean> minBeanList) {
		this.minBeanList = minBeanList;
	}
	
	
	
	
}
