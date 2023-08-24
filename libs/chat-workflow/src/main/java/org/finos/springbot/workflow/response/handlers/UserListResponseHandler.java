package org.finos.springbot.workflow.response.handlers;

import java.util.Collections;
import java.util.stream.Collectors;

import org.finos.springbot.workflow.annotations.RequiresUserList;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.conversations.AllConversations;
import org.finos.springbot.workflow.form.DropdownList;
import org.finos.springbot.workflow.form.DropdownList.Item;
import org.finos.springbot.workflow.response.Response;
import org.finos.springbot.workflow.response.WorkResponse;

/**
 * When returning a {@link WorkResponse} to the user, this gathers up a list 
 * of users in the current chat that can be used to populate a drop-down.
 * 
 * @author rob@kite9.com
 *
 */
public class UserListResponseHandler implements ResponseHandler<Void> {
	
	AllConversations conversations;
	
	
	public UserListResponseHandler(AllConversations conversations) {
		super();
		this.conversations = conversations;
	}

	@Override
	public Void apply(Response t) {
		if (t instanceof WorkResponse) {
			WorkResponse wr = (WorkResponse) t;
			Class<?> c = ((WorkResponse) t).getFormClass();
			
			RequiresUserList rcl = c.getAnnotation(RequiresUserList.class);
			if (rcl != null) {
				
				Addressable a = t.getAddress();
				
				if (a instanceof User) {
					// writing to a single user
					Item i = new Item(a.getKey(), ((User)a).getName());
					wr.getData().put(rcl.key(), new DropdownList(Collections.singletonList(i)));
				} else if (a instanceof Chat) {
					wr.getData().put(rcl.key(), new DropdownList(
						conversations.getChatMembers((Chat) a).stream()
							.map(uu -> new Item(uu.getKey(), uu.getName()))
							.collect(Collectors.toList())));
				}
			}
		}
		
		return null;
	}


	@Override
	public int getOrder() {
		return MEDIUM_PRIORITY;
	}

}
