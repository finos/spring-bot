package org.finos.symphony.toolkit.quickfix;

import java.io.IOException;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import quickfix.DataDictionary;
import quickfix.Field;
import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.FieldType;
import quickfix.Group;
import quickfix.Message;
import quickfix.Message.Header;
import quickfix.Session;
import quickfix.field.BeginString;

public class QuickfixjSerializer<X  extends FieldMap> extends JsonSerializer<X> {

	private Session s;

	public QuickfixjSerializer(Session s) {
		this.s = s;
	}

	@Override
	public void serialize(X value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeStartObject();

		if (value instanceof Message) {
			Message message = (Message) value;
			DataDictionary dd = identifyDictionary(value, s.getDataDictionary());
			
			doSection(gen, message.getHeader(), "Header", dd);
			doSection(gen, message, "Body", dd);
			doSection(gen, message.getTrailer(), "Trailer", dd);
		} else {
			doFieldMap(gen, s.getDataDictionary(), value);
		}

		gen.writeEndObject();
	}
	
	private void doSection(JsonGenerator gen, FieldMap fm, String sectionName, DataDictionary dd) throws IOException {
		gen.writeFieldName(sectionName);
		gen.writeStartObject();
		doFieldMap(gen, dd, fm);
		gen.writeEndObject();
	}

	private void doFieldMap(JsonGenerator gen, DataDictionary dd, FieldMap fm) throws IOException {
		try {
			for (Iterator<Field<?>> iterator = fm.iterator(); iterator.hasNext();) {
				Field<?> field = iterator.next();
				String name = dd.getFieldName(field.getTag());
				
				if (name == null) {
					name = "#"+field.getTag();
				}
				
				
				gen.writeFieldName(name);
				Object o = field.getObject();
				FieldType ft = dd.getFieldType(field.getTag());
				
				if (ft == FieldType.NUMINGROUP) {
					int count = Integer.parseInt((String) o);
					gen.writeStartArray();
					
					for (int i = 0; i < count; i++) {
						Group g = new Group(field.getTag(), -1);
						fm.getGroup(i+1, g);
						gen.writeObject(g);
					}
					
					gen.writeEndArray();
				} else {
					gen.writeObject(o);
				}
				
			}
		} catch (NumberFormatException e) {
			throw new IOException(e);
		} catch (FieldNotFound e) {
			throw new IOException(e);
		}
	}

	private DataDictionary identifyDictionary(FieldMap value, DataDictionary original) throws IOException {
		try {
			if (value instanceof Message) {
				Header h = ((Message) value).getHeader();
				BeginString bs = new BeginString();
				h.getField(bs);
				return s.getDataDictionaryProvider().getSessionDataDictionary(bs.getValue());
			} else {
				return original;
			}
		} catch (FieldNotFound e) {
			throw new IOException("Couldn't serialize fix message: ", e);
		}
	}

}
