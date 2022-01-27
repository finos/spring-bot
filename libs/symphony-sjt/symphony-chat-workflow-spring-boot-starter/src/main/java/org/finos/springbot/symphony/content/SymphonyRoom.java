package org.finos.springbot.symphony.content;

import java.util.ArrayList;
import java.util.List;

import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.content.Chat;
import org.symphonyoss.Taxonomy;
import org.symphonyoss.TaxonomyElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.symphony.user.StreamID;

@Work(jsonTypeName = { "org.finos.symphony.toolkit.workflow.content.chat" }, index = false)
public class SymphonyRoom extends Taxonomy implements Chat, SymphonyAddressable {

	public SymphonyRoom() {
	}
	
	public SymphonyRoom(String name, String id) {
		super(createTaxonomy(name, id));
	}
	
	private static List<TaxonomyElement> createTaxonomy(String name, String streamId) {
		List<TaxonomyElement> out = new ArrayList<TaxonomyElement>();
		if (streamId != null) {
			out.add(new StreamID(streamId));
		} else {
			out.add(null);
		}
		if (name != null) {
		 	out.add(new RoomName(name));
		}  else {
			out.add(null);
		}
		
		return out;
	}


	@Override
	@JsonIgnore
	public String getName() {
		return fromTaxonomy(RoomName.class);
	}

	@Override
	public String toString() {
		return "SymphonyRoom [name=" + getName() + ", streamId=" + getKey() + "]";
	}

	@JsonIgnore
	public String getKey() {
		return fromTaxonomy(StreamID.class);
	}
}
