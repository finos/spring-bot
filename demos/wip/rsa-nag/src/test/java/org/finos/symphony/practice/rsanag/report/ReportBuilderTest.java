package org.finos.symphony.practice.rsanag.report;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.finos.symphony.practice.rsanag.Config;
import org.finos.symphony.practice.rsanag.report.Report;
import org.finos.symphony.practice.rsanag.report.ReportBuilder;
import org.finos.symphony.toolkit.workflow.content.RoomDef;
import org.finos.symphony.toolkit.workflow.history.History;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.symphony.api.agent.AuditTrailApi;
import com.symphony.api.model.Pagination;
import com.symphony.api.model.PaginationCursors;
import com.symphony.api.model.UserSystemInfo;
import com.symphony.api.model.UserSystemInfo.StatusEnum;
import com.symphony.api.model.V1AuditTrailInitiatorList;
import com.symphony.api.model.V1AuditTrailInitiatorResponse;
import com.symphony.api.model.V2UserAttributes;
import com.symphony.api.model.V2UserDetail;
import com.symphony.api.model.V2UserDetailList;
import com.symphony.api.model.V2UserKeyRequest;
import com.symphony.api.pod.UserApi;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
public class ReportBuilderTest {

	@MockBean
	AuditTrailApi auditTrailApi;
	
	@MockBean
	UserApi userApi;
	
	@Autowired
	ReportBuilder reportBuilder; 
	
	@MockBean
	History history;
	
	ObjectMapper out = new ObjectMapper();

	public ReportBuilderTest() {
		out.registerModule(new JavaTimeModule());
	}
	
	@BeforeEach
	public void setup() {
		V2UserDetailList out = new V2UserDetailList();
		out.add(new V2UserDetail()
			.userAttributes(
				new V2UserAttributes()
					.emailAddress("bot1@example.com")
					.displayName("Bot 1")
					.currentKey(new V2UserKeyRequest().key("abc123")))
			.userSystemInfo(
				new UserSystemInfo()
					.id(5647l)
					.status(StatusEnum.ENABLED)
					.createdDate(1610000000000l))); // 7/1/2021 (still in date as of 4/3/2021)
 		
		out.add(new V2UserDetail()
				.userAttributes(
					new V2UserAttributes()
						.emailAddress("bot2@example.com")
						.displayName("Bot 2")
						.currentKey(new V2UserKeyRequest().key("def8462")))
				.userSystemInfo(
					new UserSystemInfo()
						.id(999l)
						.status(StatusEnum.ENABLED)
						.createdDate(1514321549000l)
						.lastUpdatedDate(1514321549000l))); // 26/12/2017 (expired, but renewed recently)
			
		out.add(new V2UserDetail()
				.userAttributes(
					new V2UserAttributes()
						.emailAddress("bot3@example.com")
						.displayName("Bot 3")
						.currentKey(new V2UserKeyRequest().key("judfhggd")))
				.userSystemInfo(
					new UserSystemInfo()
						.id(123l)
						.status(StatusEnum.DISABLED)
						.createdDate(1510000000000l)
						.lastUpdatedDate(1510000000000l))); //6/11/2017 (expired, not renewed)
		
		out.add(new V2UserDetail()
				.userAttributes(
					new V2UserAttributes()
						.emailAddress("bot4@example.com")
						.displayName("Bot 4 Certs"))
				.userSystemInfo(
					new UserSystemInfo()
						.status(StatusEnum.ENABLED)
						.id(666l))); //6/11/2017 - no RSA key
		
		out.add(new V2UserDetail()
				.userAttributes(
					new V2UserAttributes()
						.emailAddress("bot5@example.com")
						.displayName("Bot 5")
						.currentKey(new V2UserKeyRequest().key("hhhh")))
				.userSystemInfo(
					new UserSystemInfo()
						.id(777l)
						.status(StatusEnum.ENABLED)
						.createdDate(1583860463000l))); //10/3/2020 (expiring soon)
		
		V1AuditTrailInitiatorList firstAuditPage = new V1AuditTrailInitiatorList();
		firstAuditPage.items(Arrays.asList(
			new V1AuditTrailInitiatorResponse()
				.actionName("rsaKeyAdded")
				.timestamp("1604510063000")));  // 4/11/2020
		firstAuditPage.pagination(new Pagination().cursors(new PaginationCursors().after("1")));

		V1AuditTrailInitiatorList secondAuditPage = new V1AuditTrailInitiatorList();
		secondAuditPage
			.pagination(new Pagination().cursors(new PaginationCursors()))
			.items(new ArrayList<V1AuditTrailInitiatorResponse>());

		
		Mockito.when(userApi.v2AdminUserListGet(Mockito.isNull(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(out);
		Mockito.when(auditTrailApi.v1AudittrailPrivilegeduserGet(
			Mockito.isNull(), Mockito.isNull(), 
			Mockito.anyLong(), Mockito.anyLong(), 
			Mockito.isNull(), Mockito.isNull(), 
			Mockito.anyInt(), Mockito.isNull(), Mockito.isNull())).thenReturn(firstAuditPage);
		
		Mockito.when(auditTrailApi.v1AudittrailPrivilegeduserGet(
				Mockito.isNull(), Mockito.isNull(), 
				Mockito.anyLong(), Mockito.anyLong(), 
				Mockito.isNull(), Mockito.matches("1"), 
				Mockito.anyInt(), Mockito.isNull(), Mockito.isNull())).thenReturn(secondAuditPage);
	}
	
	@Test
	public void testNewReportBuilder() throws Exception {
		Mockito.when(history.getLastFromHistory(Mockito.any(), Mockito.any())).thenReturn(Optional.empty());
		Instant reportTime = LocalDate.of(2021, 2, 7).atStartOfDay().toInstant(ZoneOffset.UTC);
		Report r = reportBuilder.generateReportInner(new RoomDef("", "", false, Config.RSA_KEY_AUDIT_ROOM), reportTime);
		String json = out.writerWithDefaultPrettyPrinter().writeValueAsString(r); 
		System.out.println(json);
		
		// checks
		String expected = expectedFirstReport();
		Assertions.assertEquals(expected, json);
		Mockito.verify(auditTrailApi, Mockito.times(53)).v1AudittrailPrivilegeduserGet(
				Mockito.isNull(), Mockito.isNull(), 
				Mockito.anyLong(), Mockito.anyLong(), 
				Mockito.isNull(), Mockito.isNull(), 
				Mockito.anyInt(), Mockito.isNull(), Mockito.isNull());
		Mockito.verify(auditTrailApi, Mockito.times(53)).v1AudittrailPrivilegeduserGet(
				Mockito.isNull(), Mockito.isNull(), 
				Mockito.anyLong(), Mockito.anyLong(), 
				Mockito.isNull(), Mockito.matches("1"), 
				Mockito.anyInt(), Mockito.isNull(), Mockito.isNull());
		
		Mockito.reset(history);
	}

	public String expectedFirstReport() throws IOException {
		return StreamUtils.copyToString(ReportBuilderTest.class.getResourceAsStream("/expected-report-1.json"),StandardCharsets.UTF_8);
	}
	
	
	@Test
	public void testFollowingReportBuilder() throws Exception {
		
		Report old = out.readValue(expectedFirstReport(), Report.class);
		Mockito.when(history.getLastFromHistory(Mockito.any(), Mockito.any())).thenReturn(Optional.of(old));
		Instant reportTime = LocalDate.of(2021, 3, 7).atStartOfDay().toInstant(ZoneOffset.UTC);
		
		Report r = reportBuilder.generateReportInner(new RoomDef("", "", false, Config.RSA_KEY_AUDIT_ROOM), reportTime);
		String json = out.writerWithDefaultPrettyPrinter().writeValueAsString(r); 
		System.out.println(json);
		
		// checks
		String expected = StreamUtils.copyToString(ReportBuilderTest.class.getResourceAsStream("/expected-report-2.json"),StandardCharsets.UTF_8);
		Assertions.assertEquals(expected, json);
		Mockito.verify(auditTrailApi, Mockito.times(4)).v1AudittrailPrivilegeduserGet(
				Mockito.isNull(), Mockito.isNull(), 
				Mockito.anyLong(), Mockito.anyLong(), 
				Mockito.isNull(), Mockito.isNull(), 
				Mockito.anyInt(), Mockito.isNull(), Mockito.isNull());
		Mockito.reset(history);
	}
	
	
}
