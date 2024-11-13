package com.visualpathit.account.controllerTest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SampleTest {

    @Test
    public void shouldReturnCorrectStringLength() {
        String sampleText = "Hello";
        int expectedLength = 5;

        assertEquals("String length should be 5", expectedLength, sampleText.length());
    }
}
