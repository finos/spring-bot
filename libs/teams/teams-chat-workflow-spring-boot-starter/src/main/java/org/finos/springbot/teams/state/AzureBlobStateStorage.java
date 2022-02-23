package org.finos.springbot.teams.state;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

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
			sb.append("' ");
			
		});
		
		return sb.toString();
	}

	private String getAzureTag(String s) {
		return s.replaceAll("[^0-9a-zA-Z]","_");
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
		try {
			if (tags.size() > 0) { 
				BlobClient bc = bcc.getBlobClient(file);
				
				String out = ejc.writeValue(data);
				byte[] dataBytes = out.getBytes();
				bc.upload(new ByteArrayInputStream(dataBytes), dataBytes.length);
				bc.setTags(tags);
			}
		} catch (Exception e) {
			throw new TeamsException("Cannot persist data to "+file, e);
		}
		
	}

	@Override
	public Iterable<Map<String, Object>> retrieve(List<Filter> tags, int maxPageSize) {
		String query = compileQuery(tags);
		FindBlobsOptions fbo = new FindBlobsOptions(query).setMaxResultsPerPage(maxPageSize);
		PagedIterable<TaggedBlobItem> pi = bsc.findBlobsByTags(fbo, Duration.ofSeconds(5), Context.NONE);	
		return new Iterable<Map<String,Object>>() {
			
			@Override
			public Iterator<Map<String, Object>> iterator() {
				Iterator<TaggedBlobItem> underyling = pi.iterator();
				return new Iterator<Map<String, Object>>() {

					@Override
					public boolean hasNext() {
						return underyling.hasNext();
					}

					@Override
					public Map<String, Object> next() {
						while (underyling.hasNext()) {
							TaggedBlobItem tbi = underyling.next();
							String name = tbi.getName();
							try {
								String json = StreamUtils.copyToString(bcc.getBlobClient(name).openInputStream(), StandardCharsets.UTF_8);
								EntityJson data = ejc.readValue(json);
								return data;
							} catch (IOException e) {
								LOG.error("Couldn't retreive blob: "+name);
							}
						}
						
						throw new NoSuchElementException();
					}
				};
			}
		};
	}

	@Override
	public Optional<Map<String, Object>> retrieve(String file) {
		try {
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
