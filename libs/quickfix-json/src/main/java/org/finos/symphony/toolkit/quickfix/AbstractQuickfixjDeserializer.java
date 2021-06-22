package org.finos.symphony.toolkit.quickfix;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import quickfix.DataDictionary;
import quickfix.Field;
import quickfix.FieldMap;
import quickfix.FieldType;
import quickfix.Session;
import quickfix.StringField;

public abstract class AbstractQuickfixjDeserializer<X> extends JsonDeserializer<X> {

	protected Session s;
	protected Class<X> c;

	public AbstractQuickfixjDeserializer(Session s, Class<X> class1) {
		this.s = s;
		this.c = class1;
	}

	protected void fillFields(FieldMap fm, JsonParser p, DeserializationContext ctxt) throws IOException {
		JsonToken jt;

		while ((jt = p.nextToken()) != JsonToken.END_OBJECT) {
			if (jt == JsonToken.FIELD_NAME) {
				String name = p.currentName();
				createField(name, p, fm, ctxt);
			}
		}
	}

	protected Field<?> constructFieldAndParse(String name, JsonParser p) throws IOException {
		try {
			Integer f = Dictionaries.getFieldNumber(name);
			
			if (f == null) {
				f = Integer.parseInt(name.substring(1));
			}
			
			StringField out = new StringField(f, p.readValueAs(String.class));
			return out;
		} catch (Exception e) {
			throw new IOException("Couldn't handle field: " + name, e);
		}
	}

	protected Field<?> constructFieldWithValue(String name, Object v) throws IOException {
		try {
			Constructor<Field<?>> con = findAppropriateFieldConstructor(name);
			Field<?> out = con.newInstance(v);
			return out;
		} catch (Exception e) {
			throw new IOException("Couldn't handle field: " + name, e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Constructor<Field<?>> findAppropriateFieldConstructor(String name)
			throws ClassNotFoundException, IOException {
		String className = "quickfix.field." + name;
		Class<Field> cl = (Class<Field>) Class.forName(className);
		Constructor<Field<?>> con = (Constructor<Field<?>>) Arrays.stream(cl.getConstructors())
				.filter(c -> c.getParameterTypes().length == 1).findFirst()
				.orElseThrow(() -> new IOException("Couldn't find appropriate constructor"));
		return con;
	}

	protected DataDictionary setDictionary(String beginString, DeserializationContext dctx) throws IOException {
		Dictionaries.getDictionary(beginString, s.getSessionID().getBeginString());
		DataDictionary dd = s.getDataDictionaryProvider().getSessionDataDictionary(beginString);
		dctx.setAttribute(DataDictionary.class, dd);
		return dd;
	}

	protected DataDictionary getDictionary(DeserializationContext dctx) {
		DataDictionary dd = (DataDictionary) dctx.getAttribute(DataDictionary.class);
		return dd == null ? s.getDataDictionary() : dd;
	}

	protected void createField(String name, JsonParser p, FieldMap fm, DeserializationContext dctx) throws IOException {
		if ("BeginString".equals(name)) {
			// special case for dictionary handling.
			String fixVersion = p.nextTextValue();
			setDictionary(fixVersion, dctx);
			Field<?> out = constructFieldWithValue(name, fixVersion);
			fm.setField(getFieldTag(name, dctx), out);
		} else if (getFieldType(name, dctx) == FieldType.NUMINGROUP) {
			p.nextToken();
			int tag = getFieldTag(name, dctx);
			List<TempFieldMap> tempMaps = p.readValueAs(new TypeReference<List<TempFieldMap>>() {});
			tempMaps.stream().forEach(tm -> {
				tm.setGroupName(name);
				((TempFieldMap)fm).addTempGroup(tm);
			});
			Field<?> out = constructFieldWithValue(name, tempMaps.size());
			fm.setField(tag, out);
				
		} else {
			p.nextToken();
			Field<?> out = constructFieldAndParse(name, p);
			fm.setField(getFieldTag(name, dctx), out);
		}
	}

	protected int getFieldTag(String name, DeserializationContext dctx) {
		DataDictionary dd = getDictionary(dctx);
		int tag = dd.getFieldTag(name);
		return tag;
	}
	
	protected FieldType getFieldType(String name, DeserializationContext dctx) {
		DataDictionary dd = getDictionary(dctx);
		int tag = getFieldTag(name, dctx);
		FieldType ft = dd.getFieldType(tag);
		return ft;
	}
	
}
