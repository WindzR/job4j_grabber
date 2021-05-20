import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class TriggerTest {

    @Test
    public void whenReversePositive() {
        int result = Trigger.reverse(12345);
        int expected = 54321;
        assertThat(expected, is(result));
    }

    @Test
    public void whenReverseNegative() {
        int result = Trigger.reverse(-12345);
        int expected = -54321;
        assertThat(expected, is(result));
    }

    @Test
    public void whenReverse100() {
        int result = Trigger.reverse(100);
        int expected = 1;
        assertThat(expected, is(result));
    }
}