package org.finos.symphony.toolkit.workflow.sources.symphony.content;

import java.util.ArrayList;
import java.util.List;

import org.finos.symphony.toolkit.workflow.content.Chat;
import org.symphonyoss.Taxonomy;
import org.symphonyoss.TaxonomyElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.symphony.user.StreamID;

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
		}
		if (name != null) {
			out.add(new RoomName(name));
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
		return "SymphonyRoom [name=" + getName() + ", streamId=" + getStreamId() + "]";
	}

	@JsonIgnore
	public String getStreamId() {
		return fromTaxonomy(StreamID.class);
	}

	
}
