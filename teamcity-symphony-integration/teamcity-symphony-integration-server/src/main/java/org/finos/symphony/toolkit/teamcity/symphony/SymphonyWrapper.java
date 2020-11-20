package org.finos.symphony.toolkit.teamcity.symphony;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;
import org.finos.symphony.toolkit.teamcity.teamcity.SymphonyNotificator;
import org.finos.symphony.toolkit.teamcity.teamcity.SymphonyPayload;
import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jetbrains.buildServer.Build;
import jetbrains.buildServer.serverSide.TeamCityProperties;
import jetbrains.buildServer.web.util.WebUtil;

public class SymphonyWrapper
{
    public static final GsonBuilder GSON_BUILDER = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();
    private static final Logger LOG = Logger.getLogger(SymphonyNotificator.class);
    protected String symphonyUrl;

    protected String username;

    protected String channel;

    protected String serverUrl;

    protected Boolean useAttachment;

    public SymphonyWrapper () {
        this.useAttachment  = TeamCityProperties.getBooleanOrTrue("teamcity.notification.symphony.useAttachment");
    }

    public SymphonyWrapper (Boolean useAttachment) {
        this.useAttachment = useAttachment;
    }

    public String send(String project, String build, String branch, String statusText, String statusColor, Build bt) throws IOException
    {
        String formattedPayload = getFormattedPayload(project, build, branch, statusText, statusColor, bt.getBuildTypeExternalId(), bt.getBuildId());
        LOG.debug(formattedPayload);

        URL url = new URL(this.getsymphonyUrl());
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();

        httpsURLConnection.setRequestMethod("POST");
        httpsURLConnection.setRequestProperty("User-Agent", "Enliven");
        httpsURLConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        httpsURLConnection.setDoOutput(true);

        DataOutputStream dataOutputStream = new DataOutputStream(
            httpsURLConnection.getOutputStream()
        );

        //dataOutputStream.writeBytes(formattedPayload);
        byte[] array = formattedPayload.getBytes("UTF-8");
        dataOutputStream.write(array, 0, array.length);
        dataOutputStream.flush();
        dataOutputStream.close();

        InputStream inputStream;
        String responseBody = "";

        try {
            inputStream = httpsURLConnection.getInputStream();
        }
        catch (IOException e) {
            responseBody = e.getMessage();
            inputStream = httpsURLConnection.getErrorStream();
            if (inputStream != null) {
                responseBody += ": ";
                responseBody = getResponseBody(inputStream, responseBody);
            }
            throw new IOException(responseBody);
        }

        return getResponseBody(inputStream, responseBody);
    }

    @NotNull
    public String getFormattedPayload(String project, String build, String branch, String statusText, String statusColor, String btId, long buildId) {
        Gson gson = GSON_BUILDER.create();

        SymphonyPayload symphonyPayload = new SymphonyPayload(project, build, branch, statusText, statusColor, btId, buildId, WebUtil.escapeUrlForQuotes(getServerUrl()));
        symphonyPayload.setChannel(getChannel());
        symphonyPayload.setUsername(getUsername());
        symphonyPayload.setUseAttachments(this.useAttachment);

        return gson.toJson(symphonyPayload);
    }

    private String getResponseBody(InputStream inputStream, String responseBody) throws IOException {
        String line;

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream)
        );

        while ((line = bufferedReader.readLine()) != null) {
            responseBody += line + "\n";
        }

        bufferedReader.close();
        return responseBody;
    }

    public void setsymphonyUrl(String symphonyUrl)
    {
        this.symphonyUrl = symphonyUrl;
    }

    public String getsymphonyUrl()
    {
        return this.symphonyUrl;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return this.username;
    }

    public void setChannel(String channel)
    {
        this.channel = channel;
    }

    public String getChannel()
    {
        return this.channel;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
}
