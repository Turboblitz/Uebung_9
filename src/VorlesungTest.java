import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VorlesungTest {

    private Vorlesung testVorlesung = new Vorlesung("Test", 1);
    private final Student testStudent = new Student("A", "b", UNI_ID.UDE, 1);

    @Test void anmelden(){
        testVorlesung.getList().clear();
        testVorlesung.anmelden(testStudent);
        assertEquals(1, testVorlesung.getList().size());
        testVorlesung.anmelden(testStudent);
        assertEquals(1, testVorlesung.getList().size());
    }

    @Test void pruefungDurchfuehren(){
        testVorlesung.getList().clear();
        ArrayList<Student> testStudents = new ArrayList<>();
        for(int i = 0; i<40;i++){
            testVorlesung.getList().add(new Student("A" + i, "B" + i, UNI_ID.UDE, i));
        }
        assertEquals(40, testVorlesung.getList().size());
        testVorlesung.pruefungDurchfuehren();
        testVorlesung.getList().forEach(s -> assertTrue(testVorlesung.getNote(s) <= 6));
        testVorlesung.getList().forEach(s -> assertTrue(testVorlesung.getNote(s) >= 1));

        assertThrows(StudentNotFoundExeption.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Student testForExeption = new Student();
                testVorlesung.getNote(testForExeption);
            }
        });

    }

    void pruefungWithGivenVorlesung(Vorlesung givenVorlesung){
        testVorlesung = givenVorlesung;
        pruefungDurchfuehren();
    }


}
