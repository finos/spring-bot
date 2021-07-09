package org.finos.symphony.toolkit.workflow.help;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.finos.symphony.toolkit.workflow.annotations.Exposed;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.java.mapping.ChatHandlerMapping;
import org.finos.symphony.toolkit.workflow.java.mapping.ChatMapping;
import org.finos.symphony.toolkit.workflow.response.FormResponse;
import org.finos.symphony.toolkit.workflow.response.Response;

/**
 * Builds help menu from the @Exposed annotations.
 * 
 * @author rob@kite9.com
 *
 */
public class HelpController {

	List<ChatHandlerMapping<Exposed>> exposedHandlers;

	public HelpController(List<ChatHandlerMapping<Exposed>> exposedHandlers) {
		super();
		this.exposedHandlers = exposedHandlers;
	}

	public class AnnotationBasedCommandDescription implements CommandDescription {

		private Exposed e;

		public AnnotationBasedCommandDescription(Exposed e) {
			this.e = e;
		}

		@Override
		public String getDescription() {
			return e.description();
		}

		@Override
		public boolean addToHelp() {
			return e.addToHelp();
		}

		@Override
		public boolean isButton() {
			return e.isButton();
		}

		@Override
		public boolean isMessage() {
			return e.isMessage();
		}

		@Override
		public List<String> getExamples() {
			return Arrays.asList(e.value());
		}

	}

	@Exposed(value = "help")
	public Response handleHelp(Addressable a, User u) {
		List<CommandDescription> commands = exposedHandlers
				.stream()
				.flatMap(ec -> ec.getAllHandlers(a, u).stream())
				.map(hm -> convertToCommandDescriptions(hm))
				.collect(Collectors.toList());

		return new FormResponse(a, new HelpPage(commands), false);
	}

	private CommandDescription convertToCommandDescriptions(ChatMapping<Exposed> hm) {
		Exposed e = hm.getMapping();
		return new AnnotationBasedCommandDescription(e);
	}

}
