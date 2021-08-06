package example.symphony.demoworkflow.todo;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.finos.symphony.toolkit.workflow.annotations.ButtonRequest;
import org.finos.symphony.toolkit.workflow.annotations.ChatRequest;
import org.finos.symphony.toolkit.workflow.annotations.ChatResponseBody;
import org.finos.symphony.toolkit.workflow.annotations.ChatVariable;
import org.finos.symphony.toolkit.workflow.annotations.WorkMode;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.content.Word;
import org.springframework.stereotype.Controller;

import example.symphony.demoworkflow.todo.ToDoItem.Status;

@Controller
public class ToDoController {

	@ChatRequest(value="new", description = "Create new item list")
	public ToDoList init() {
		return new ToDoList();
	}
	
	private void reNumber(ToDoList l) {
		int initial = 1;
		for (ToDoItem toDoItem : l.getItems()) {
			toDoItem.setNumber(initial++);
		}
	}
	
	@ChatRequest(value="add", description = "Add an item")
	@ChatResponseBody(workMode = WorkMode.EDIT)
	public NewItemDetails add1(User author) {
		NewItemDetails out = new NewItemDetails();
		out.assignTo = author;
		return out;
	}
	
	@ButtonRequest(value = NewItemDetails.class)
	public ToDoList add(NewItemDetails a, User u, Optional<ToDoList> toDo) {
		ToDoList out = toDo.orElse(new ToDoList());
		out.getItems().add(new ToDoItem(a.getDescription(), u, a.getAssignTo(), Status.OPEN));
		reNumber(out);
		return out;
	}

	@ChatRequest(value="show", description = "Show current list of items")
	@ChatResponseBody(workMode = WorkMode.EDIT)
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

	@ChatRequest(value="delete {item}", description = "Remove items by number. e.g. \"/delete 5 6 7\"")
	public ToDoList delete(@ChatVariable(name = "item") List<Word> toDelete, Optional<ToDoList> toDo) {
		ToDoList out = toDo.orElse(new ToDoList());
		Set<Integer> toRemove = numbers(toDelete);
		for (Iterator<ToDoItem> iterator = out.getItems().iterator(); iterator.hasNext();) {
			ToDoItem item = iterator.next();
			if (toRemove.contains(item.getNumber())) {
				iterator.remove();
			}	
		}
		reNumber(out);
		return out;
	}

	private void changeStatus(ToDoList on, List<Word> words, User u, Status s) {
		Set<Integer> toUpdate = numbers(words);

		on.getItems().stream()
			.filter(i -> toUpdate.contains(i.getNumber()))
			.forEach(i -> {
				i.setAssignTo(u);	
				i.setStatus(s);
		});
		
		reNumber(on);
	}
	
	@ChatRequest(value="complete {items} {by}", description = "Complete items, e.g. \"/complete 1 3 5 @Suresh Rupnar\"")
	public ToDoList complete(@ChatVariable("items") List<Word> words, @ChatVariable("by") Optional<User> by, User a, Optional<ToDoList> toDo) {
		ToDoList out = toDo.orElse(new ToDoList());
		User u = by.orElse(a);
		changeStatus(out, words, u, Status.COMPLETE);
		return out;
	}

	@ChatRequest(value="assign {items} {to}", description = "Assign items, e.g. \"/assign 1 3 5 @Suresh Rupnar\"")
	public ToDoList assign(@ChatVariable("items") List<Word> words, @ChatVariable("by") Optional<User> by, User a, Optional<ToDoList> toDo) {
		ToDoList out = toDo.orElse(new ToDoList());
		User u = by.orElse(a);
		changeStatus(out, words, u, Status.OPEN);
		return out;
	}
}
