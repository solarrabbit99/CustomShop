package example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

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
        int[] array = new int[0];
        System.out.println(Arrays.toString(array));
        // String arrayString = "[2, 3, 4]";
        // if (arrayString.equalsIgnoreCase("[]")) {

        // }
        // arrayString = arrayString.substring(1, arrayString.length() - 1);
        // String[] strArr = arrayString.split(", ");
        // List<Integer> intList = new ArrayList<>();
        // for (String e : strArr) {
        // intList.add(Integer.parseInt(e));
        // }
        assertTrue(true);
    }

    @Test
    public void yamlTest() {

    }
}
