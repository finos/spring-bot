package org.finos.springbot.teams.history;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.finos.springbot.entityjson.EntityJson;
import org.finos.springbot.teams.TeamsException;
import org.finos.springbot.teams.content.TeamsAddressable;
import org.finos.springbot.teams.messages.MessageActivityHandler;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.Tag;
import org.finos.springbot.workflow.data.EntityJsonConverter;
import org.finos.springbot.workflow.tags.HeaderDetails;
import org.finos.springbot.workflow.tags.TagSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import com.azure.core.http.rest.PagedIterable;
import com.azure.core.util.Context;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.TaggedBlobItem;
import com.azure.storage.blob.options.FindBlobsOptions;

/**
 * This uses Azure's blob storage to store the data-history of chats with the bot. 
 * 
 * @author rob@kite9.com
 *
 */
public class AzureBlobStorageTeamsHistory implements TeamsHistory {
	
	private static final String CHAT_KEY = "chat";
	private static final String TIMESTAMP_KEY = "timestamp";

	private static final Logger LOG = LoggerFactory.getLogger(MessageActivityHandler.class);
	
	private final BlobContainerClient bcc;
	private final BlobServiceClient bsc;
	private final EntityJsonConverter ejc;
	private final String container;

	public AzureBlobStorageTeamsHistory(BlobServiceClient bsc, EntityJsonConverter ejc, String container) {
		this.bcc = getContainerClient(bsc, container);
		this.bsc = bsc;
		this.ejc = ejc;
		this.container = container;
	}


	@Override
	public boolean isSupported(Addressable a) {
		return a instanceof TeamsAddressable;
	}


	@Override
 	public <X> Optional<X> getLastFromHistory(Class<X> type, TeamsAddressable address) {
		String expectedTag = getAzureTag(type);
		String directory = address.getKey();
		return getLast(type, expectedTag, directory);
	}


	protected <X> Optional<X> getLast(Class<X> type, String expectedTag, String directory) {
		try {
			FindBlobsOptions fbo = new FindBlobsOptions("@container='"+container+"' AND "+expectedTag+"='tag' AND "+CHAT_KEY+"='"+directory+"'").setMaxResultsPerPage(1);
			PagedIterable<TaggedBlobItem> pi = bsc.findBlobsByTags(fbo, Duration.ofSeconds(5), Context.NONE);
			Iterator<TaggedBlobItem> it = pi.iterator();
			if (it.hasNext()) {
				TaggedBlobItem tbi = it.next();
				String item = tbi.getName();
				return findObjectInItem(item, type);
			} else {
				return Optional.empty();
			}
		} catch (Exception e) {
			throw new TeamsException("Couldn't access blob storage", e);
		}
	}
	
	protected <X> List<X> getList(Class<X> type, String expectedTag, String directory, long sinceTimestamp) {
		try {
			FindBlobsOptions fbo = new FindBlobsOptions("@container='"+container+"' AND "+expectedTag+"='tag' AND "+CHAT_KEY+"='"+directory+"' AND "+TIMESTAMP_KEY+">='"+sinceTimestamp+"'").setMaxResultsPerPage(1);
			PagedIterable<TaggedBlobItem> pi = bsc.findBlobsByTags(fbo, Duration.ofSeconds(5), Context.NONE);			
			return StreamSupport.stream(pi.spliterator(), false)
				.map(tbi -> findObjectInItem(tbi.getName(), type))
				.filter(x -> x.isPresent())
				.map(x -> x.get())
				.collect(Collectors.toList());
		} catch (Exception e) {
			throw new TeamsException("Couldn't access blob storage", e);
		}
	}

	private String getAzureTag(Tag t) {
		return getAzureTag(t.getName());	
	}
	
	private <X> String getAzureTag(Class<X> type) {
		return getAzureTag(TagSupport.formatTag(type));
	}
	
	private String getAzureTag(String s) {
		return s.replace("-", "_");
	}


	@SuppressWarnings("unchecked")
	protected <X> Optional<X> findObjectInItem(String item, Class<X> type) {
		try {
			String json = StreamUtils.copyToString(bcc.getBlobClient(item).openInputStream(), StandardCharsets.UTF_8);
			EntityJson data = ejc.readValue(json);
			for (Object val : data.values()) {
				if (val.getClass().getName().equals(type.getName())) {
					return Optional.of((X) val);
				}
			}
			
			LOG.error("Should have found object of type "+type+" inside azure blob "+item);
			
			return Optional.empty();
		} catch (Exception e) {
			throw new TeamsException("Couldn't deserialize blob: "+item, e);
		}
	}


	@Override
	public <X> Optional<X> getLastFromHistory(Class<X> type, Tag t, TeamsAddressable address) {
		return getLast(type, getAzureTag(t), address.getKey());
	}


	@Override
	public <X> List<X> getFromHistory(Class<X> type, TeamsAddressable address, Instant since) {
		return getList(type, getAzureTag(type), address.getKey(), since.getEpochSecond());
	}


	@Override
	public <X> List<X> getFromHistory(Class<X> type, Tag t, TeamsAddressable address, Instant since) {
		return getList(type, getAzureTag(t), address.getKey(), since.getEpochSecond());
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
	public void store(TeamsAddressable a, Map<String, Object> data) {
		try {
			Map<String, String> tags = getTags(data, a);
			if (tags.size() > 0) { 
				String blobId = createBlobId();
				String directory = a.getKey();
				BlobClient bc = bcc.getBlobClient(directory+"/"+blobId);
				
				String out = ejc.writeValue(data);
				byte[] dataBytes = out.getBytes();
				bc.upload(new ByteArrayInputStream(dataBytes), dataBytes.length);
				bc.setTags(tags);
			}
		} catch (Exception e) {
			throw new TeamsException("Cannot persist data to "+a, e);
		}
		
	}


	/**
	 * The blob ID should be alphabetically ordered so that newer blobs are 
	 * earlier in the alphabet.  That's quite tricky, so subtracing from the long of thre current time
	 * @return
	 */
	private String createBlobId() {
		long ts = Long.MAX_VALUE;
		ts = ts - System.currentTimeMillis();
		
		return ""+ts+"-"+UUID.randomUUID().toString();
	}


	private Map<String, String> getTags(Map<String, Object> data, Addressable a) {
		HeaderDetails hd = (HeaderDetails) data.get(HeaderDetails.KEY);
		Map<String, String> out = new HashMap<String, String>();
		out.put(TIMESTAMP_KEY, ""+ System.currentTimeMillis());
		if (hd != null) {
			for (String entry : hd.getTags()) {
				out.put(getAzureTag(entry), "tag");
			}
		}
		
		out.put(CHAT_KEY, a.getKey());		
		return out;
	}
}
