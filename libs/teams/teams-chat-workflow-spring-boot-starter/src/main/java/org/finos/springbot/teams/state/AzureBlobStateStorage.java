package org.finos.springbot.teams.state;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.finos.springbot.entityjson.EntityJson;
import org.finos.springbot.teams.TeamsException;
import org.finos.springbot.teams.messages.MessageActivityHandler;
import org.finos.springbot.workflow.data.EntityJsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.StreamUtils;

import com.azure.core.http.rest.PagedIterable;
import com.azure.core.util.Context;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobStorageException;
import com.azure.storage.blob.models.TaggedBlobItem;
import com.azure.storage.blob.options.FindBlobsOptions;

/**
 * This uses Azure's blob storage to store the data-history of chats with the bot. 
 * 
 * @author rob@kite9.com
 *
 */
public class AzureBlobStateStorage extends AbstractStateStorage {
	
	private static final Logger LOG = LoggerFactory.getLogger(MessageActivityHandler.class);
	
	private final BlobContainerClient bcc;
	private final BlobServiceClient bsc;
	private final EntityJsonConverter ejc;
	private final String container;

	public AzureBlobStateStorage(BlobServiceClient bsc, EntityJsonConverter ejc, String container) {
		this.bcc = getContainerClient(bsc, container);
		this.bsc = bsc;
		this.ejc = ejc;
		this.container = container;
	}
	
	public String compileQuery(List<Filter> filters) {
		StringBuilder sb = new StringBuilder();
		sb.append("@container='");
		sb.append(container);
		sb.append("'" );
		filters.forEach(f -> {
			sb.append(" AND ");
			sb.append(getAzureTag(f.key));
			sb.append(f.operator);
			sb.append("'");
			sb.append(getAzureTag(""+f.value));
			sb.append("'");
			
		});
		
		return sb.toString();
	}

	private String getAzureTag(String s) {
		return s.replaceAll("[^0-9a-zA-Z]","_");
	}
	
	private String getAzurePath(String s) {
		return s.replaceAll("[^0-9a-zA-Z/]","_");
	}
	
	private Map<String, String> getAzureTags(Map<String, String> in) {
		return in.entrySet().stream()
			.collect(Collectors.toMap(e -> getAzureTag(e.getKey()), e -> getAzureTag(e.getValue())));
	}

	private static BlobContainerClient getContainerClient(BlobServiceClient bsc, String container) {
		BlobContainerClient bcc = bsc.getBlobContainerClient(container);
		if (!bcc.exists()) {
            try {
                bcc.create();
            } catch (Exception e) {
                throw new TeamsException("Couldn't create blob container for "+bsc.getAccountUrl());
            }
        }
			
		return bcc;
	}
	

	@Override
	public void store(String file, Map<String, String> tags, Map<String, Object> data) {
		file = getAzurePath(file);
		try {
			if ((tags != null) && (tags.size() > 0)) { 
				BlobClient bc = bcc.getBlobClient(file);
				
				String out = ejc.writeValue(data);
				byte[] dataBytes = out.getBytes();
				bc.upload(new ByteArrayInputStream(dataBytes), dataBytes.length);
				bc.setTags(getAzureTags(tags));
			} else {
				throw new TeamsException("Couldn't persist - no tags");
			}
		} catch (Exception e) {
			throw new TeamsException("Cannot persist data to "+file, e);
		}
		
	}

	private static final Map<String, Object> DONE = new HashMap<String, Object>();

	class DuplicateCheckingIterator implements Iterator<Map<String, Object>> {
		
		Set<String> done = new HashSet<String>();
		Iterator<TaggedBlobItem> underlying;
		Map<String, Object> next = null;
		
		public DuplicateCheckingIterator(Iterator<TaggedBlobItem> underlying) {
			super();
			this.underlying = underlying;
			consumeNext();
		}
		
		private void consumeNext() {
			// while loop is used in case we can't get certain blobs
			while (underlying.hasNext()) {
				next = convert(underlying.next());
				if (next == DONE) {
					next = null;
					return;
				} else if (next != null) {
					return;
				}
			} 
			
			next = null;
		}
		
		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public Map<String, Object> next() {
			if (next != null) {
				Map<String, Object> out = next;
				consumeNext();
				return out;
			} else {
				throw new NoSuchElementException();
			}
		}
		
		
		private Map<String, Object> convert(TaggedBlobItem tbi) {
			String name = tbi.getName();
			if (done.contains(name)) {
				return DONE;
			} 
			
			done.add(name);
			
			try {
				String json = StreamUtils.copyToString(bcc.getBlobClient(name).openInputStream(), StandardCharsets.UTF_8);
				EntityJson data = ejc.readValue(json);
				return data;
			} catch (Exception e) {
				LOG.error("Couldn't retreive blob: "+name);
				return null;
			}
		}
	}

	@Override
	public Iterable<Map<String, Object>> retrieve(List<Filter> tags, boolean singleResultOnly) {
		return new Iterable<Map<String,Object>>() {
			
			@Override
			public Iterator<Map<String, Object>> iterator() {
				try {
					String query = compileQuery(tags);
					FindBlobsOptions fbo = new FindBlobsOptions(query).setMaxResultsPerPage(singleResultOnly ? 1 : 20);
					PagedIterable<TaggedBlobItem> pi = bsc.findBlobsByTags(fbo, Duration.ofSeconds(5), Context.NONE);
					Iterator<TaggedBlobItem> underlying = pi.iterator();		
					return new DuplicateCheckingIterator(underlying);
				} catch (Exception e) {
					LOG.error("Couldn't retrieve from AzureBlobStorage with tags "+tags, e) ;
					return Collections.emptyIterator();
				}
			}
		};
		
	}	
	
	@Override
	public Optional<Map<String, Object>> retrieve(String file) {
		try {
			file = getAzurePath(file);
			BlobClient bc = bcc.getBlobClient(file);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bc.download(baos);
			if (baos.size() == 0) {
				return Optional.empty();
			}
			EntityJson out = ejc.readValue(new String(baos.toByteArray(), StandardCharsets.UTF_8));
			return Optional.of(out);
		} catch (BlobStorageException e) {
			if (e.getResponse().getStatusCode() == HttpStatus.NOT_FOUND.value()) {
				return Optional.empty();
			}
			LOG.warn("Couldn't retrieve: "+file, e);
			return Optional.empty();
		}
	}
}
