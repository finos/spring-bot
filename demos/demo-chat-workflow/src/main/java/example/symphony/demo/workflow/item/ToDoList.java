/**
 * 
 */
package example.symphony.demo.workflow.item;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.content.Content;
import com.github.deutschebank.symphony.workflow.content.Paragraph;
import com.github.deutschebank.symphony.workflow.content.User;
import com.github.deutschebank.symphony.workflow.content.Word;
import com.github.deutschebank.symphony.workflow.java.Exposed;
import com.github.deutschebank.symphony.workflow.java.Work;

import example.symphony.demo.workflow.item.ToDoItem.Status;

/**
 * @author rupnsur
 *
 */
@Work(name = "Todo Items", instructions = "List of current items", editable = true)
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

	@Exposed(description = "Create new item list")
	public static ToDoList init() {
		return new ToDoList();
	}

	@Exposed(description = "Add an item")
	public ToDoList add(NewItemDetails a, User u) {
		this.items.add(new ToDoItem(a.getDescription(), u, a.getAssignTo(), Status.OPEN));
		reNumber();
		return this;
	}

	@Exposed(description = "Show current list of items")
	public ToDoList show(Workflow wf) {
		reNumber();
		return this;
	}

	@Exposed(description = "Append an item. e.g. \"/append item1\"")
	public ToDoList append(Paragraph p, User u) {
		List<Content> elements = p.getContents();
		if (elements.size() > 1) {
			elements.remove(0);
			p = Paragraph.of(elements);
			this.items.add(new ToDoItem(p.getText(), u, null, Status.OPEN));
		}
		reNumber();
		return this;
	}

	@Exposed(description = "Remove items. e.g. \"/remove 5 6 7\"")
	public ToDoList remove(Paragraph p) {
		List<Content> elements = p.getContents();
		if (elements.size() > 1) {
			elements.remove(0);
			List<ToDoItem> removeItems = new ArrayList<>();
			for (Content content : elements) {
				if (content instanceof Word) {
					removeItems.add(items.get(Integer.parseInt(content.getText()) - 1));					
				}
			}
			items.removeAll(removeItems);
		}
		reNumber();
		return this;
	}

	@Exposed(description = "Assign items, e.g. \"/assign 1 3 5 @Suresh Rupnar\"")
	public ToDoList assign(Paragraph p) {
		List<Content> elements = p.getContents();
		if (elements.size() > 1) {
			elements.remove(0);
			if (elements.get(elements.size() - 1) instanceof User) {
				User user = (User) elements.get(elements.size() - 1);
				for (Content content : elements) {
					if (content instanceof Word) {
						items.get(Integer.valueOf(content.getText()) - 1).setAssignTo(user);;
					}
				}
			}
		}
		reNumber();
		return this;
	}

	private void reNumber() {
		int initial = 1;
		for (ToDoItem toDoItem : items) {
			toDoItem.setNumber(initial++);
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((items == null) ? 0 : items.hashCode());
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
		ToDoList other = (ToDoList) obj;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		return true;
	}
}
