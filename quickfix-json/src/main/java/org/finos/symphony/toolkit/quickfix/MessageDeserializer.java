package org.finos.symphony.toolkit.quickfix;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

import quickfix.FieldMap;
import quickfix.Group;
import quickfix.Message;
import quickfix.Message.Header;
import quickfix.Message.Trailer;
import quickfix.Session;
import quickfix.field.BeginString;
import quickfix.field.MsgType;

/**
 * Handles constructing messages, which are a header, body and trailer.

 * @author Rob Moffat
 *
 */
public class MessageDeserializer extends AbstractQuickfixjDeserializer<Message> {
	
	public MessageDeserializer(Session s) {
		super(s, Message.class);
	}

	@Override
	public Message deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		try {
			TempFieldMap tfm = new TempFieldMap();
			fillFields(tfm, p, ctxt);
			
			// build the return object.
			MsgType type = (MsgType) tfm.getTempHeader().getField(new MsgType());
			BeginString bs = (BeginString) tfm.getTempHeader().getField(new BeginString());
			String version = bs.getValue();
			Class<Message> messageClass = Dictionaries.getClassForMessageType(version, type.getValue(), s.getSessionID().getBeginString());
			Message out = messageClass.newInstance(); 
			
			// populate it
			Header header = out.getHeader();
			header.setFields(tfm.getTempHeader());
			ReflectionHelpers.setFieldOrder(header, tfm.getTempHeader().getFieldOrder());
			
			out.setFields(tfm);
			tfm.getTempGroups().forEach(g -> {
				Group ng = ((TempFieldMap) g).toGroup(out.getClass().getName());
				out.addGroupRef(ng);
			});
			ReflectionHelpers.setFieldOrder(out, tfm.getFieldOrder());
			
			Trailer trailer = out.getTrailer();
			trailer.setFields(tfm.getTempTrailer());
			ReflectionHelpers.setFieldOrder(trailer, tfm.getTempTrailer().getFieldOrder());
			
			
			return out;
		} catch (Exception e) {
			throw new IOException("Coudldn't build message: ", e);
		}
	}

	@Override
	protected void createField(String name, JsonParser p, FieldMap fm, DeserializationContext dctx) throws IOException {
		
		if ("Header".equals(name)) {
			TempFieldMap temp = p.readValueAs(TempFieldMap.class);
			((TempFieldMap)fm).setTempHeader(temp);
		} else if ("Body".equals(name)) {
			fillFields(fm, p, dctx);
		} else if ("Trailer".equals(name)) {
			TempFieldMap temp = p.readValueAs(TempFieldMap.class);
			((TempFieldMap)fm).setTempTrailer(temp);
		} else {	
			super.createField(name, p, fm, dctx);
		}
	}

}
