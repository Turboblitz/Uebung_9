import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class MoodleTest {

    private static final Moodle testMoodle = new Moodle();

    public static final Vorlesung[] TEST_VORLESUNGEN = new Vorlesung[]{
            new Vorlesung("Ana", 1),
            new Vorlesung("Lina", 2),
            new Vorlesung("sep", 3)
    };

    public static final Student[] TEST_STUDENTEN = new Student[]{
            new Student("A_F", "A_L", UNI_ID.UDE, 1),
            new Student("B_F", "B_L", UNI_ID.UDE, 2),
            new Student("C_F", "C_L", UNI_ID.UDE, 3),
            new Student("D_F", "D_L", UNI_ID.UDE, 4)
    };


    @Test
    void immatrikuliereTest() {
        testMoodle.getStudentList().clear();
        Student testStudent = new Student("A", "B", UNI_ID.UDE, 0);
        assertEquals(0, testMoodle.getStudentList().size());
        testMoodle.immatrikuliere(testStudent);
        assertEquals(testMoodle.getStudentList().size(), 1);
        Assertions.assertTrue(testMoodle.getStudentList().contains(testStudent));
    }

    @Test
    void exmatrikuliereTest() {
        testMoodle.getStudentList().clear();
        Student testStudent = new Student("A", "B", UNI_ID.UDE, 1);
        testMoodle.getStudentList().add(testStudent);
        assertThrows(ImmatriculationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Student exceptionStudent = new Student(
                        "A",
                        "B",
                        UNI_ID.UDE,
                        1);
                testMoodle.immatrikuliere(exceptionStudent);
            }
        });
        testMoodle.exmatrikuliere(testStudent);
        assertFalse(testMoodle.getStudentList().contains(testStudent));


    }

    @Test
    void vorlesungenTest() {
        testMoodle.getVorlesungList().clear();
        Vorlesung addedVorlesung = new Vorlesung("Programmierung", 1);
        testMoodle.vorlesungHinzufuegen(addedVorlesung);
        assertEquals(1, testMoodle.getVorlesungList().size());
        assertTrue(testMoodle.getVorlesungList().contains(addedVorlesung));
        assertThrows(VorlesungExistsException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Vorlesung exeptionVorlesung = new Vorlesung("Programmierung", 1);
                testMoodle.vorlesungHinzufuegen(exeptionVorlesung);
            }
        });
        VorlesungTest vorlesungTest = new VorlesungTest();
        vorlesungTest.anmelden();
        vorlesungTest.pruefungDurchfuehren();
        vorlesungTest.pruefungWithGivenVorlesung(addedVorlesung);

    }

    @Test
    void ioStudentenTest() {
        emptyMoodleData();
        final String OUTPUT_FILE_NAME = "output.txt";
        emptyMoodleData();
        prepareTestDataForScribeList();
        checkStudentScribeOutput(OUTPUT_FILE_NAME);
        emptyMoodleData();
        checkStudentScribeInput(OUTPUT_FILE_NAME);


    }

    private void checkStudentScribeInput(String output_file_name) {
        testMoodle.leseStudenten(output_file_name);
        for (Student s : TEST_STUDENTEN) {
            assertTrue(testMoodle.getStudentList().contains(s));
        }

    }


    private void checkStudentScribeOutput(String OUTPUT_FILE_NAME) {
        BufferedReader reader = getReaderFromFile(OUTPUT_FILE_NAME);
        checkOutputFile(reader);
    }

    private void emptyMoodleData() {
        testMoodle.getStudentList().clear();
        testMoodle.getVorlesungList().clear();
    }

    @Test
    void ioVorlesungTest() {

        this.emptyMoodleData();

        final String TEST_FILE_NAME = "vorlesung_test.txt";
        final Vorlesung testVorlesung = new Vorlesung("Test", 5);

        prepareMoodleForWriteVorlesung(TEST_FILE_NAME, testVorlesung);
        checkProperIO(TEST_FILE_NAME, testVorlesung);


    }

    private void checkProperIO(String TEST_FILE_NAME, Vorlesung testVorlesung) {

        BufferedReader outputReader = this.getReaderFromFile(TEST_FILE_NAME);
        checkForProperFileOutput(TEST_FILE_NAME, testVorlesung, outputReader);
        checkForProperFileReading(TEST_FILE_NAME, testVorlesung);
    }

    private void checkForProperFileOutput(String TEST_FILE_NAME,
                                          Vorlesung testVorlesung,
                                          BufferedReader outputReader) {

        testVorlesungsOutPutForNotDonePruefung(outputReader);
        doPruefung(testVorlesung, TEST_FILE_NAME);
        outputReader = this.getReaderFromFile(TEST_FILE_NAME);
        testVorlesungsOutPutForDonePruefung(outputReader);
    }

    private void checkForProperFileReading(String TEST_FILE_NAME, Vorlesung testVorlesung) {

        testReadingOfEmptyVorlesung(TEST_FILE_NAME, testVorlesung);
        writeFullVorlesung(TEST_FILE_NAME, testVorlesung);
        testReadingOfFullVorlesung(TEST_FILE_NAME, testVorlesung);
    }

    private void testReadingOfFullVorlesung(String TEST_FILE_NAME, Vorlesung testVorlesung) {

        testMoodle.getVorlesungList().clear();
        testMoodle.leseVorlesung(TEST_FILE_NAME);
        assertTrue(testMoodle.getVorlesungList().contains(testVorlesung));
    }

    private void writeFullVorlesung(String TEST_FILE_NAME, Vorlesung testVorlesung) {

        this.emptyMoodleData();
        for (Student s : TEST_STUDENTEN) {
            testMoodle.immatrikuliere(s);
            testVorlesung.anmelden(s);
        }
        testMoodle.vorlesungHinzufuegen(testVorlesung);
        testMoodle.schreibeVorlesung(testVorlesung, TEST_FILE_NAME);
    }

    private void testReadingOfEmptyVorlesung(String TEST_FILE_NAME, Vorlesung testVorlesung) {

        this.emptyMoodleData();
        testMoodle.leseVorlesung(TEST_FILE_NAME);
        assertTrue(testMoodle.getVorlesungList().contains(testVorlesung));
    }

    private void doPruefung(Vorlesung testVorlesung, String fileName) {

        testVorlesung.pruefungDurchfuehren();
        testMoodle.schreibeVorlesung(testVorlesung, fileName);
    }

    private void testVorlesungsOutPutForDonePruefung(BufferedReader outputReader) {

        String[] outputLines = {
                "Test;5;",
                "1;[1-6];",
                "2;[1-6];",
                "3;[1-6];",
                "4;[1-6];"
        };
        try {
            this.assertEqualOutput(outputReader, outputLines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void testVorlesungsOutPutForNotDonePruefung(BufferedReader outputReader) {

        String[] outputLines = {
                "Test;5;",
                "1;",
                "2;",
                "3;",
                "4;"
        };
        try {
            this.assertEqualOutput(outputReader, outputLines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void prepareMoodleForWriteVorlesung(String TEST_FILE_NAME, Vorlesung testVorlesung) {

        testMoodle.getVorlesungList().clear();
        for (Student s : TEST_STUDENTEN) {
            testVorlesung.anmelden(s);
        }
        testMoodle.vorlesungHinzufuegen(testVorlesung);
        testMoodle.schreibeVorlesung(testVorlesung, TEST_FILE_NAME);
    }

    private void checkOutputFile(BufferedReader reader) {

        final String[] outputLines = {
                "A_F;A_L;UDE;1;1;Ana;",
                "B_F;B_L;UDE;2;1;Ana;2;Lina;",
                "C_F;C_L;UDE;3;1;Ana;",
                "D_F;D_L;UDE;4;1;Ana;2;Lina;",
        };
        try {
            assertEqualOutput(reader, outputLines);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("IOEXCEPTION");
            assertTrue(false);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void prepareTestDataForScribeList() {

        for (int i = 0; i < TEST_STUDENTEN.length; i++) {
            TEST_VORLESUNGEN[0].anmelden(TEST_STUDENTEN[i]);
            if (i % 2 == 1) TEST_VORLESUNGEN[1].anmelden(TEST_STUDENTEN[i]);

        }
        for (Vorlesung v : TEST_VORLESUNGEN) {
            testMoodle.vorlesungHinzufuegen(v);
        }
        for (Student s : TEST_STUDENTEN) {
            testMoodle.immatrikuliere(s);
        }
        testMoodle.schreibeStudentenListe();
    }

    private BufferedReader getReaderFromFile(String fileName) {

        File outputFile = new File(fileName);
        assertTrue(outputFile.isFile());
        assertTrue(outputFile.exists());
        try {
            return new BufferedReader(new FileReader(outputFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("didn't find output file");
    }

    private void assertEqualOutput(BufferedReader reader, String... outputLines) throws IOException {

        for (String line : outputLines) {
            String readLine = reader.readLine();
            assertTrue(readLine.matches(line));
        }
    }
}
