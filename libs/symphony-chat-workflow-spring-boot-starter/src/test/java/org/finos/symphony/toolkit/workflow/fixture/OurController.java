package org.finos.symphony.toolkit.workflow.fixture;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.annotations.ChatVariable;
import org.finos.symphony.toolkit.workflow.annotations.Exposed;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Chat;
import org.finos.symphony.toolkit.workflow.content.CodeBlock;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.Table;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.content.Word;
import org.finos.symphony.toolkit.workflow.form.Button;
import org.finos.symphony.toolkit.workflow.form.ButtonList;
import org.finos.symphony.toolkit.workflow.form.FormSubmission;
import org.finos.symphony.toolkit.workflow.form.Button.Type;
import org.finos.symphony.toolkit.workflow.response.AttachmentResponse;
import org.finos.symphony.toolkit.workflow.response.FormResponse;
import org.finos.symphony.toolkit.workflow.response.MessageResponse;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.HashTag;
import org.finos.symphony.toolkit.workflow.sources.symphony.json.EntityJsonConverter;
import org.springframework.stereotype.Controller;

@Controller
public class OurController {
	
	public List<Object> lastArguments;
	public String lastMethod;
	
	// todo: do we need any other kinds of wildcards?
	@Exposed("*") 
	public void listenToEverything(Message m) {
		// guarav's reminder bot should do this - it needs to parse the date out of every
		// message
		lastArguments = Collections.singletonList(m);
		lastMethod = "listenToEverything";
	}
	
	@Exposed(value = "call", formClass=Person.class)
	public void callPerson(Person arg) {
		// do your own form processing
		lastArguments = Collections.singletonList(arg);
		lastMethod = "callPerson";
	}
	
	@Exposed(value = "new claim", isButton = true, isMessage = false)
	public TestObject startNewClaim(StartClaim sc) {
		// can't run without StartClaim, returns form to begin a process..
		// user fills it in and this runs.
		lastArguments = Collections.singletonList(sc);
		lastMethod = "startNewClaim";
		return new TestObject();
	}
	

	@Exposed(value = "process", isButton = true, formName = "process-form")
	public void processForm(FormSubmission f) {
		// do your own form processing
		// is this needed?
		lastArguments = Collections.singletonList(f);
		lastMethod = "processForm";
	}
	
	
	@Exposed("list") 
	public void doCommand(Message m) {
		// do something when user types in "/list"
		lastArguments = Collections.singletonList(m);
		lastMethod = "doCommand";
	}

	
	@Exposed("show {user}") 
	public void userDetails(@ChatVariable("user") User u) {
		// provide some user details, e.g. /show @Rob Moffat
		lastArguments = Collections.singletonList(u);
		lastMethod = "userDetails";
	}
	
	@Exposed("userDetails2 {user}") 
	public void userDetails2(@ChatVariable("user") User u, User author) {
		// provide some user details, e.g. /show @Rob Moffat, also provides the person who typed the command
		lastArguments = Arrays.asList(u, author);
		lastMethod = "userDetails2";
	}

	@Exposed("process-table {sometable} {user}") 
	public void process1(@ChatVariable("sometable") Table t, @ChatVariable(required = false, value="user") User u) {
		// provide some processing for a table.
		lastArguments = Arrays.asList(t, u);
		lastMethod = "process-table";
	}

	@Exposed("update {code}") 
	public void process2(@ChatVariable("code") CodeBlock cb) {
		// provide some processing for a block of code
		lastArguments = Collections.singletonList(cb);
		lastMethod = "process2";
	}
	
	@Exposed({
		"add {user} to {hashtag}", 
		"add {user} {hashtag}"}) 
	public void addUserToTopic(@ChatVariable("user") User u, @ChatVariable("hashtag") HashTag t) {
		// provide some processing for a block of code
		lastArguments = Arrays.asList(u, t);
		lastMethod = "addUserToTopic";
	}
	
	
	@Exposed(admin = true, value = "delete {user}")
	public void removeUserFromRoom(@ChatVariable("user") User u, Chat r) {
		lastArguments = Arrays.asList(u, r);
		lastMethod = "removeUserFromRoom";	
	}
	
	@Exposed(value="ban {word}")
	public MessageResponse banWord(@ChatVariable("word") Word w, Addressable a) {
		lastArguments = Collections.singletonList(w);
		lastMethod = "banWord";
		return new MessageResponse(a, Message.of(Word.build("banned words: "+w.getText())));
	}
	
	@Exposed(value="attachment")
	public AttachmentResponse attachment(Addressable a) {
		lastArguments = Collections.emptyList();
		lastMethod = "attachment";
		byte[] bytes = { 1, 2, 3 };
		return new AttachmentResponse(a, bytes, "somefile", "bin");
	}
	
	@Exposed(description="Do blah with a form", value="form1")
	public FormResponse form1(Addressable a) {
		ButtonList bl = new ButtonList();	
		bl.add(new Button("go", Type.ACTION, "Do The Thing"));
		return new FormResponse(a, new TestObject(), true, bl, null);
	}
	
	@Exposed(value="form2")
	public TestObject form2(Addressable a) {
		return new TestObject();
	}
	
	@Exposed(value = "ok", formClass = TestObject.class)
	public TestObject ok(TestObject to) {
		return null;
	}
	
	@Exposed(value="throwsError")
	public TestObject throwsError() {
		throw new RuntimeException("Error123");
	}

}
