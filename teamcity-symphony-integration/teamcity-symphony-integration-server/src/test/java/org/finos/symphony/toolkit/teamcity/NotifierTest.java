package org.finos.symphony.toolkit.teamcity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.agent.MessagesApi;

import jetbrains.buildServer.StatusDescriptor;
import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.notification.NotificatorRegistry;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.openapi.PagePlace;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class NotifierTest {



	public static final String EXPECTED_TEMPLATE = "<messageML>some template</messageML>";
	
	@Mock
	MessagesApi messages;
	
	
	@Mock
	SBuildServer server;
	
	@Mock
	WebControllerManager wcm;
	
	@Mock
	HttpServletResponse response;
	
	@Mock
	NotificatorRegistry notificatorRegistry;
	
	@Mock
	ResourceLoader rl;
	
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
	SymphonyAdminController controller;
	
	SymphonyNotificator notificator;
	
	SRunningBuild srb;
	
	Set<SUser> users;
	
	@BeforeEach
	public void setup() throws IOException {
		Mockito.when(response.getOutputStream()).thenReturn(new ServletOutputStream() {
			
			@Override
			public void write(int b) throws IOException {
				baos.write(b);
			}
			
			@Override
			public void setWriteListener(WriteListener listener) {	
			}
			
			@Override
			public boolean isReady() {
				return true;
			}
			
		});
		
		Files.copy(Paths.get("src/test/resources/symphony-config.json"), Paths.get("target/symphony-config.json"), StandardCopyOption.REPLACE_EXISTING);
		ServerPaths serverPaths = new ServerPaths("target", "target", "target","target");

		// test loading of file
		controller = new SymphonyAdminController(server, serverPaths, wcm, rl);
		notificator = new SymphonyNotificator(notificatorRegistry, controller, rl, server);
		
		Mockito.when(server.getRootUrl()).thenReturn("http://some.url");
		
		srb = Mockito.mock(SRunningBuild.class);
		Mockito.when(srb.getProjectId()).thenReturn("project-test-id");
		Mockito.when(srb.getFullName()).thenReturn("Some Test Build");
		Mockito.when(srb.getBuildNumber()).thenReturn("45");
		Mockito.when(srb.getStatusDescriptor()).thenReturn(new StatusDescriptor(Status.NORMAL, "hello"));
		Mockito.when(srb.getLogMessages(Mockito.anyInt(),Mockito.anyInt())).thenReturn(Arrays.asList("error: something", "warn: somethign else"));
		SUser mockUser = Mockito.mock(SUser.class);
		
		Mockito.when(mockUser.getPropertyValue(Mockito.any())).thenReturn("some-weird-stream-id");
		
		Mockito.when(rl.getResource(Mockito.anyString())).thenAnswer(a -> new InputStreamResource(NotifierTest.class.getResourceAsStream("/template.ftl")));
		
		users = Collections.singleton(mockUser);
		
 	}
	
	@Test
	public void testController() throws Exception {
		// test some load/save of the config properties
		Config c = controller.getConfig();
		Assertions.assertEquals("symphony.practice.bot1@list.db.com", c.getIdentityProperties().getEmail());
		Assertions.assertEquals(c.getTemplate(), EXPECTED_TEMPLATE);
		
		// test save
		controller.setConfig(c);
		Config c2 = controller.getConfig();
		ObjectMapper om = new ObjectMapper();
		Assertions.assertEquals(om.writeValueAsString(c), om.writeValueAsString(c2));
		
		// test doHandle
		ModelAndView fail = controller.handleInternal(c);
		Assertions.assertNotNull(fail);
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		fail.getView().render(fail.getModel(), request, response);
		
		Assertions.assertTrue(baos.size() > 1000);
	}
	
	@Test
	public void testAdminPage() throws Exception {
		Config c = controller.getConfig();
		PagePlace p = Mockito.mock(PagePlace.class);
		PagePlaces pp = Mockito.mock(PagePlaces.class);
		Mockito.when(pp.getPlaceById(Mockito.any())).thenReturn(p);
		PluginDescriptor pd = Mockito.mock(PluginDescriptor.class);
		Mockito.when(pd.getPluginResourcesPath(Mockito.anyString())).thenReturn("resources");
		
		SymphonyAdminPage ap = new SymphonyAdminPage(controller, pp, pd);
		HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
		Map<String, Object> model = new HashMap<>();
		ap.fillModel(model, req);
		Assertions.assertEquals(c.getCertificates(), model.get("certificates"));
	}
	
	@Test
	public void testNotificatorWithOverriddenTemplate() {
		Config c = controller.getConfig();
		c.setTemplate(EXPECTED_TEMPLATE);
		controller.setConfig(c);
		notificator.notifyBuildFailed(srb, users);
		notificator.notifyBuildFailedToStart(srb, users);
		notificator.notifyBuildSuccessful(srb, users);
		notificator.notifyBuildProbablyHanging(srb, users);
	}
	
	@Test
	public void testNotificatorWithBuildInTemplate() {
		Config c = controller.getConfig();
		c.setTemplate("");
		controller.setConfig(c);
		notificator.notifyLabelingFailed(srb, null, null, users);
		notificator.notifyBuildFailedToStart(srb, users);
		notificator.notifyBuildStarted(srb, users);
		notificator.notifyBuildFailing(srb, users);
	}
	
		
	
}
