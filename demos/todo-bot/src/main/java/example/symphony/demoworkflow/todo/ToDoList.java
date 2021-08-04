/**
 * 
 */
package example.symphony.demoworkflow.todo;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.finos.symphony.toolkit.workflow.annotations.Work;

/**
 * @author rupnsur
 *
 */
@Work
public class ToDoList {

	@Valid
	private List<ToDoItem> items = new ArrayList<ToDoItem>();

	public ToDoList() {
		super();
	}

	public ToDoList(List<ToDoItem> items) {
		super();
		this.items = items;
	}

	public List<ToDoItem> getItems() {
		return items;
	}

	public void setItems(List<ToDoItem> items) {
		this.items = items;
	}

	
	
	
}
