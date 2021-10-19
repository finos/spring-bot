package example.symphony.demoworkflow.todo;

import java.util.List;

import org.finos.springbot.workflow.annotations.Display;
import org.finos.springbot.workflow.annotations.Work;

@Work
public class SimpleTestObject {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
//	@Display(name = "Main")
//	private NewItemDetails nid;
//
//	public NewItemDetails getNid() {
//		return nid;
//	}
//
//	public void setNid(NewItemDetails nid) {
//		this.nid = nid;
//	}
//	
//	@Display(name = "New Item Details")
//	private List<NewItemDetails> lotsOfNids;
//
//	public List<NewItemDetails> getLotsOfNids() {
//		return lotsOfNids;
//	}
//
//	public void setLotsOfNids(List<NewItemDetails> lotsOfNids) {
//		this.lotsOfNids = lotsOfNids;
//	}
//	
	
}
