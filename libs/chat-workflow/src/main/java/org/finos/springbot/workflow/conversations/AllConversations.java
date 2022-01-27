package org.finos.springbot.workflow.conversations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class AllConversations implements Conversations<Chat, User>, ApplicationContextAware {
	
	private ApplicationContext ctx;
	
	private List<PlatformConversations<Chat, User>> delegates;
	
	@SuppressWarnings("unchecked")
	private List<PlatformConversations<Chat, User>> getDelegates() {
		if (delegates == null) {
			delegates = Arrays.stream(ctx.getBeanNamesForType(PlatformConversations.class))
				.map(s -> (PlatformConversations<Chat,User>) ctx.getBean(s))
				.collect(Collectors.toList());
		}
		
		return delegates;
	}
	

	@Override
	public Set<Addressable> getAllAddressables() {
		return getDelegates().stream()
			.flatMap(c -> c.getAllAddressables().stream())
			.collect(Collectors.toSet());
	}

	@Override
	public Set<Chat> getAllChats() {
		return getDelegates().stream()
				.flatMap(c -> c.getAllChats().stream())
				.collect(Collectors.toSet());
	}

	@Override
	public Chat getExistingChat(String name) {
		return getDelegates().stream()
				.map(c -> c.getExistingChat(name))
				.filter(c -> c!=null)
				.findFirst()
				.orElse(null);
	}

	@Override
	public Chat ensureChat(Chat r, List<User> users, Map<String, Object> meta) {
		return getDelegates().stream()
				.filter(p -> p.isSupported(r))
				.map(p -> p.ensureChat(r, users, meta))
				.findFirst()
				.orElse(null);
	}

	@Override
	public List<User> getChatMembers(Chat r) {
		return getDelegates().stream()
				.filter(p -> p.isSupported(r))
				.flatMap(p -> p.getChatMembers(r).stream())
				.collect(Collectors.toList());
	}

	@Override
	public List<User> getChatAdmins(Chat r) {
		return getDelegates().stream()
				.filter(p -> p.isSupported(r))
				.flatMap(p -> p.getChatAdmins(r).stream())
				.collect(Collectors.toList());
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;
	}


	@Override
	public boolean isThisBot(User u) {
		return getDelegates().stream()
			.filter(d -> d.isThisBot(u))
			.count() > 0;
	}

}
