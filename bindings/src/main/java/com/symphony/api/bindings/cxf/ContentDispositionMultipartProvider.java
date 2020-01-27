package com.symphony.api.bindings.cxf;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.provider.MultipartProvider;

/**
 * This solves the following issue:
 * <ul>
 * <li>CXF doesn't send through the Content-Disposition header in the regular
 * MultipartProvider. This is a bug in apache cxf.
 * </ul>
 * 
 * @author moffrob
 *
 */
public class ContentDispositionMultipartProvider extends MultipartProvider {
	
	@SuppressWarnings("unchecked")
	@Override
	public void writeTo(Object obj, Class<?> type, Type genericType, Annotation[] anns, MediaType mt,
			MultivaluedMap<String, Object> headers, OutputStream os) throws IOException, WebApplicationException {

		if (obj instanceof List) {
			obj = ((List<Object>) obj).stream().map(o -> setContentDisposition(o)).collect(Collectors.toList());
		}

		super.writeTo(obj, type, genericType, anns, mt, headers, os);
	}

	private Object setContentDisposition(Object obj) {
		if (obj instanceof Attachment) {
			Attachment a = (Attachment) obj;
			MultivaluedMap<String, String> attachmentHeaders = a.getHeaders();
			while (obj instanceof Attachment) {
				attachmentHeaders.putAll(((Attachment) obj).getHeaders());
				obj = ((Attachment) obj).getObject();
			}

			if (!attachmentHeaders.containsKey("Content-Disposition")) {
				String id = attachmentHeaders.getFirst("Content-ID");
				String contentType = attachmentHeaders.getFirst("Content-Type");
				String name = getName(obj, contentType);
				attachmentHeaders.putSingle("Content-Disposition",
						"form-data; name=\"" + id + "\"; filename=\"" + name + "\"");
				return new Attachment(attachmentHeaders, obj);
			}
		}

		return obj;
	}

	private String getName(Object obj, String contentType) {
		if (obj instanceof File) {
			return ((File) obj).getName();
		} else {
			return contentType.replace("/", "."); 
		}
	}

}
