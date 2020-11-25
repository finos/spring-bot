package org.finos.symphony.toolkit.teamcity;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.compress.utils.Charsets;
import org.apache.log4j.Logger;
import org.apache.log4j.lf5.util.StreamUtils;
import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.spring.api.ApiInstance;
import org.finos.symphony.toolkit.spring.api.ApiInstanceFactory;
import org.finos.symphony.toolkit.spring.api.TokenManagingApiInstanceFactory;
import org.finos.symphony.toolkit.spring.api.builders.ApiBuilderFactory;
import org.finos.symphony.toolkit.spring.api.properties.IdentityProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.symphony.api.agent.MessagesApi;
import com.symphony.api.bindings.ApiBuilder;
import com.symphony.api.bindings.ConfigurableApiBuilder;
import com.symphony.api.bindings.jersey.JerseyApiBuilder;
import com.symphony.api.id.SymphonyIdentity;

import jetbrains.buildServer.Build;
import jetbrains.buildServer.notification.Notificator;
import jetbrains.buildServer.notification.NotificatorRegistry;
import jetbrains.buildServer.responsibility.ResponsibilityEntry;
import jetbrains.buildServer.responsibility.TestNameResponsibilityEntry;
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

    public SymphonyNotificator(NotificatorRegistry notificatorRegistry, SymphonyAdminController c, ResourceLoader rl) {
        registerNotificatorAndUserProperties(notificatorRegistry);
        this.c = c;
        this.rl = rl;
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
         sendNotification(sRunningBuild.getFullName(), sRunningBuild.getBuildNumber(), "failed: " + sRunningBuild.getStatusDescriptor().getText(), "danger", users, sRunningBuild);
    }

    public void notifyBuildFailedToStart(@NotNull SRunningBuild sRunningBuild, @NotNull Set<SUser> users) {
        sendNotification(sRunningBuild.getFullName(), sRunningBuild.getBuildNumber(), "failed to start", "danger", users, sRunningBuild);
    }

    public void notifyBuildSuccessful(@NotNull SRunningBuild sRunningBuild, @NotNull Set<SUser> users) {
        sendNotification(sRunningBuild.getFullName(), sRunningBuild.getBuildNumber(), "built successfully", "good", users, sRunningBuild);
    }

    public void notifyLabelingFailed(@NotNull Build build, @NotNull VcsRoot vcsRoot, @NotNull Throwable throwable, @NotNull Set<SUser> sUsers) {
        sendNotification(build.getFullName(), build.getBuildNumber(), "labeling failed", "danger", sUsers, build);
    }

    public void notifyBuildFailing(@NotNull SRunningBuild sRunningBuild, @NotNull Set<SUser> sUsers) {
        sendNotification(sRunningBuild.getFullName(), sRunningBuild.getBuildNumber(), "failing", "danger", sUsers, sRunningBuild);
    }

    public void notifyBuildProbablyHanging(@NotNull SRunningBuild sRunningBuild, @NotNull Set<SUser> sUsers) {
        sendNotification(sRunningBuild.getFullName(), sRunningBuild.getBuildNumber(), "probably hanging", "warning", sUsers, sRunningBuild);
    }

    public void notifyBuildStarted(@NotNull SRunningBuild sRunningBuild, @NotNull Set<SUser> sUsers) {
        sendNotification(sRunningBuild.getFullName(), sRunningBuild.getBuildNumber(), "started", "warning", sUsers, sRunningBuild);
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
			BuildData bd = new BuildData(project, build, statusText, statusColor);
			EntityJson json = new EntityJson();
			json.put("teamcity", bd);
			jsonString = c.getObjectMapper().writeValueAsString(json);
			log.warn("JSON: \n"+jsonString);
		} catch (JsonProcessingException e1) {
			log.error("Couldn't format JSON string ", e1);
			return;
		}
		
		try {
			messages = c.getAPI(MessagesApi.class);
		} catch (Exception e) {
			log.error("Couldn't send message to symphony ", e);
			return;
		}
		

		
		if (StringUtils.hasText(config.getTemplate())) {
			template = config.getTemplate();
		} else {
			template = asString(rl.getResource("classpath:/template.ftl"));
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
