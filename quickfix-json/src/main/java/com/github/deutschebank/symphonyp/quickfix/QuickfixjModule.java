package com.github.deutschebank.symphonyp.quickfix;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.ser.Serializers;

import quickfix.ApplicationAdapter;
import quickfix.ConfigError;
import quickfix.DefaultSessionFactory;
import quickfix.FieldMap;
import quickfix.Log;
import quickfix.LogFactory;
import quickfix.NoopStoreFactory;
import quickfix.RuntimeError;
import quickfix.Session;
import quickfix.SessionFactory;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.field.BeginString;
import quickfix.field.SenderCompID;
import quickfix.field.TargetCompID;

public class QuickfixjModule extends Module {

	private static final String NAME = "QuickFix Module";
	private static final Version VERSION = new Version(1, 0, 0, "", "com.db.symphony", "jackson-quickfix-mapper");
	private Session s;
	
	public QuickfixjModule(Session s) {
		this.s = s;
	}
	
	public QuickfixjModule() {
		this(createSimpleSession("FIX.5.0SP2"));
	}
	
	@Override
	public String getModuleName() {
		return NAME;
	}

	@Override
	public Version version() {
		return VERSION;
	}

	@Override
	public void setupModule(SetupContext context) {
		
		context.addDeserializers(new Deserializers.Base() {

			@Override
			public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config,
					BeanDescription beanDesc) throws JsonMappingException {
				if (TempFieldMap.class.isAssignableFrom(type.getRawClass())) {
					return new TempFieldMapDeserializer(s);
				} else if (FieldMap.class.isAssignableFrom(type.getRawClass())) {
					return new MessageDeserializer(s);
				} else {
					return null;
				}
			}
		});
		
		context.addSerializers(new Serializers.Base() {

			@Override
			public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type,
					BeanDescription beanDesc) {
				if (FieldMap.class.isAssignableFrom(type.getRawClass())) {
					return new QuickfixjSerializer<>(s);
				} else {
					return null;
				}
			}
		});
	}
	
	private static Session createSimpleSession(String fixVersionString) {
		try {
			BeginString bs = new BeginString(fixVersionString);
			SenderCompID sc = new SenderCompID("---");
			TargetCompID tc = new TargetCompID("---");
			SessionSettings ss = new SessionSettings();
			ss.setString(SessionFactory.SETTING_CONNECTION_TYPE, SessionFactory.ACCEPTOR_CONNECTION_TYPE);
			ss.setString(Session.SETTING_START_TIME, new SimpleDateFormat("hh:mm:ss").format(new Date()));
			ss.setString(Session.SETTING_END_TIME, new SimpleDateFormat("hh:mm:ss").format(new Date()));
			
			return new DefaultSessionFactory(new ApplicationAdapter(), new NoopStoreFactory(), new LogFactory() {

				@Override
				public Log create(SessionID sessionID) {
					return null;
				}
			}).create(new SessionID(bs, sc, tc), ss);
		} catch (ConfigError e) {
			throw new RuntimeError("Config Error creating MessageSerializer: ", e);
		}
	}
}
