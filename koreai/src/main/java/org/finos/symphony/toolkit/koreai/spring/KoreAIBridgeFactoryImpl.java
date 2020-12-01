package org.finos.symphony.toolkit.koreai.spring;

import java.io.IOException;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.finos.symphony.toolkit.koreai.output.KoreAIResponseHandler;
import org.finos.symphony.toolkit.koreai.output.KoreAIResponseHandlerImpl;
import org.finos.symphony.toolkit.koreai.request.KoreAIRequester;
import org.finos.symphony.toolkit.koreai.request.KoreAIRequesterImpl;
import org.finos.symphony.toolkit.koreai.response.KoreAIResponseBuilder;
import org.finos.symphony.toolkit.koreai.response.KoreAIResponseBuilderImpl;
import org.finos.symphony.toolkit.spring.api.ApiInstance;
import org.finos.symphony.toolkit.spring.api.ApiInstanceFactory;
import org.finos.symphony.toolkit.spring.api.properties.IdentityProperties;
import org.finos.symphony.toolkit.spring.api.properties.PodProperties;
import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.StreamEventConsumer;
import org.finos.symphony.toolkit.stream.cluster.transport.Multicaster;
import org.finos.symphony.toolkit.stream.filter.SymphonyLeaderEventFilter;
import org.finos.symphony.toolkit.stream.handler.SymphonyStreamHandler;
import org.finos.symphony.toolkit.stream.log.LogMessageHandler;
import org.finos.symphony.toolkit.stream.log.SharedLog;
import org.finos.symphony.toolkit.stream.log.SymphonyRoomSharedLog;
import org.finos.symphony.toolkit.stream.spring.SharedStreamConfig.ExceptionConsumer;
import org.finos.symphony.toolkit.stream.spring.SymphonyStreamProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.symphony.api.agent.DatafeedApi;
import com.symphony.api.agent.MessagesApi;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.pod.UsersApi;

public class KoreAIBridgeFactoryImpl implements KoreAIBridgeFactory {
	
	private static final Logger LOG = LoggerFactory.getLogger(KoreAIBridgeFactoryImpl.class);
	
	private ResourceLoader rl;
	private ObjectMapper om;
	private TrustManagerFactory trustManagers;
	private PodProperties pp;
	private KoreAIProperties koreAIProperties;
	private SymphonyStreamProperties streamProperties;
	private ApiInstanceFactory symphonyFactory;
	private ExceptionConsumer exceptionConsumer;
	private Multicaster mc;
	private Participant self;

	public KoreAIBridgeFactoryImpl(ResourceLoader rl, ObjectMapper om, TrustManagerFactory trustManagers, PodProperties pp,
			KoreAIProperties koreAIProperties, SymphonyStreamProperties streamProperties,
			ApiInstanceFactory symphonyFactory, ExceptionConsumer exceptionConsumer, Multicaster mc, Participant self) {
		super();
		this.rl = rl;
		this.om = om;
		this.trustManagers = trustManagers;
		this.pp = pp;
		this.koreAIProperties = koreAIProperties;
		this.streamProperties = streamProperties;
		this.symphonyFactory = symphonyFactory;
		this.exceptionConsumer = exceptionConsumer;
		this.mc = mc;
		this.self = self;
	}

	@Override
	public SymphonyStreamHandler buildBridge(KoreAIInstanceProperties props) {
		try {
			// build symphony api and bot indentity.
			SymphonyIdentity symphonyBotIdentity = IdentityProperties.instantiateIdentityFromDetails(rl, props.getSymphonyBot(), om);
			TrustManager[] tms = trustManagers == null ? null: trustManagers.getTrustManagers();
			ApiInstance apiInstance = symphonyFactory.createApiInstance(symphonyBotIdentity, pp, tms);
			
			// build KoreAI pipeline
			KoreAIResponseBuilder koreAIResponseBuilder = koreAIResponseBuilder();
			KoreAIResponseHandler koreAIResponseHandler = responseMessageAdapter(apiInstance, props);
			KoreAIRequester requester = koreAIRequester(props, koreAIResponseHandler, koreAIResponseBuilder);
			StreamEventConsumer koreAISymphonyConsumer = koreAIEventHandler(requester, apiInstance, symphonyBotIdentity, props);
			
			// wire this up to a shared stream
			SharedLog sharedLog = symphonySharedLog(apiInstance);
			SymphonyLeaderEventFilter eventFilter = symphonyLeaderEventFilter(mc, self, (LogMessageHandler) sharedLog, koreAISymphonyConsumer);
			SymphonyStreamHandler out = symphonyStreamHandler(exceptionConsumer, eventFilter, apiInstance);
			return out;
		} catch (Exception e) {
			LOG.error("Couldn't construct Kore/Symphony bridge bean", e);
			return null;
		}
		
	}
	
	
	public KoreAIResponseHandler responseMessageAdapter(ApiInstance api, KoreAIInstanceProperties properties) throws IOException {
		return new KoreAIResponseHandlerImpl(api.getAgentApi(MessagesApi.class), rl, 
				properties.isSkipEmptyResponses(), 
				om,
				koreAIProperties.getTemplatePrefix());	
	}
	
	public KoreAIRequester koreAIRequester(
			KoreAIInstanceProperties properties,
			KoreAIResponseHandler responseHandler, 
			KoreAIResponseBuilder responseBuilder) throws Exception {
		return new KoreAIRequesterImpl(responseHandler,
				responseBuilder,
				properties.getUrl(), 
				JsonNodeFactory.instance, 
				properties.getJwt());
	}
	
	public KoreAIResponseBuilder koreAIResponseBuilder() {
		return new KoreAIResponseBuilderImpl(new ObjectMapper(), JsonNodeFactory.instance);
	}
	
	public StreamEventConsumer koreAIEventHandler(KoreAIRequester requester, ApiInstance api, SymphonyIdentity botIdentity, KoreAIInstanceProperties props) {
		return new KoreAIEventHandler(botIdentity, api.getPodApi(UsersApi.class), requester, om, props.isOnlyAddressed());
	}
	
	public SharedLog symphonySharedLog(ApiInstance symphonyInstance) {
		if (StringUtils.isEmpty(streamProperties.getCoordinationStreamId())) {
			throw new IllegalArgumentException("Shared Log needs a stream ID to write to.  PLease set symphony.stream.coordination-stream-id");
		}
		
		return new SymphonyRoomSharedLog(
				streamProperties.getCoordinationStreamId(), 
				symphonyInstance.getAgentApi(MessagesApi.class), 
				streamProperties.getEnvironmentIdentifier(),
				streamProperties.getParticipantWriteIntervalMillis());
	}
	
	public SymphonyStreamHandler symphonyStreamHandler(ExceptionConsumer exceptionHandler, 
			SymphonyLeaderEventFilter eventFilter,
			ApiInstance symphonyInstance) {
		return new SymphonyStreamHandler(symphonyInstance.getAgentApi(DatafeedApi.class), eventFilter, exceptionHandler, streamProperties.isStartImmediately());
	}

	public SymphonyLeaderEventFilter symphonyLeaderEventFilter(Multicaster mc, Participant self, LogMessageHandler lmh, StreamEventConsumer userDefinedCallback) {
		return new SymphonyLeaderEventFilter(userDefinedCallback, 
			streamProperties.getCoordinationStreamId() == null, // if not defined, we are leader
			self, 
			lmh, lm -> mc.accept(lm.getParticipant()));
	}

}
