package slapp.editor.tests;

import static java.lang.Math.round;

public class RoundingTest {

    public RoundingTest() {
        System.out.println("up: " + (double) round(2.999)  + " down: " + (double) round(3.001 ));
    }
}
