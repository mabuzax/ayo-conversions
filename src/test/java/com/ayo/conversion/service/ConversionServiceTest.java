package com.ayo.conversion.service;

import com.ayo.conversion.exception.ConversionConfigConflictException;
import com.ayo.conversion.exception.ConversionConfigNotFoundException;
import com.ayo.conversion.model.ConversionConfig;
import com.ayo.conversion.repository.ConversionConfigRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ConversionConfigServiceTest {

    private ConversionConfigRepository repository = Mockito.mock(ConversionConfigRepository.class);
    private ConversionConfigService service = new ConversionConfigService(repository);

    /**
     * Test for existing/configured conversions.
     * Simulates a conversion from 'meter' to 'foot' configured in the repo.
     * Expected outcome: Conversion should succeed and return correct value.
     */
    @Test
    public void testConversionForExistingConversion() {
        ConversionConfig conversion = new ConversionConfig();
        conversion.setFromUnit("meter");
        conversion.setToUnit("foot");
        conversion.setConvertFactor(3.28084);
        Mockito.when(repository.findByFromUnitAndToUnit("meter", "foot")).thenReturn(Optional.of(conversion));
        assertEquals(32.8084, service.convert("meter", "foot", 10), 0.001);
    }

    /**
     * Test for existing/configured conversion's reverse conversion.
     * Simulates a conversion from 'foot' to 'meter', where configured as 'meter' to
     * 'foot' in the repo.
     * Expected outcome: Conversion should succeed and return correct value.
     */
    @Test
    public void testConversionForReverseConversionOfExistingConversion() {
        ConversionConfig conversion = new ConversionConfig();
        conversion.setFromUnit("meter");
        conversion.setToUnit("foot");
        conversion.setConvertFactor(3.28084);
        Mockito.when(repository.findByFromUnitAndToUnit("foot", "meter")).thenReturn(Optional.of(conversion));
        assertEquals(3.0479999, service.convert("foot", "meter", 10), 0.001);
    }

    /**
     * Test for conversions not in the repo.
     * Simulates a conversion from 'meter' to 'foot' if not defined in the repo.
     * Expected outcome: A ConversionConfigNotFoundException should be thrown.
     */
    @Test
    public void testConversionForUndefinedConversion() {
        Mockito.when(repository.findByFromUnitAndToUnit("meter", "foot")).thenReturn(Optional.empty());
        assertThrows(ConversionConfigNotFoundException.class, () -> service.convert("meter", "foot", 10));
    }

    /**
     * Test for adding a new (non-existent) conversion config to the repo.
     * Simulates addition of a new conversion of 'inch' to 'yard'.
     * Expected outcome: A new Conversion config must be returned.
     */
    @Test
    public void testAddNewNonExistentConversionToRepo() {
        ConversionConfig conversion = new ConversionConfig();
        conversion.setFromUnit("inch");
        conversion.setToUnit("yard");
        conversion.setConvertFactor(0.0277778);

        Mockito.when(repository.findByFromUnitAndToUnit("inch", "yard")).thenReturn(Optional.empty());
        Mockito.when(repository.findByFromUnitAndToUnit("yard", "inch")).thenReturn(Optional.empty());
        Mockito.when(repository.save(conversion)).thenReturn(conversion);

        ConversionConfig savedConversion = service.saveConversion(conversion);

        assertEquals(conversion, savedConversion);
        Mockito.verify(repository, Mockito.times(1)).save(conversion);
    }

    /**
     * Test for adding a Existing conversion config to the repo.
     * Simulates addition of a duplicate conversion of 'meter' to 'foot'.
     * Expected outcome: A ConversionConfigConflictException should be thrown.
     */
    @Test
    public void testAddExistingConversionToRepo() {
        ConversionConfig conversion = new ConversionConfig();
        conversion.setFromUnit("meter");
        conversion.setToUnit("foot");
        conversion.setConvertFactor(3.28084);

        Mockito.when(repository.findByFromUnitAndToUnit("meter", "foot")).thenReturn(Optional.of(conversion));

        assertThrows(ConversionConfigConflictException.class, () -> service.saveConversion(conversion));
    }

    /**
     * Test for adding a null conversion config
     */
    @Test
    public void testAddNullConversionToRepo() {
        assertThrows(IllegalArgumentException.class, () -> service.saveConversion(null));
    }

    /**
     * Test for illegal values emptyStrings and Nulls provided
     * Simulate conversion of values less or equal to zero
     *
     */
    @Test
    public void testConvertWithNullOrEmptyArguments() {
        assertThrows(IllegalArgumentException.class, () -> service.convert(null, "foot", 10));
        assertThrows(IllegalArgumentException.class, () -> service.convert("", "foot", 10));
        assertThrows(IllegalArgumentException.class, () -> service.convert("meter", null, 10));
        assertThrows(IllegalArgumentException.class, () -> service.convert("meter", "", 10));
        assertThrows(IllegalArgumentException.class, () -> service.convert("meter", "foot", 0));
        assertThrows(IllegalArgumentException.class, () -> service.convert("meter", "foot", -1));
    }

}
