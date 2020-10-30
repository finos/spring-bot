package org.finos.symphony.toolkit.koreai;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.finos.symphony.toolkit.koreai.response.KoreAIResponse;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


public class KoreaResponseTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void isTemplate() throws IOException {
        String input = "{\"text\":\"bla\",\"isTemplate\":true}";
        KoreAIResponse koreaResponse = objectMapper.readValue(input, KoreAIResponse.class);
        assertThat(koreaResponse.isTemplate()).isTrue();
    }

    @Test
    public void isNotTemplate() throws IOException {
        String input = "{\"text\":\"bla\",\"isTemplate\":false}";
        KoreAIResponse koreaResponse = objectMapper.readValue(input, KoreAIResponse.class);
        assertThat(koreaResponse.isTemplate()).isFalse();
    }

    @Test
    public void hasNotTemplate() throws IOException {
        String input = "{\"text\":\"bla\"}";
        KoreAIResponse koreaResponse = objectMapper.readValue(input, KoreAIResponse.class);
        assertThat(koreaResponse.isTemplate()).isFalse();
    }

    @Test
    public void error() throws IOException {
        String input = "{\"errors\":[{\"msg\":\"Something went wrong! Try again later!\",\"code\":400}]}";
        KoreAIResponse koreaResponse = objectMapper.readValue(input, KoreAIResponse.class);
        assertThat(koreaResponse.isTemplate()).isFalse();
        assertThat(koreaResponse.getText()).isNull();
        assertThat(koreaResponse.getErrors()).size().isEqualTo(1);
    }
}
