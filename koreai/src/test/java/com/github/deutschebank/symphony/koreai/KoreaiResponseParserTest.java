package com.github.deutschebank.symphony.koreai;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

/**
 * @author rodriva
 */
@RunWith(Parameterized.class)
public class KoreaiResponseParserTest {

    private final KoreaiResponseMessageAdapter parser;
    private String fInput;
    private String fExpected;

    public KoreaiResponseParserTest(String input, String expected) {
        this.fInput = input;
        this.fExpected = expected;
        this.parser = new KoreaiResponseMessageAdapter(new ObjectMapper());
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {file("ans0.json"), null},
                {file("ans1.json"), file("res1.txt")},
                {file("ans2.json"), file("res2.txt")},
                {file("ans3.json"), file("res3.txt")}
        });
    }

    private static String file(String filename) {
        return new Scanner(Thread.currentThread().getContextClassLoader().getResourceAsStream(filename))
                .useDelimiter("\\Z").next().replaceAll("\r\n", "\n");
    }

    @Test
    public void parse() {
        String actual = this.parser.parse(asInputStream(fInput));
        assertEquals(fExpected, actual);
    }

    private InputStream asInputStream(String fInput) {
        return new ByteArrayInputStream(fInput.getBytes());
    }
}
