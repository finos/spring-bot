package org.finos.symphony.toolkit.teamcity;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.apache.commons.compress.utils.Charsets;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.log4j.Logger;
import org.finos.springbot.entityjson.EntityJson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import com.symphony.api.agent.MessagesApi;

import jetbrains.buildServer.Build;
import jetbrains.buildServer.notification.Notificator;
import jetbrains.buildServer.notification.NotificatorRegistry;
import jetbrains.buildServer.responsibility.ResponsibilityEntry;
import jetbrains.buildServer.responsibility.TestNameResponsibilityEntry;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.serverSide.STest;
import jetbrains.buildServer.serverSide.UserPropertyInfo;
import jetbrains.buildServer.serverSide.mute.MuteInfo;
import jetbrains.buildServer.serverSide.problems.BuildProblemInfo;
import jetbrains.buildServer.tests.TestName;
import jetbrains.buildServer.users.NotificatorPropertyKey;
import jetbrains.buildServer.users.PropertyKey;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.vcs.VcsRoot;

public class SymphonyNotificator implements Notificator {

    private static final Logger log = Logger.getLogger(SymphonyNotificator.class);

    private static final String type = "SymphonyNotificator";

    private static final String symphonyStreamIdKey = "symphony.streamId";

    private static final PropertyKey symphonyStreamId = new NotificatorPropertyKey(type, symphonyStreamIdKey);
    
    private SymphonyAdminController c;
    
    private ResourceLoader rl;
    
    private SBuildServer sBuildServer;

    public SymphonyNotificator(NotificatorRegistry notificatorRegistry, SymphonyAdminController c, ResourceLoader rl, SBuildServer sBuildServer) {
        registerNotificatorAndUserProperties(notificatorRegistry);
        this.c = c;
        this.rl = rl;
        this.sBuildServer = sBuildServer;
        log.warn("SYMPHONY: Constructed notificator ");
    }

    @NotNull
    public String getNotificatorType() {
        return type;
    }

    @NotNull
    public String getDisplayName() {
        return "Symphony Notifier";
    }

    public void notifyBuildFailed(@NotNull SRunningBuild sRunningBuild, @NotNull Set<SUser> users) {
         sendNotification(sRunningBuild.getFullName(), sRunningBuild.getBuildNumber(), "failed: " + sRunningBuild.getStatusDescriptor().getText(), "red", users, sRunningBuild);
    }

    public void notifyBuildFailedToStart(@NotNull SRunningBuild sRunningBuild, @NotNull Set<SUser> users) {
        sendNotification(sRunningBuild.getFullName(), sRunningBuild.getBuildNumber(), "failed to start", "red", users, sRunningBuild);
    }

    public void notifyBuildSuccessful(@NotNull SRunningBuild sRunningBuild, @NotNull Set<SUser> users) {
        sendNotification(sRunningBuild.getFullName(), sRunningBuild.getBuildNumber(), "built successfully", "green", users, sRunningBuild);
    }

    public void notifyLabelingFailed(@NotNull Build build, @NotNull VcsRoot vcsRoot, @NotNull Throwable throwable, @NotNull Set<SUser> sUsers) {
        sendNotification(build.getFullName(), build.getBuildNumber(), "labeling failed", "red", sUsers, build);
    }

    public void notifyBuildFailing(@NotNull SRunningBuild sRunningBuild, @NotNull Set<SUser> sUsers) {
        sendNotification(sRunningBuild.getFullName(), sRunningBuild.getBuildNumber(), "failing", "red", sUsers, sRunningBuild);
    }

    public void notifyBuildProbablyHanging(@NotNull SRunningBuild sRunningBuild, @NotNull Set<SUser> sUsers) {
        sendNotification(sRunningBuild.getFullName(), sRunningBuild.getBuildNumber(), "probably hanging", "yellow", sUsers, sRunningBuild);
    }

    public void notifyBuildStarted(@NotNull SRunningBuild sRunningBuild, @NotNull Set<SUser> sUsers) {
        sendNotification(sRunningBuild.getFullName(), sRunningBuild.getBuildNumber(), "started", "yellow", sUsers, sRunningBuild);
    }

    public void notifyResponsibleChanged(@NotNull SBuildType sBuildType, @NotNull Set<SUser> sUsers) {

    }

    public void notifyResponsibleAssigned(@NotNull SBuildType sBuildType, @NotNull Set<SUser> sUsers) {

    }

    public void notifyResponsibleChanged(@Nullable TestNameResponsibilityEntry testNameResponsibilityEntry, @NotNull TestNameResponsibilityEntry testNameResponsibilityEntry2, @NotNull SProject sProject, @NotNull Set<SUser> sUsers) {

    }

    public void notifyResponsibleAssigned(@Nullable TestNameResponsibilityEntry testNameResponsibilityEntry, @NotNull TestNameResponsibilityEntry testNameResponsibilityEntry2, @NotNull SProject sProject, @NotNull Set<SUser> sUsers) {

    }

    public void notifyResponsibleChanged(@NotNull Collection<TestName> testNames, @NotNull ResponsibilityEntry responsibilityEntry, @NotNull SProject sProject, @NotNull Set<SUser> sUsers) {

    }

    public void notifyResponsibleAssigned(@NotNull Collection<TestName> testNames, @NotNull ResponsibilityEntry responsibilityEntry, @NotNull SProject sProject, @NotNull Set<SUser> sUsers) {

    }

    public void notifyBuildProblemResponsibleAssigned(@NotNull Collection<BuildProblemInfo> buildProblemInfos, @NotNull ResponsibilityEntry responsibilityEntry, @NotNull SProject sProject, @NotNull Set<SUser> sUsers) {

    }

    public void notifyBuildProblemResponsibleChanged(@NotNull Collection<BuildProblemInfo> buildProblemInfos, @NotNull ResponsibilityEntry responsibilityEntry, @NotNull SProject sProject, @NotNull Set<SUser> sUsers) {

    }

    public void notifyTestsMuted(@NotNull Collection<STest> sTests, @NotNull MuteInfo muteInfo, @NotNull Set<SUser> sUsers) {

    }

    public void notifyTestsUnmuted(@NotNull Collection<STest> sTests, @NotNull MuteInfo muteInfo, @Nullable SUser sUser, @NotNull Set<SUser> sUsers) {

    }

    public void notifyBuildProblemsMuted(@NotNull Collection<BuildProblemInfo> buildProblemInfos, @NotNull MuteInfo muteInfo, @NotNull Set<SUser> sUsers) {

    }

    public void notifyBuildProblemsUnmuted(@NotNull Collection<BuildProblemInfo> buildProblemInfos, @NotNull MuteInfo muteInfo, @Nullable SUser sUser, @NotNull Set<SUser> sUsers) {

    }

    private void registerNotificatorAndUserProperties(NotificatorRegistry notificatorRegistry) {
        ArrayList<UserPropertyInfo> userPropertyInfos = getUserPropertyInfosList();
        notificatorRegistry.register(this, userPropertyInfos);
    }

    private ArrayList<UserPropertyInfo> getUserPropertyInfosList() {
        ArrayList<UserPropertyInfo> userPropertyInfos = new ArrayList<UserPropertyInfo>();

        userPropertyInfos.add(new UserPropertyInfo(symphonyStreamIdKey, "Stream ID To Report To"));

        return userPropertyInfos;
    }
    
    public static String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), Charsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
        	log.error("Couldn't load classpath template: ", e);
            throw new UncheckedIOException(e);
        }
    }

	private void sendNotification(String project, String build, String statusText, String statusColor, Set<SUser> users, Build bt) {
		
		MessagesApi messages;
		String jsonString;
		String template;
		Config config = c.getConfig();
		
		try {
			String details = bt.getLogMessages(0, Integer.MAX_VALUE).stream()
					.filter(m -> !m.contains("errorreport"))
					.filter(m -> m.contains("error"))
					.map(a -> HtmlUtils.htmlEscape(a))
					.reduce("", (a, b) -> a + "<br/>" + b);
			
			String url = sBuildServer.getRootUrl() + "/project.html?projectId=" + URIUtil.encodeQuery(bt.getProjectId());
			
			BuildData bd = new BuildData(project, build, statusText, statusColor, url, details);
			EntityJson json = new EntityJson();
			json.put("teamcity", bd);
			jsonString = c.getObjectMapper().writeValueAsString(json);
			log.warn("JSON: \n"+jsonString);
		} catch (Exception e1) {
			log.error("Couldn't format string ", e1);
			return;
		}
		
		try {
			messages = c.getAPI(MessagesApi.class);
		} catch (Exception e) {
			log.error("Couldn't aquire symphony API ", e);
			return;
		}
		

		
		if (StringUtils.hasText(config.getTemplate())) {
			template = config.getTemplate();
			log.warn("Using custom symphony template");
		} else {
			template = asString(rl.getResource("classpath:/template.ftl"));
			log.warn("Using built-in symphony template");
		}
		
		for (SUser sUser : users) {
			String streamId = sUser.getPropertyValue(symphonyStreamId);
			log.warn("Sending notification to Symphony on "+streamId);
			if (StringUtils.hasText(streamId)) {
				try {
					messages.v4StreamSidMessageCreatePost(null, streamId, template, jsonString, null, null, null, null);
				} catch (Exception e) {
					log.error("Couldn't send message to symphony ", e);
				}
			}
		}
	}

}
