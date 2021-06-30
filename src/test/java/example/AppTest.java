package example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test.
 */
class AppTest {
    /**
     * "Rigorous Test" :)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertEquals(-2, (-8) % 6);
        assertEquals(-2, ((Float) ((float) -2.8)).intValue());
    }
}
