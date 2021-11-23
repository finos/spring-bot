package org.finos.springbot.teams.history;

import java.io.ByteArrayInputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.finos.springbot.teams.TeamsException;
import org.finos.springbot.teams.content.TeamsAddressable;
import org.finos.springbot.teams.messages.MessageActivityHandler;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.Tag;
import org.finos.springbot.workflow.tags.HeaderDetails;
import org.finos.springbot.workflow.tags.TagSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.core.http.rest.PagedIterable;
import com.azure.core.util.Context;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.TaggedBlobItem;
import com.azure.storage.blob.options.FindBlobsOptions;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This uses Azure's blob storage to store the data-history of chats with the bot. 
 * 
 * @author rob@kite9.com
 *
 */
public class AzureBlobStorageTeamsHistory implements TeamsHistory {
	
	private static final Logger LOG = LoggerFactory.getLogger(MessageActivityHandler.class);
	
	private static final TypeReference<Map<String, Object>> DATA_TYPE_REFERENCE = new TypeReference<Map<String, Object>>() {};

	private final BlobContainerClient bcc;
	private final BlobServiceClient bsc;
	private final ObjectMapper om;
	private final String container;

	public AzureBlobStorageTeamsHistory(BlobServiceClient bsc, ObjectMapper om, String container) {
		this.bcc = getContainerClient(bsc, container);
		this.bsc = bsc;
		this.om = om;
		this.container = container;
	}


	@Override
	public boolean isSupported(Addressable a) {
		return a instanceof TeamsAddressable;
	}


	@Override
 	public <X> Optional<X> getLastFromHistory(Class<X> type, TeamsAddressable address) {
		try {
			String expectedTag = TagSupport.formatTag(type).replace("-", "_");
			String directory = address.getKey();
			FindBlobsOptions fbo = new FindBlobsOptions("@container='"+container+"' AND "+expectedTag+"='tag'").setMaxResultsPerPage(1);
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


	@SuppressWarnings("unchecked")
	protected <X> Optional<X> findObjectInItem(String item, Class<X> type) throws Exception {
		Map<String, Object> data = om.readValue(bcc.getBlobClient(item).openInputStream(), DATA_TYPE_REFERENCE);
		for (Object val : data.values()) {
			if (val.getClass().getName().equals(type.getName())) {
				return Optional.of((X) val);
			}
		}
		
		LOG.error("Should have found object of type "+type+" inside azure blob "+item);
		
		return Optional.empty();
	}


	@Override
	public <X> Optional<X> getLastFromHistory(Class<X> type, Tag t, TeamsAddressable address) {
		return Optional.empty();
	}


	@Override
	public <X> List<X> getFromHistory(Class<X> type, TeamsAddressable address, Instant since) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public <X> List<X> getFromHistory(Class<X> type, Tag t, TeamsAddressable address, Instant since) {
		// TODO Auto-generated method stub
		return null;
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
			Map<String, String> tags = getTags(data);
			if (tags.size() > 0) { 
				String blobId = UUID.randomUUID().toString();
				String directory = a.getKey();
				BlobClient bc = bcc.getBlobClient(directory+"/"+blobId);
				
				byte[] dataBytes = om.writeValueAsBytes(data);
				bc.upload(new ByteArrayInputStream(dataBytes), dataBytes.length);
				bc.setTags(tags);
			}
		} catch (Exception e) {
			throw new TeamsException("Cannot persist data to "+a, e);
		}
		
	}


	private Map<String, String> getTags(Map<String, Object> data) {
		HeaderDetails hd = (HeaderDetails) data.get(HeaderDetails.KEY);
		Map<String, String> out = new HashMap<String, String>();
		out.put("timestamp", ""+ System.currentTimeMillis());
		if (hd != null) {
			for (String entry : hd.getTags()) {
				out.put(entry.replace("-", "_"), "tag");
			}
		}
		
		return out;
	}
}
