package be.sel2.api.util_tests;

import be.sel2.api.util.InputValidator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ValidatorTest {

    private static String generateRandString(int len) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(len)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    @ParameterizedTest
    @MethodSource("provideEmails")
    void testEmail(String email, boolean expected) {
        assertEquals(expected, InputValidator.validateEmail(email));
    }

    private static Stream<Arguments> provideEmails() {
        return Stream.of(
                Arguments.of("mail", false),
                Arguments.of(null, false),
                Arguments.of("User.mail@live.be", false), // Contains upper case characters
                Arguments.of("user.mail@live.be", true),
                Arguments.of("a b@mail.com", false),
                Arguments.of("🚘🚎🚍@mail.com", false),
                Arguments.of("@", false),
                Arguments.of("x@y", false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideOvo")
    void testOvo(String ovo, boolean expected) {
        assertEquals(expected, InputValidator.validateOVO(ovo));
    }

    private static Stream<Arguments> provideOvo() {
        return Stream.of(
                Arguments.of("OVOXXXXXX", false),
                Arguments.of(null, false),
                Arguments.of("OVO1", false),
                Arguments.of("OVO3468954", false),
                Arguments.of("OVO34689", false),
                Arguments.of("OVO346895", true),
                Arguments.of("34689", false),
                Arguments.of("0w034689", false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideKbo")
    void testKbo(String kbo, boolean expected) {
        assertEquals(expected, InputValidator.validateKBO(kbo));
    }

    private static Stream<Arguments> provideKbo() {
        return Stream.of(
                Arguments.of("XXXXXXXXXX", false),
                Arguments.of(null, false),
                Arguments.of("0XXXXXXXXX", false),
                Arguments.of("0123456789", true),
                Arguments.of("01234567891", false),
                Arguments.of("012345678", false),
                Arguments.of("1234567890", true),
                Arguments.of("2345678901", false),
                Arguments.of("1859634795", true)
        );
    }

    @ParameterizedTest
    @MethodSource("provideNis")
    void testNis(String nis, boolean expected) {
        assertEquals(expected, InputValidator.validateNIS(nis));
    }

    private static Stream<Arguments> provideNis() {
        return Stream.of(
                Arguments.of("XXXXX", false),
                Arguments.of(null, false),
                Arguments.of("07653", true),
                Arguments.of("7653", false),
                Arguments.of("076538", false)
        );
    }

    @ParameterizedTest
    @MethodSource("providePhone")
    void testPhone(String phone, boolean expected) {
        assertEquals(expected, InputValidator.validatePhoneNumber(phone));
    }

    private static Stream<Arguments> providePhone() {
        return Stream.of(
                Arguments.of("X", false),
                Arguments.of(null, false),
                Arguments.of("093854466", true), // Local Belgian number
                Arguments.of("056 67 70 21", true), // Belgian number
                Arguments.of("056677021", true), // Belgian number with no spacing
                Arguments.of("+32 056 67 70 21", true), // With spacing, international format
                Arguments.of("+32056677021", true), // No spacing, international format
                Arguments.of("123-567-8905", false), // American number
                Arguments.of("1234567890", false),
                Arguments.of("0612345678", true) // Dutch number
        );
    }

    @ParameterizedTest
    @MethodSource("providePassword")
    void testPassword(String password, boolean expected) {
        assertEquals(expected, InputValidator.validatePassword(password));
    }

    private static Stream<Arguments> providePassword() {
        return Stream.of(
                Arguments.of("Xx2", false), //Too short
                Arguments.of(null, false),
                Arguments.of("helpme123", false), //No upper case
                Arguments.of("HELPME123", false), //No lower case
                Arguments.of("WhyHelloThere", false), //No numbers
                Arguments.of("TheLegend47", true),
                Arguments.of("I_cast_F1reb@ll", true),
                Arguments.of("W@y2L00000000ngPassword" + generateRandString(300), false) //Too long
        );
    }

    @ParameterizedTest
    @MethodSource("provideName")
    void testName(String name, boolean expected) {
        assertEquals(expected, InputValidator.validateName(name));
    }

    private static Stream<Arguments> provideName() {
        return Stream.of(
                Arguments.of("", false),
                Arguments.of("\t", false),
                Arguments.of(" ", false),
                Arguments.of(null, false),
                Arguments.of("Jond\nsloo", false), //Contains a newline
                Arguments.of("Justin", true),
                Arguments.of("X Æ A-Xii", true),
                Arguments.of("A Test Company", true),
                Arguments.of("A company with a 256 character name, " +
                        generateRandString(256 - 37), true),
                Arguments.of("A company with a 257 character name, " +
                        generateRandString(257 - 37), false), //Too long
                Arguments.of("A very, VERY long string of characters, " +
                        generateRandString(500), false) //Too long
        );
    }

    @ParameterizedTest
    @MethodSource("provideShortTexts")
    void testAllowShortTexts(String input, boolean expected) {
        assertEquals(expected, InputValidator.validateMaxLenShort(input));
    }

    private static Stream<Arguments> provideShortTexts() {
        return Stream.of(
                Arguments.of("", true),
                Arguments.of(null, true),
                Arguments.of("just a test string", true),
                Arguments.of("A string that is 35 characters long", true),
                Arguments.of("A string that is 256 characters long, " +
                        generateRandString(256 - 38), true),
                Arguments.of("A string that is 257 characters long, " +
                        generateRandString(257 - 38), false), //Too long
                Arguments.of("A string that is 4000 characters long, " +
                        generateRandString(4000 - 39), false), //Too long
                Arguments.of("A very, VERY long string of characters, " +
                        generateRandString(5000), false) //Too long
        );
    }

    @ParameterizedTest
    @MethodSource("provideLongTexts")
    void testAllowLongTexts(String input, boolean expected) {
        assertEquals(expected, InputValidator.validateMaxLenLong(input));
    }

    private static Stream<Arguments> provideLongTexts() {
        return Stream.of(
                Arguments.of("", true),
                Arguments.of(null, true),
                Arguments.of("just a test description", true),
                Arguments.of("A string that is 35 characters long", true),
                Arguments.of("A string that is 2000 characters long, " +
                        generateRandString(2000 - 39), true),
                Arguments.of("A string that is 4096 characters long, " +
                        generateRandString(4096 - 39), true),
                Arguments.of("A string that is 4097 characters long, " +
                        generateRandString(4097 - 39), false), //Too long
                Arguments.of("A very, VERY long string of characters, " +
                        generateRandString(5000), false) //Too long
        );
    }

    @ParameterizedTest
    @MethodSource("provideVeryLongTexts")
    void testAllowVeryLongTexts(String input, boolean expected) {
        assertEquals(expected, InputValidator.validateMaxLenFeedback(input));
    }

    private static Stream<Arguments> provideVeryLongTexts() {
        return Stream.of(
                Arguments.of("", true),
                Arguments.of(null, true),
                Arguments.of("just some test feedback", true),
                Arguments.of("A string that is 35 characters long", true),
                Arguments.of("A string that is 2000 characters long, " +
                        generateRandString(2000 - 39), true),
                Arguments.of("A string that is 16384 characters long, " +
                        generateRandString(16384 - 40), true),
                Arguments.of("A string that is 16385 characters long, " +
                        generateRandString(16385 - 40), false), //Too long
                Arguments.of("A very, VERY long string of characters, " +
                        generateRandString(17000), false) //Too long
        );
    }

    @ParameterizedTest
    @MethodSource("provideDates")
    void testDeadlineOrder(Date before, Date after, boolean expected) {
        assertEquals(expected, InputValidator.validateDeadlines(before, after));
    }

    private static Stream<Arguments> provideDates() {
        Date rightNowDate = new Date();

        Calendar before = Calendar.getInstance();
        before.setTime(rightNowDate);
        before.set(Calendar.YEAR, before.get(Calendar.YEAR) - 1);
        Date beforeDate = before.getTime();

        Calendar after = Calendar.getInstance();
        after.setTime(rightNowDate);
        after.set(Calendar.YEAR, after.get(Calendar.YEAR) + 1);
        Date afterDate = after.getTime();

        return Stream.of(
                Arguments.of(null, null, true),
                Arguments.of(null, rightNowDate, true),
                Arguments.of(rightNowDate, null, true),
                Arguments.of(rightNowDate, rightNowDate, true),
                Arguments.of(beforeDate, afterDate, true),
                Arguments.of(beforeDate, rightNowDate, true),
                Arguments.of(rightNowDate, afterDate, true),
                Arguments.of(afterDate, beforeDate, false),
                Arguments.of(afterDate, rightNowDate, false),
                Arguments.of(rightNowDate, beforeDate, false)
        );
    }

}
