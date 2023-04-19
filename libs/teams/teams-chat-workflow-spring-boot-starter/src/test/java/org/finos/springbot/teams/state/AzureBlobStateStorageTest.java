package org.finos.springbot.teams.state;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.finos.springbot.teams.MockTeamsConfiguration;
import org.finos.springbot.workflow.data.DataHandlerConfig;
import org.finos.springbot.workflow.data.EntityJsonConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.azure.core.http.rest.PagedFlux;
import com.azure.core.http.rest.PagedIterable;
import com.azure.core.http.rest.PagedResponseBase;
import com.azure.core.util.Context;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.TaggedBlobItem;
import com.azure.storage.blob.options.FindBlobsOptions;
import com.azure.storage.blob.specialized.BlobInputStream;

import reactor.core.publisher.Mono;

@SpringBootTest(classes = {
		MockTeamsConfiguration.class, 
		DataHandlerConfig.class
})
@ActiveProfiles("teams")
public class AzureBlobStateStorageTest extends AbstractStateStorageTest {

	private static final String THE_FILE_CONTENTS = "{\"a\": \"b\"}";
	
	@Autowired
	EntityJsonConverter ejc;
	
	@BeforeEach
	public void setup() {
		this.tss = new AzureBlobStateStorage(createBlobServiceClientMock(), ejc, "test");
	}

	BlobServiceClient bsc;
	BlobContainerClient bcc;
	BlobClient theFileBlobClient;
	BlobClient theOtherFileBlobClient;
	BlobClient nonFileBlobClient;
	BlobClient theFileWithSlashBlobClient;
	BlobClient theOtherFileWithSlashBlobClient;
	
	private BlobServiceClient createBlobServiceClientMock() {
		bsc = Mockito.mock(BlobServiceClient.class);
		bcc = Mockito.mock(BlobContainerClient.class);
		theFileBlobClient = Mockito.mock(BlobClient.class);
		theOtherFileBlobClient = Mockito.mock(BlobClient.class);
		nonFileBlobClient = Mockito.mock(BlobClient.class);
		theFileWithSlashBlobClient = Mockito.mock(BlobClient.class);
		theOtherFileWithSlashBlobClient  = Mockito.mock(BlobClient.class);
		Mockito.when(bsc.getBlobContainerClient("test"))
			.thenReturn(bcc);
		
		
		Mockito.when(bcc.exists()).thenReturn(true);
		
		Mockito.when(bcc.getBlobClient("thefile")).thenReturn(theFileBlobClient);
		Mockito.when(bcc.getBlobClient("theotherfile")).thenReturn(theOtherFileBlobClient);
		Mockito.when(bcc.getBlobClient("nonfile")).thenReturn(nonFileBlobClient);
		Mockito.when(bcc.getBlobClient("thefile/a")).thenReturn(theFileWithSlashBlobClient);
		Mockito.when(bcc.getBlobClient("thefile/b")).thenReturn(theOtherFileWithSlashBlobClient);
		
		return bsc;
	}

	@Test
	@Override
	public void testStoreWithTags() throws IOException {
		allowWritingToTheFile();
		allowReadingFromTheFile();
		setupBlobSearch(Collections.singletonMap("@container='test' AND tag='rob'", Collections.singletonList("thefile")));
		super.testStoreWithTags();
	}

	private void setupBlobSearch(Map<String, List<String>> results) {
		Mockito.when(bsc.findBlobsByTags(
				Mockito.any(),
				Mockito.eq(Duration.ofSeconds(5)), 
				Mockito.eq(Context.NONE)))
			.thenAnswer((a) -> {	
				FindBlobsOptions fbo = a.getArgument(0);
				List<String> res = results.get(fbo.getQuery());
				if (res != null) {
					return new PagedIterable<TaggedBlobItem>(
						new PagedFlux<TaggedBlobItem>(
							() -> Mono.just(new PagedResponseBase<Object, TaggedBlobItem>(null, 200, null, 
									res.stream().map(s -> new TaggedBlobItem("test", s)).collect(Collectors.toList()),
									null, null))));
				} else {
					System.out.println("no match for "+fbo.getQuery());
					return new PagedIterable<TaggedBlobItem>(
						new PagedFlux<TaggedBlobItem>(() -> Mono.empty()));
				}
			});
	}

	private void allowReadingFromTheFile() throws IOException {
		Mockito.when(theFileBlobClient.openInputStream()).thenAnswer((a) -> setupBlobInputStream());
	}
	
	private void allowReadingFromTheOtherFile() throws IOException {
		Mockito.when(theOtherFileBlobClient.openInputStream()).thenAnswer((a) -> setupBlobInputStream());
	}
	
	private void allowReadingFromTheSlashFile() throws IOException {
		Mockito.when(theFileWithSlashBlobClient.openInputStream()).thenAnswer((a) -> setupBlobInputStream());
	}
	
	private void allowReadingFromTheOtherSlashFile() throws IOException {
		Mockito.when(theOtherFileWithSlashBlobClient.openInputStream()).thenAnswer((a) -> setupBlobInputStream());
	}
	

	private BlobInputStream setupBlobInputStream() throws IOException {
		BlobInputStream bis = Mockito.mock(BlobInputStream.class);
		boolean isRead[] = { false };
		Mockito.when(bis.read(Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenAnswer((a2) -> {
			if (isRead[0]) {
				return -1;
			} else {
				isRead[0] = true;
				byte[] buffer = a2.getArgument(0);
				int len = THE_FILE_CONTENTS.getBytes().length;
				System.arraycopy(THE_FILE_CONTENTS.getBytes(), 0, buffer, 0, len);
				return len;
			}
		});
		return bis;
	}

	private void allowWritingToTheFile() {
		mockBlobOutputStream(theFileBlobClient);
	}
	
	private void allowWritingToTheOtherFile() {
		mockBlobOutputStream(theOtherFileBlobClient);
	}
	
	private void allowWritingToTheSlashFile() {
		mockBlobOutputStream(theFileWithSlashBlobClient);
	}
	
	private void allowWritingToTheOtherSlashFile() {
		mockBlobOutputStream(theOtherFileWithSlashBlobClient);
	}
	
	
	
	private void mockBlobOutputStream(BlobClient bc) {
		Mockito.doAnswer(new Answer<Void>() {
		    public Void answer(InvocationOnMock a) {
				try {
					OutputStream os = a.getArgument(0);
					OutputStreamWriter osw = new OutputStreamWriter(os);
					osw.write(THE_FILE_CONTENTS);
					osw.close();
				} catch (IOException e) {
				}
				
				return null;
		    }
		}).when(bc).download(Mockito.any());
	}

	@Override
	@Test
	public void testStoreWithTagDates() throws IOException {
		allowWritingToTheFile();
		allowReadingFromTheFile();
		allowWritingToTheOtherFile();
		allowReadingFromTheOtherFile();
		
		Map<String, List<String>> queries = new HashMap<>();
		queries.put("@container='test' AND date>='20220501'", Arrays.asList("thefile","theotherfile"));
		queries.put("@container='test' AND date>='20220501' AND name='rob'", Arrays.asList("thefile","theotherfile"));
		queries.put("@container='test' AND date<'20220601'", Arrays.asList("thefile","theotherfile"));
		setupBlobSearch(queries);
		
		super.testStoreWithTagDates();
	}
	
	@Override
	@Test
	public void testSlashStoreWithMultipleDirectories() throws IOException {
		allowWritingToTheSlashFile();
		allowWritingToTheOtherSlashFile();
		allowReadingFromTheSlashFile();
		allowReadingFromTheOtherSlashFile();
		
		Map<String, List<String>> queries = new HashMap<>();
		queries.put("@container='test' AND addressable='one' AND object1='tag'", Arrays.asList("thefile/a"));
		queries.put("@container='test' AND addressable='two' AND object2='tag'", Arrays.asList("thefile/b"));
		
		setupBlobSearch(queries);
		
		super.testSlashStoreWithMultipleDirectories();
	}
}
