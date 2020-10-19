package com.github.deutschebank.symphonyp.maven;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.BuildSuccess;
import org.apache.maven.execution.BuildSummary;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.CiManagement;
import org.apache.maven.model.Notifier;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.id.SymphonyIdentity;

@Component(role = AbstractMavenLifecycleParticipant.class)
public class SymphonyBuildReporter extends AbstractMavenLifecycleParticipant {
    private static final String NONE_PROXY = "none";

    private static final String SYMPHONY_REPORTER_RECIPIENTS = "symphony.reporter.recipients";
    private static final String SYMPHONY_REPORTER_HASHTAGS = "symphony.reporter.hashtags";
    private static final String SYMPHONY_REPORTER_IDENTITY = "symphony.reporter.identity";
    private static final String SYMPHONY_REPORTER_TRUST_STORE_JKS = "symphony.reporter.trust-store.jks";
    private static final String SYMPHONY_REPORTER_PROXIES = "symphony.reporter.proxies";
    private static final String SYMPHONY_REPORTER_POD_URL = "symphony.reporter.pod.url";
    private static final String SYMPHONY_REPORTER_AGENT_URL = "symphony.reporter.agent.url";
    private static final String SYMPHONY_REPORTER_KEYAUTH_URL = "symphony.reporter.keyauth.url";
    private static final String SYMPHONY_REPORTER_SESSIONAUTH_URL = "symphony.reporter.sessionauth.url";
    private static final String SYMPHONY_REPORTER_RELAY_URL = "symphony.reporter.relay.url";
    private static final String SYMPHONY_REPORTER_LOGIN_URL = "symphony.reporter.login.url";

    private final Logger LOG = LoggerFactory.getLogger(SymphonyBuildReporter.class);

    public SymphonyBuildReporter() {
        super();
    }

    @Override
    public void afterSessionEnd(MavenSession session) throws MavenExecutionException {
        try {
            MavenExecutionResult done = session.getResult();
            Notifier n = getSymphonyNotifier(done);
            boolean sendOnFail = n == null ? true : n.isSendOnFailure();
            boolean passed = passed(done);
            boolean sendOnSuccess = n == null ? true : n.isSendOnSuccess();

            Map<String, Object> data = new HashMap<String, Object>();
            List<Map<String, Object>> projects = done.getTopologicallySortedProjects().stream()
                    .map(p -> getProjectDetails(p, done))
                    .collect(Collectors.toList());
            data.put("projects", projects);
            data.put("exceptions", formatExceptions(done));
            data.put("developers", done.getProject().getDevelopers());
            data.put("title", done.getProject().getName());
            data.put("date", new Date());
            data.put("passed", passed);
            data.put("url", getProjectUrl(done));
            data.put("recipients", getRecipients(done, n));
            data.put("hashtags", getHashTags(done, n));
            data.put("version", "1.0");
            data.put("type", "com.github.deutschebank.symphony.maven-event");

            Map<String, Object> out = Collections.singletonMap("event", data);

            SymphonyMessageSender sender = prepareApi(done.getProject());

            if (passed) {
                if (sendOnSuccess) {
                    sender.sendMessage(out);
                }
            } else {
                if (sendOnFail) {
                    sender.sendMessage(out);
                }
            }
        } catch (Exception e) {
            LOG.error("Failed to report build result: ", e);
        }

    }

    private String getProjectUrl(MavenExecutionResult done) {
        CiManagement ciManagement = done.getProject().getCiManagement();
        if (ciManagement != null) {
            if (ciManagement.getUrl() != null) {
                return ciManagement.getUrl();
            }
        }

        return done.getProject().getUrl();
    }

    private Notifier getSymphonyNotifier(MavenExecutionResult done) {
        CiManagement ciManagement = done.getProject().getCiManagement();
        if (ciManagement != null) {
            return ciManagement.getNotifiers().stream()
                    .filter(n -> n.getType().toLowerCase().contains("symphony"))
                    .findFirst().orElse(null);
        } else {
            return null;
        }
    }

    private List<String> getRecipients(MavenExecutionResult done, Notifier n) {
        String property = getProjectProperty(done.getProject(), SYMPHONY_REPORTER_RECIPIENTS);

        if (n != null) {
            if (property == null) {
                property = n.getConfiguration().getProperty("recipients");
            }
        }

        if (StringUtils.isBlank(property)) {
            return done.getProject().getDevelopers().stream()
                    .map(d -> d.getEmail()).collect(Collectors.toList());
        }

        if (StringUtils.isBlank(property)) {
            LOG.error("No recipients found for symphony notifier, consider setting " + SYMPHONY_REPORTER_RECIPIENTS + " property");
            property = "";
        }

        return Arrays.stream(property.split(","))
                .map(s -> s.trim()).collect(Collectors.toList());
    }

    private List<String> getHashTags(MavenExecutionResult done, Notifier n) {
        String property = getProjectProperty(done.getProject(), SYMPHONY_REPORTER_HASHTAGS);

        if (n != null) {
            if (property == null) {
                property = n.getConfiguration().getProperty("hashtags");
            }
        }

        if (property == null) {
            property = "";
        }

        return Arrays.stream(property.split(","))
                .map(s -> s.trim())
                .filter(s -> !StringUtils.isBlank(s))
                .collect(Collectors.toList());
    }


  

    private SymphonyMessageSender prepareApi(MavenProject project) throws IOException {
        String identityJson = getProjectPropertyStrict(project, SYMPHONY_REPORTER_IDENTITY);
        SymphonyIdentity identity = new ObjectMapper().readValue(identityJson, SymphonyIdentity.class);

        String trustStoreJks = getProjectProperty(project, SYMPHONY_REPORTER_TRUST_STORE_JKS);

        String proxiesRaw = getProjectProperty(project, SYMPHONY_REPORTER_PROXIES);
        List<String> proxies;
        
        if (StringUtils.isEmpty(proxiesRaw)) {
        	proxies = Collections.singletonList(null);
        } else {
        	proxies = Arrays.stream(proxiesRaw.split(","))
            .filter(s -> !s.isEmpty())
            .map(x -> NONE_PROXY.equals(x) ? null : x)
            .collect(Collectors.toList());
        }

        String podUrl = getProjectPropertyStrict(project, SYMPHONY_REPORTER_POD_URL);
        String agentUrl = getProjectPropertyStrict(project, SYMPHONY_REPORTER_AGENT_URL);
        String sessionauthUrl = getProjectProperty(project, SYMPHONY_REPORTER_SESSIONAUTH_URL);
        String keyauthUrl = getProjectProperty(project, SYMPHONY_REPORTER_KEYAUTH_URL);
        String relayUrl = getProjectProperty(project, SYMPHONY_REPORTER_RELAY_URL);
        String loginUrl = getProjectProperty(project, SYMPHONY_REPORTER_LOGIN_URL);

        ProxyingWrapper pod = new ProxyingWrapper(trustStoreJks, proxies, podUrl, identity, LOG);
        ProxyingWrapper agent = new ProxyingWrapper(trustStoreJks, proxies, agentUrl, identity, LOG);
        ProxyingWrapper session = sessionauthUrl == null ? null : new ProxyingWrapper(trustStoreJks, proxies, sessionauthUrl, identity, LOG);
        ProxyingWrapper key = keyauthUrl == null ? null : new ProxyingWrapper(trustStoreJks, proxies, keyauthUrl, identity, LOG);
        ProxyingWrapper relay = relayUrl == null ? null : new ProxyingWrapper(trustStoreJks, proxies, relayUrl, identity, LOG);
        ProxyingWrapper login = loginUrl == null ? null : new ProxyingWrapper(trustStoreJks, proxies, loginUrl, identity, LOG);
        return new SymphonyMessageSender(pod,agent, session, key, relay, login, identity);
    }


    private boolean passed(MavenExecutionResult done) {
        return done.getTopologicallySortedProjects().stream()
                .filter(p -> !(done.getBuildSummary(p) instanceof BuildSuccess))
                .count() == 0l;
    }

    private Map<String, Object> getProjectDetails(MavenProject mp, MavenExecutionResult done) {
        Map<String, Object> out = new HashMap<>();
        out.put("name", getNameOrArtifact(mp));
        out.put("status", textResult(done, mp));
        out.put("time", getTime(done, mp));
        return out;

    }

    private long getTime(MavenExecutionResult done, MavenProject mp) {
        BuildSummary bs = done.getBuildSummary(mp);
        long out = bs == null ? 0 : bs.getTime();
        return out;
    }

    private List<String> formatExceptions(MavenExecutionResult done) {
        return done.getExceptions().stream().map(e -> {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            return sw.toString();

        }).collect(Collectors.toList());
    }


    private String textResult(MavenExecutionResult done, MavenProject p) {
        BuildSummary out = done.getBuildSummary(p);
        if (out != null) {
            String n = out.getClass().getName();
            return n.substring(n.lastIndexOf(".") + 1);
        } else {
            return "Skipped";
        }
    }


    private String getNameOrArtifact(MavenProject p) {
        String name = p.getName();
        String art = p.getArtifactId();
        return name == null ? art : name;
    }

    private String getProjectProperty(MavenProject project, String name) {
        String value = (String) project.getProperties().get(name);
        return value;
    }

    private String getProjectPropertyStrict(MavenProject project, String name) {
        String value = getProjectProperty(project, name);
        if (StringUtils.isBlank(value)) {
            throw new IllegalStateException(name + " is missing");
        }
        return value;
    }
}