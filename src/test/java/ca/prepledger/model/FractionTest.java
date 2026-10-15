package ca.prepledger.model;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class FractionTest {

    @ParameterizedTest
    @ValueSource(strings = {
        // Your requested cases
        "1/0",
        "1/2 1",
        "1 1/0",

        // Missing numerator or denominator
        "/2",
        "1/",
        "/",
        "1//2",

        // Too many separators or components
        "1/2/3",
        "1 1 1/2",
        "1 1/2 3",

        // Missing or invalid values
        "",
        " ",
        "abc",
        "one/half",
        "1/a",
        "a/2",

        // Invalid numeric formats
        "1.5/2",
        "1/2.5",
        "--1/2",
        "1/-/2",

        // Malformed mixed fractions
        "1 /2",
        "1/ 2",
        "1  /2",
        "1 1/",
        "1 /",
        "1  / 2",

        // Invalid whole-number formats
        "1..0",
        "1a",
        "1 2/",
        "1 / 2 / 3",
        
        // Negative fractions
        "1/-2",
        "-1/2",
        "/-1"
    })
    void invalidFractionStringsThrowException(String input) {
    	assertThrows(
    		    RuntimeException.class,
    		    () -> new Fraction(input),
    		    "Expected invalid fraction to be rejected: '" + input + "'"
    		);
    }
	
}
