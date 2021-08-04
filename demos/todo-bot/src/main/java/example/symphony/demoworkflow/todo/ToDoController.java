package example.symphony.demoworkflow.todo;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.finos.symphony.toolkit.workflow.annotations.ChatVariable;
import org.finos.symphony.toolkit.workflow.annotations.Exposed;
import org.finos.symphony.toolkit.workflow.annotations.WorkMode;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.content.Word;
import org.springframework.stereotype.Controller;

import example.symphony.demoworkflow.todo.ToDoItem.Status;

@Controller
public class ToDoController {

	@Exposed(value="new", description = "Create new item list")
	public ToDoList init() {
		return new ToDoList();
	}
	
	private void reNumber(ToDoList l) {
		int initial = 1;
		for (ToDoItem toDoItem : l.getItems()) {
			toDoItem.setNumber(initial++);
		}
	}
	
	@Exposed(value="add", description = "Add an item")
	public NewItemDetails add1(User author) {
		NewItemDetails out = new NewItemDetails();
		out.assignTo = author;
		return out;
	}
	
	@Exposed(value="add", addToHelp = false, formClass = NewItemDetails.class)
	public ToDoList add(NewItemDetails a, User u, Optional<ToDoList> toDo) {
		ToDoList out = toDo.orElse(new ToDoList());
		out.getItems().add(new ToDoItem(a.getDescription(), u, a.getAssignTo(), Status.OPEN));
		reNumber(out);
		return out;
	}

	@Exposed(value="show", description = "Show current list of items")
	public ToDoList show(Optional<ToDoList> in) {
		ToDoList out = in.orElse(new ToDoList());
		reNumber(out);
		return out;
	}
	
	private Integer parseInt(Word w) {
		try {
			return Integer.parseInt(w.getText());
		} catch (NumberFormatException nfe) {
			return null;
		}
	}
	
	private Set<Integer> numbers(List<Word> m) {
		return m.stream()
			.map(w -> parseInt(w))
			.filter(i -> i != null)
			.collect(Collectors.toSet());
	}

	@Exposed(value="delete {item}", description = "Remove items by number. e.g. \"/delete 5 6 7\"")
	public ToDoList delete(@ChatVariable(name = "item") List<Word> toDelete, Optional<ToDoList> toDo) {
		ToDoList out = in.orElse(new ToDoList());
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
