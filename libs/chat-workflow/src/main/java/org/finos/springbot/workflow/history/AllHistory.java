package org.finos.springbot.workflow.history;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.Tag;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class AllHistory implements History<Addressable>, ApplicationContextAware {
	
	private ApplicationContext ctx;
	
	private List<PlatformHistory<Addressable>> delegates;
	
	@SuppressWarnings("unchecked")
	private List<PlatformHistory<Addressable>> getDelegates() {
		if (delegates == null) {
			delegates = Arrays.stream(ctx.getBeanNamesForType(PlatformHistory.class))
				.map(s -> (PlatformHistory<Addressable>) ctx.getBean(s))
				.collect(Collectors.toList());
		}
		
		return delegates;
	}
	

	@Override
	public <X> Optional<X> getLastFromHistory(Class<X> type, Addressable address) {
		return getDelegates().stream()
				.filter(p -> p.isSupported(address))
				.map(p -> p.getLastFromHistory(type, address))
				.findFirst().orElse(Optional.empty());
	}


	@Override
	public <X> Optional<X> getLastFromHistory(Class<X> type, Tag t, Addressable address) {
		return getDelegates().stream()
				.filter(p -> p.isSupported(address))
				.map(p -> p.getLastFromHistory(type, t, address))
				.findFirst().orElse(Optional.empty());
	}


	@Override
	public <X> List<X> getFromHistory(Class<X> type, Addressable address, Instant since) {
		return getDelegates().stream()
				.filter(p -> p.isSupported(address))
				.flatMap(p -> p.getFromHistory(type, address, since).stream())
				.collect(Collectors.toList());
	}


	@Override
	public <X> List<X> getFromHistory(Class<X> type, Tag t, Addressable address, Instant since) {
		return getDelegates().stream()
				.filter(p -> p.isSupported(address))
				.flatMap(p -> p.getFromHistory(type, t, address, since).stream())
				.collect(Collectors.toList());
	}


	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;
	}

}
