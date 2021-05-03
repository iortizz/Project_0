package appTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AppTests {
    private static Validation validation;

    @BeforeAll
    public static void setupValidation() {
        validation = new Validation();
    }

    @Test
    public static void testIsValidOption() {
        int choice = 3;
        Assertions.assertEquals(true, validation.isValidOption(choice));
    }

    @Test
    public void testIsValidContactNumber() {
        Assertions.assertEquals(true, validation.isValidContactNumber("1231231212"));
    }

    @Test
    public void testIsNotValidContactNumber() {
        Assertions.assertEquals(false, validation.isValidContactNumber("null"));
    }

    @Test
    public void testIsValidEmail() {
        Assertions.assertEquals(true, validation.isValidEmail("email@aol.com"));
    }

    @Test
    public void testIsNotValidemail() {
        Assertions.assertEquals(false, validation.isNotValidEmail("null"));
    }

    @Test
    public void testIsValidPassword() {
        Assertions.assertEquals(true, validation.isValidPassword("Orange123"));
    }

    @Test
    public void testIsNotValidPassword() {
        Assertions.assertEquals(false, validation.isNotValidPassword("null"));
    }

    @Test
    public void testIsValiddeposit() {
        Assertions.assertEquals(true, validation.isValidDeposit(9000));
    }

    @Test
    public void testIsNotValidDepsit() {
        Assertions.assertEquals(false, validation.isValidDeposit(50));
    }

    @Test
    public void testIsValidAccount() {
        Assertions.assertEquals(true, validation.isValidAccount("checking"));
    }

    @Test
    public void testIsNotValidAccount() {
        Assertions.assertEquals(false, validation.isValidAccount("check"));
    }


}
