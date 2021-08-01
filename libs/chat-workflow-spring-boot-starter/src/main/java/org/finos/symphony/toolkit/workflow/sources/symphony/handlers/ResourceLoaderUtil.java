package org.finos.symphony.toolkit.workflow.sources.symphony.handlers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

/**
 * @author rupnsur
 *
 */
@Component
public class ResourceLoaderUtil {

@Autowired
ResourceLoader resourceLoader;

public String readTemplateToString(String templateName) {
   Resource resource = resourceLoader.getResource(templateName);
   if (!resource.exists()) {
throw new UnsupportedOperationException("Template not available: " + templateName);
}
   return asString(resource);
}

private String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), Charset.defaultCharset())) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
        throw new UnsupportedOperationException("Template not available:", e);
        }
    }
}