package com.ayo.conversion.controller;

import com.ayo.conversion.model.ConversionConfig;
import com.ayo.conversion.service.ConversionConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ConversionControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ConversionConfigService conversionConfigService;

    @BeforeEach
    public void setup() {
        RestAssured.port = port;

        ConversionConfig meterToFoot = new ConversionConfig();
        meterToFoot.setFromUnit("meter");
        meterToFoot.setToUnit("foot");
        meterToFoot.setConvertFactor(3.28084);

        ConversionConfig lengthConversion = conversionConfigService.retrieveConversion(meterToFoot.getFromUnit(),
                meterToFoot.getToUnit());
        if (lengthConversion == null) {
            conversionConfigService.saveConversion(meterToFoot);
        }

        ConversionConfig celsiusToFahrenheit = new ConversionConfig();
        celsiusToFahrenheit.setFromUnit("celsius");
        celsiusToFahrenheit.setToUnit("fahrenheit");
        celsiusToFahrenheit.setConvertFactor(1.8);
        celsiusToFahrenheit.setAddend(32.0);

        ConversionConfig temperatureConversion = conversionConfigService
                .retrieveConversion(celsiusToFahrenheit.getFromUnit(), celsiusToFahrenheit.getToUnit());
        if (temperatureConversion == null) {
            conversionConfigService.saveConversion(celsiusToFahrenheit);
        }

        ConversionConfig kilometerToMile = new ConversionConfig();
        kilometerToMile.setFromUnit("kilometer");
        kilometerToMile.setToUnit("mile");
        kilometerToMile.setConvertFactor(0.621371);

        ConversionConfig distanceConversion = conversionConfigService.retrieveConversion(kilometerToMile.getFromUnit(),
                kilometerToMile.getToUnit());
        if (distanceConversion == null) {
            conversionConfigService.saveConversion(kilometerToMile);
        }

    }

    @Test
    public void testAddConversion() {
        ConversionConfig conversion = new ConversionConfig();
        conversion.setFromUnit("metera");
        conversion.setToUnit("kilometera");
        conversion.setConvertFactor(1000);

        Response postResponse = RestAssured.given()
                .contentType("application/json")
                .body(conversion)
                .post("/conversion/new");

        assertEquals(201, postResponse.statusCode());

    }

    @Test
    public void testRetrieveConversionConfigByFromUnitAndToUnit() {

        Response getResponse = RestAssured.given()
                .contentType("application/json")
                .get("/conversion/show?fromUnit=meter&toUnit=foot");

        assertEquals("foot", getResponse.jsonPath().getString("result.toUnit"));
        assertEquals("meter", getResponse.jsonPath().getString("result.fromUnit"));
        assertEquals(3.28084, getResponse.jsonPath().getDouble("result.convertFactor"), 0.001);
    }

    @Test
    public void testRetrieveAllConversions() {
        Response response = RestAssured.given()
                .contentType("application/json")
                .get("/conversion/show/all");

        assertEquals(200, response.statusCode());
        assertTrue(response.getBody().asString().contains("result")); // Add more assertions based on your expected data
    }

    @Test
    public void testUpdateConversion() {
        ConversionConfig conversion = new ConversionConfig();
        conversion.setFromUnit("meter");
        conversion.setToUnit("foot");
        conversion.setConvertFactor(3.28084);

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(conversion)
                .put("/conversion/update");

        assertEquals(200, response.statusCode());
        assertEquals("foot", response.jsonPath().getString("result.toUnit"));
    }

    @Test
    public void testConvert() {
        Response response = RestAssured.given()
                .contentType("application/json")
                .get("/conversion/convert?fromUnit=kilometer&toUnit=mile&inputValue=10");

        assertEquals(200, response.statusCode());
        assertEquals(6.21371, response.jsonPath().getDouble("result"), 0.001);
    }

    @Test
    public void testRetrieveConversionMissingParameter() {
        Response response = RestAssured.given()
                .contentType("application/json")
                .get("/conversion/show?fromUnit=meters");

        assertEquals(400, response.statusCode());
        assertTrue(response.getBody().asString().contains("Required parameter [toUnit] is not present."));
    }

    @Test
    public void testAddConversionWithNegativeFactor() {
        ConversionConfig conversion = new ConversionConfig();
        conversion.setFromUnit("meter");
        conversion.setToUnit("kilometer");
        conversion.setConvertFactor(-1);

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(conversion)
                .post("/conversion/new");

        assertEquals(400, response.statusCode());
        assertTrue(response.getBody().asString().contains("Missing or incorrect parameter(s) for conversion create."));
    }

    @Test
    public void testUpdateNonExistentConversion() {
        ConversionConfig conversion = new ConversionConfig();
        conversion.setFromUnit("badFromUnit");
        conversion.setToUnit("badToUnit");
        conversion.setConvertFactor(1000);

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(conversion)
                .put("/conversion/update");

        assertEquals(404, response.statusCode());
        assertTrue(response.getBody().asString().contains("Conversion from badFromUnit to badToUnit not found"));
    }

    @Test
    public void testRetrieveNonExistentConversionConfig() {
        Response response = RestAssured.given()
                .contentType("application/json")
                .get("/conversion/show?fromUnit=nonexistent&toUnit=nonexistent");

        assertEquals(404, response.statusCode());
        assertTrue(response.getBody().asString().contains("not found"));
    }

    @Test
    public void testConvertFromMeterToFoot() {
        double inputValue = 1;
        Response response = RestAssured.given()
                .contentType("application/json")
                .get("/conversion/convert?fromUnit=meter&toUnit=foot&inputValue=" + inputValue);

        assertEquals(200, response.statusCode());
        assertEquals(3.28084, response.jsonPath().getDouble("result"), 0.001);
    }

    @Test
    public void testConvertFromMeterToFootReverse() {
        double inputValue = 1;
        Response response = RestAssured.given()
                .contentType("application/json")
                .get("/conversion/convert?fromUnit=foot&toUnit=meter&inputValue=" + inputValue);

        assertEquals(200, response.statusCode());
        assertEquals(0.3048, response.jsonPath().getDouble("result"), 0.001);
    }

    @Test
    public void testConvertZeroFromCelsiusToFahrenheit() {

        Response response = RestAssured.given()
                .contentType("application/json")
                .get("/conversion/convert?fromUnit=celsius&toUnit=fahrenheit&inputValue=0");

        assertEquals(400, response.statusCode());
        assertTrue(response.getBody().asString().contains("Incorrect value for parameter(s) for the conversion"));
    }

    @Test
    public void testConvertFromSimilarSourceAndTargetUnit() {
        double inputValue = 10;
        Response response = RestAssured.given()
                .contentType("application/json")
                .get("/conversion/convert?fromUnit=meter&toUnit=meter&inputValue=" + inputValue);

        assertEquals(200, response.statusCode());
        assertEquals(10, response.jsonPath().getDouble("result"), 0.001);
    }

    @Test
    public void testAddExistingConversionConfig() {
        ConversionConfig conversion = new ConversionConfig();
        conversion.setFromUnit("meter");
        conversion.setToUnit("kilometer");
        conversion.setConvertFactor(1000);

        // Add the conversion for the first time
        RestAssured.given()
                .contentType("application/json")
                .body(conversion)
                .post("/conversion/new");

        // Try to add the same conversion again
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(conversion)
                .post("/conversion/new");

        assertEquals(409, response.statusCode());
        assertTrue(response.getBody().asString().contains("already exists"));
    }

    @Test
    public void testRemoveConversion() {
        Response response = RestAssured.given()
                .contentType("application/json")
                .delete("/conversion/remove?fromUnit=metera&toUnit=kilometera");

        assertEquals(200, response.statusCode());
        assertTrue(response.getBody().asString().contains("removed"));
    }

}
