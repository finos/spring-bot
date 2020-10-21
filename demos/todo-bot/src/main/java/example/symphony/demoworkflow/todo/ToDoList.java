/**
 * 
 */
package example.symphony.demoworkflow.todo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.validation.Valid;

import com.github.deutschebank.symphony.workflow.content.Author;
import com.github.deutschebank.symphony.workflow.content.Content;
import com.github.deutschebank.symphony.workflow.content.Message;
import com.github.deutschebank.symphony.workflow.content.Paragraph;
import com.github.deutschebank.symphony.workflow.content.User;
import com.github.deutschebank.symphony.workflow.content.Word;
import com.github.deutschebank.symphony.workflow.java.Exposed;
import com.github.deutschebank.symphony.workflow.java.Work;

import example.symphony.demoworkflow.todo.ToDoItem.Status;

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
	
	private void reNumber() {
		int initial = 1;
		for (ToDoItem toDoItem : items) {
			toDoItem.setNumber(initial++);
		}
	}
	
	@Exposed(description = "Add an item")
	public ToDoList add(NewItemDetails a, Author u) {
		this.items.add(new ToDoItem(a.getDescription(), u, a.getAssignTo(), Status.OPEN));
		reNumber();
		return this;
	}

	@Exposed(description = "Show current list of items")
	public ToDoList show() {
		reNumber();
		return this;
	}
	
	private Integer parseInt(Word w) {
		try {
			return Integer.parseInt(w.getText());
		} catch (NumberFormatException nfe) {
			return null;
		}
	}
	
	private Set<Integer> numbers(Message m) {
		return m.only(Word.class).stream()
			.map(w -> parseInt(w))
			.filter(i -> i != null)
			.collect(Collectors.toSet());
	}

	@Exposed(isButton = false, description = "Remove items by number. e.g. \"/delete 5 6 7\"")
	public ToDoList delete(Message m) {
		Set<Integer> toRemove = numbers(m);
		for (Iterator<ToDoItem> iterator = items.iterator(); iterator.hasNext();) {
			ToDoItem item = iterator.next();
			if (toRemove.contains(item.getNumber())) {
				iterator.remove();
			}	
		}
		reNumber();
		return this;
	}



	private void changeStatus(Message m, User u, Status s) {
		Set<Integer> toUpdate = numbers(m);

		items.stream()
			.filter(i -> toUpdate.contains(i.getNumber()))
			.forEach(i -> {
				i.setAssignTo(u);	
				i.setStatus(s);
		});
		reNumber();
	}
	
	@Exposed(isButton = false, description = "Complete items, e.g. \"/complete 1 3 5 @Suresh Rupnar\"")
	public ToDoList complete(Message m, Author a) {
		User u = m.getNth(User.class, 0).orElse(a);
		changeStatus(m, u, Status.COMPLETE);
		return this;
	}

	@Exposed(isButton = false, description = "Assign items, e.g. \"/assign 1 3 5 @Suresh Rupnar\"")
	public ToDoList assign(Message m, Author a) {
		User u = m.getNth(User.class, 0).orElse(a);
		changeStatus(m, u, Status.OPEN);
		return this;
	}
	
	
}
