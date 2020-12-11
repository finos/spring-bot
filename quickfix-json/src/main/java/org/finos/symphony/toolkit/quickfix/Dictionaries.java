package org.finos.symphony.toolkit.quickfix;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import quickfix.Message;

public class Dictionaries {
	
	private static Map<String, Class<Message>> NO_MAP = new HashMap<>();
	
	private static Map<String, Map<String, Class<Message>>> dictionaries = new HashMap<>();
	
	private static Map<String, Integer> fields = new HashMap<>();
	
	public static Map<String, Class<Message>> getDictionary(String fixVersion, String sessionVersion) throws IOException {
		Map<String, Class<Message>> out = tryAndGet(fixVersion);
		
		if (out == NO_MAP) {
			out = tryAndGet(sessionVersion);
		}
		
		return out;
	}
	
	private static Map<String, Class<Message>> tryAndGet(String v) {
		try {
			return create(v, getInputStreamForVersion(v), getPackageForVersion(v));
		} catch (Exception e) {
			return NO_MAP;
		}
	}

	private static InputStream getInputStreamForVersion(String fixVersion) {
		String resource = "/"+fixVersion.replace(".", "").toUpperCase()+".xml";
		return Dictionaries.class.getResourceAsStream(resource);
	}

	private static String getPackageForVersion(String fixVersion) {
		return "quickfix."+ fixVersion.replace(".", "").toLowerCase();
	}

	public static Map<String, Class<Message>> create(String fixVersion, InputStream fixDefinition, String p) throws IOException {
		try {
			Map<String, Class<Message>> typeToClasses = new HashMap<>();
						
			SAXParserFactory parserFactor = SAXParserFactory.newInstance();
			SAXParser parser = parserFactor.newSAXParser();
 
			parser.parse(fixDefinition,  new DefaultHandler() {
				
				@Override
				@SuppressWarnings("unchecked")
				public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
					String className = null;
					if ("message".equals(qName)) {
						try {
							String type = atts.getValue("msgtype");
							String name = atts.getValue("name");
							className = p+"."+name;
							Class<Message> c = (Class<Message>) Class.forName(className);
							typeToClasses.put(type, c);
						} catch (Exception e) {
							throw new SAXException("Couldn't find message class: "+className, e);
						}
					} else if ("field".equals(qName)) {
						String number = atts.getValue("number");
						if (number!=null) {
							int no = Integer.parseInt(number);				
							String name = atts.getValue("name");
							fields.put(name, no);
						}
					}
				}
			});
			
			dictionaries.put(fixVersion, typeToClasses);
			return typeToClasses;
		} catch (Exception e) {
			dictionaries.put(fixVersion, NO_MAP);
			throw new IOException("Couldn't build message dictionary "+p, e);
		}

	}


	public static Class<Message> getClassForMessageType(String version, String type, String sessionVersion) throws IOException {
		Map<String, Class<Message>> dic = getDictionary(version, sessionVersion);
		return dic.get(type);
	}
	
	public static Integer getFieldNumber(String name) {
		return fields.get(name);
	}
}
