package com.github.deutschebank.symphony.koreai;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


public class KoreaResponseTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void isTemplate() throws IOException {
        String input = "{\"text\":\"bla\",\"isTemplate\":true}";
        KoreaResponse koreaResponse = objectMapper.readValue(input, KoreaResponse.class);
        assertThat(koreaResponse.isTemplate()).isTrue();
    }

    @Test
    public void isNotTemplate() throws IOException {
        String input = "{\"text\":\"bla\",\"isTemplate\":false}";
        KoreaResponse koreaResponse = objectMapper.readValue(input, KoreaResponse.class);
        assertThat(koreaResponse.isTemplate()).isFalse();
    }

    @Test
    public void hasNotTemplate() throws IOException {
        String input = "{\"text\":\"bla\"}";
        KoreaResponse koreaResponse = objectMapper.readValue(input, KoreaResponse.class);
        assertThat(koreaResponse.isTemplate()).isFalse();
    }

    @Test
    public void error() throws IOException {
        String input = "{\"errors\":[{\"msg\":\"Something went wrong! Try again later!\",\"code\":400}]}";
        KoreaResponse koreaResponse = objectMapper.readValue(input, KoreaResponse.class);
        assertThat(koreaResponse.isTemplate()).isFalse();
        assertThat(koreaResponse.getText()).isNull();
        assertThat(koreaResponse.getErrors()).size().isEqualTo(1);
    }
}
