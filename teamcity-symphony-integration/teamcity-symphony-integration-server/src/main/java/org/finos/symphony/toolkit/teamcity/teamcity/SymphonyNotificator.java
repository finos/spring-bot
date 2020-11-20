package org.finos.symphony.toolkit.teamcity.teamcity;

import jetbrains.buildServer.Build;
import jetbrains.buildServer.notification.Notificator;
import jetbrains.buildServer.notification.NotificatorRegistry;
import jetbrains.buildServer.responsibility.ResponsibilityEntry;
import jetbrains.buildServer.responsibility.TestNameResponsibilityEntry;
import jetbrains.buildServer.serverSide.Branch;
import jetbrains.buildServer.serverSide.SBuild;
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
import org.apache.log4j.Logger;
import org.finos.symphony.toolkit.teamcity.symphony.SymphonyWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class SymphonyNotificator implements Notificator {

    private static final Logger log = Logger.getLogger(SymphonyNotificator.class);

    private static final String type = "symphonyNotificator";

    private static final String symphonyChannelKey = "symphony.Channel";
    private static final String symphonyUsernameKey = "symphony.Username";
    private static final String symphonyUrlKey = "symphony.Url";
    private static final String symphonyVerboseKey = "symphony.Verbose";

    private static final PropertyKey symphonyChannel = new NotificatorPropertyKey(type, symphonyChannelKey);
    private static final PropertyKey symphonyUsername = new NotificatorPropertyKey(type, symphonyUsernameKey);
    private static final PropertyKey symphonyUrl = new NotificatorPropertyKey(type, symphonyUrlKey);
    private static final PropertyKey symphonyVerbose = new NotificatorPropertyKey(type, symphonyVerboseKey);

    private SBuildServer myServer;

    public SymphonyNotificator(NotificatorRegistry notificatorRegistry, SBuildServer server) {
        registerNotificatorAndUserProperties(notificatorRegistry);
        myServer = server;
    }

    @NotNull
    public String getNotificatorType() {
        return type;
    }

    @NotNull
    public String getDisplayName() {
        return "symphony Notifier";
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

        userPropertyInfos.add(new UserPropertyInfo(symphonyChannelKey, "#channel or @name"));
        userPropertyInfos.add(new UserPropertyInfo(symphonyUsernameKey, "Bot name"));
        userPropertyInfos.add(new UserPropertyInfo(symphonyUrlKey, "Webhook URL"));
        userPropertyInfos.add(new UserPropertyInfo(symphonyVerboseKey, "Verbose Messages"));

        return userPropertyInfos;
    }

    private void sendNotification(String project, String build, String statusText, String statusColor, Set<SUser> users, Build bt) {
        for (SUser user : users) {
            SymphonyWrapper SymphonyWrapper = getSymphonyWrapperWithUser(user);
            try {
                SymphonyWrapper.send(project, build, getBranch((SBuild)bt), statusText, statusColor, bt);
            }
            catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    private SymphonyWrapper getSymphonyWrapperWithUser(SUser user) {
        String channel = user.getPropertyValue(symphonyChannel);
        String username = user.getPropertyValue(symphonyUsername);
        String url = user.getPropertyValue(symphonyUrl);
        String verbose = user.getPropertyValue(symphonyVerbose);

        if (symphonyConfigurationIsInvalid(channel, username, url, verbose)) {
            log.error("Could not send symphony notification. The symphony channel, username, or URL was null. " +
                      "Double check your Notification settings");

            return new SymphonyWrapper();
        }

        boolean useAttachements = convertToBoolean(verbose);
        return constructSymphonyWrapper(channel, username, url, useAttachements);
    }

    private boolean symphonyConfigurationIsInvalid(String channel, String username, String url, String verbose) {
        return channel == null || username == null || url == null || verbose == null;
    }

    private SymphonyWrapper constructSymphonyWrapper(String channel, String username, String url, boolean useAttachements) {
        SymphonyWrapper SymphonyWrapper = new SymphonyWrapper(useAttachements);

        SymphonyWrapper.setChannel(channel);
        SymphonyWrapper.setUsername(username);
        SymphonyWrapper.setsymphonyUrl(url);
        SymphonyWrapper.setServerUrl(myServer.getRootUrl());

        return SymphonyWrapper;
    }

    private String getBranch(SBuild build) {
        Branch branch = build.getBranch();
        if (branch != null && branch.getName() != "<default>") {
            return branch.getDisplayName();
        } else {
            return "";
        }
    }

    private boolean convertToBoolean(String value) {
        String upper = value.toUpperCase();
        return "TRUE".equals(upper) || "YES".equals(upper);
    }
}
