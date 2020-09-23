package com.github.deutschebank.symphony.workflow;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.deutschebank.symphony.workflow.content.Addressable;
import com.github.deutschebank.symphony.workflow.content.Room;
import com.github.deutschebank.symphony.workflow.content.User;
import com.github.deutschebank.symphony.workflow.history.History;
import com.github.deutschebank.symphony.workflow.room.Rooms;

/**
 * Represents workflows which can use the state of the conversation, and knows which rooms it is in.
 * 
 * @author Rob Moffat
 *
 */
public abstract class AbstractWorkflow implements Workflow {
	
	protected List<History> historyProviders = new ArrayList<History>();;
	protected List<Rooms> roomsProviders = new ArrayList<>();
	private String namespace;
	private List<User> admins;
	private Rooms rooms;
	private History history;
	List<Room> keyRooms;

	public AbstractWorkflow(String namespace, List<User> admins, List<Room> keyRooms) {
		super();
		this.history = createHistoryDelegate();
		this.rooms = createRoomsDelegate();
		this.keyRooms = keyRooms;
		this.admins = admins;
		this.namespace = namespace;
	}

	@Override
	public String getNamespace() {
		return namespace;
	}

	@Override
	public Rooms getRoomsApi() {
		return rooms;
	}

	@Override
	public History getHistoryApi() {
		return history;
	}
	

	private Rooms createRoomsDelegate() {
		return new Rooms() {
			
			@Override
			public Set<Room> getAllRooms() {
				return roomsProviders.stream()
					.flatMap(rp -> rp.getAllRooms().stream())
					.collect(Collectors.toSet());
			}
			
			@Override
			public Room ensureRoom(Room r) {
				return roomsProviders.stream()
					.map(rp -> rp.ensureRoom(r))
					.findFirst()
					.orElseThrow(() -> new UnsupportedOperationException("Couldn't ensure room "+r));
			}
		};
	}

	private History createHistoryDelegate() {
		return new History() {

			@Override
			public <X> Optional<X> getLastFromHistory(Class<X> type, Addressable address) {
				return historyProviders.stream()
					.map(hp -> hp.getLastFromHistory(type, address))
					.filter(Optional::isPresent)
					.map(Optional::get)
					.findFirst();
			}

			@Override
			public <X> List<X> getFromHistory(Class<X> type, Addressable address, Instant since) {
				return historyProviders.stream()
					.flatMap(hp -> hp.getFromHistory(type, address, since).stream())
					.collect(Collectors.toList());
			}
			
		};
	} 

	@Override
	public List<Room> getKeyRooms() {
		return keyRooms;
	}

	@Override
	public List<User> getAdministrators() {
		return admins;
	}

	@Override
	public void registerHistoryProvider(History h) {
		historyProviders.add(h);
	}

	@Override
	public void registerRoomsProvider(Rooms r) {
		roomsProviders.add(r);
	}

	@Override
	public boolean hasMatchingCommand(String name, Room r) {
		return getCommands(r).stream()
			.filter(c -> c.getName().equals(name))
			.count() > 0;
	}

	
}
