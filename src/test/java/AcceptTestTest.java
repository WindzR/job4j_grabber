import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class AcceptTestTest {

    @Test
    public void whenReversePositive() {
        int result = AcceptTest.reverse(12345);
        int expected = 54321;
        assertThat(expected, is(result));
    }

    @Test
    public void whenReverseNegative() {
        int result = AcceptTest.reverse(-12345);
        int expected = -54321;
        assertThat(expected, is(result));
    }

    @Test
    public void whenReverse100() {
        int result = AcceptTest.reverse(100);
        int expected = 1;
        assertThat(expected, is(result));
    }
}