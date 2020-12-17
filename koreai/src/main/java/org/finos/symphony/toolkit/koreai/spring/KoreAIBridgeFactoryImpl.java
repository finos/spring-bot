package org.finos.symphony.toolkit.koreai.spring;

import java.io.IOException;

import org.finos.symphony.toolkit.koreai.output.KoreAIResponseHandler;
import org.finos.symphony.toolkit.koreai.output.KoreAIResponseHandlerImpl;
import org.finos.symphony.toolkit.koreai.request.KoreAIRequester;
import org.finos.symphony.toolkit.koreai.request.KoreAIRequesterImpl;
import org.finos.symphony.toolkit.koreai.response.KoreAIResponseBuilder;
import org.finos.symphony.toolkit.koreai.response.KoreAIResponseBuilderImpl;
import org.finos.symphony.toolkit.spring.api.ApiInstance;
import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.SharedStreamProperties;
import org.finos.symphony.toolkit.stream.StreamEventConsumer;
import org.finos.symphony.toolkit.stream.cluster.transport.Multicaster;
import org.finos.symphony.toolkit.stream.filter.SymphonyLeaderEventFilter;
import org.finos.symphony.toolkit.stream.handler.ExceptionConsumer;
import org.finos.symphony.toolkit.stream.handler.SymphonyStreamHandler;
import org.finos.symphony.toolkit.stream.log.LogMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.symphony.api.agent.DatafeedApi;
import com.symphony.api.agent.MessagesApi;
import com.symphony.api.pod.UsersApi;

public class KoreAIBridgeFactoryImpl implements KoreAIBridgeFactory {
	
	private static final Logger LOG = LoggerFactory.getLogger(KoreAIBridgeFactoryImpl.class);
	
	private ResourceLoader rl;
	private ObjectMapper om;
	private KoreAIProperties koreAIProperties;
	private SharedStreamProperties streamProperties;
	private ExceptionConsumer exceptionConsumer;
	private Multicaster mc;
	private Participant self;
	private LogMessageHandler log;

	public KoreAIBridgeFactoryImpl(ResourceLoader rl, ObjectMapper om, KoreAIProperties koreAIProperties,
			SharedStreamProperties streamProperties, ExceptionConsumer exceptionConsumer, Multicaster mc,
			Participant self, LogMessageHandler log) {
		super();
		this.rl = rl;
		this.om = om;
		this.koreAIProperties = koreAIProperties;
		this.streamProperties = streamProperties;
		this.exceptionConsumer = exceptionConsumer;
		this.mc = mc;
		this.self = self;
		this.log = log;
	}


	@Override
	public SymphonyStreamHandler buildBridge(KoreAIInstanceProperties props, ApiInstance apiInstance) {
		try {
			// build KoreAI pipeline
			KoreAIResponseBuilder koreAIResponseBuilder = koreAIResponseBuilder();
			KoreAIResponseHandler koreAIResponseHandler = responseMessageAdapter(apiInstance, props);
			KoreAIRequester requester = koreAIRequester(props, koreAIResponseHandler, koreAIResponseBuilder);
			StreamEventConsumer koreAISymphonyConsumer = koreAIEventHandler(requester, apiInstance, props);
			
			// wire this up to a shared stream
			SymphonyLeaderEventFilter eventFilter = symphonyLeaderEventFilter(mc, self, log, koreAISymphonyConsumer);
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
	
	public StreamEventConsumer koreAIEventHandler(KoreAIRequester requester, ApiInstance api, KoreAIInstanceProperties props) {
		return new KoreAIEventHandler(api.getIdentity(), api.getPodApi(UsersApi.class), requester, om, props.isOnlyAddressed());
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
