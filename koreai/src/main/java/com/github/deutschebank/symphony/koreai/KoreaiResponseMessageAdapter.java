package com.github.deutschebank.symphony.koreai;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.FormButtonType;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import utils.FormBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author rodriva
 */
@Component
public class KoreaiResponseMessageAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(KoreaiResponseMessageAdapter.class);

    private final ObjectMapper objectMapper;

    public static final String BR = "<br />\n";

    public KoreaiResponseMessageAdapter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    public String parse(String input) {
        return input
                .replaceAll("\\\\n", "\n")
                .replaceAll("\n", BR)
                .replaceAll("(https?:\\/\\/[\\w.\\/\\+_\\=\\-\\?]*)", "<a href=\"$1\">$1</a>");

    }

    public String parse(InputStream inputStream) {
        KoreaResponse koreaResponse;
        try {
            String text = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
            LOG.debug("RAW KORE.AI RESPONSE: " + text);

            koreaResponse = objectMapper.readValue(text, KoreaResponse.class);

            if (koreaResponse.getText().contains("I am unable to find an answer")) return null;

            if (koreaResponse.getText().startsWith("{\"text\"")) {
                koreaResponse = objectMapper.readValue(koreaResponse.getText(), KoreaResponse.class);
            }
            LOG.debug(koreaResponse.getText());
            String parsed = parse(koreaResponse.getText());

            if (koreaResponse.isTemplate()) {
                parsed = renderButtons(parsed);
            }

            return parsed;
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    private String renderButtons(String template) {
        String[] multiline = template.split(BR);

        FormBuilder formBuilder = FormBuilder.builder("koreai-passthrough")
                .addDiv(multiline[0]);
        for (int i = 1; i < multiline.length; i++) {
            formBuilder.addRadioButton("selection", multiline[i], multiline[i], false);
        }
        formBuilder.addButton("submit", "Submit", FormButtonType.ACTION);
        return formBuilder.formatElement();
    }
}
