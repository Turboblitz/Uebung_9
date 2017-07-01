import java.io.*;
import java.util.*;

public class Moodle {

    private List<Student> studentList;
    private List<Vorlesung> vorlesungList;

    private static final String ENCAPSULATION_STRING = ";";

    public Moodle() {
        studentList = new ArrayList<>();
        vorlesungList = new ArrayList<>();
    }

    public void immatrikuliere(Student student) {
        int matrikelnummer = student.getMatrikelNummer();
        throwExepctionIfMatrExists(matrikelnummer);
        this.studentList.add(student);
    }

    private void throwExepctionIfMatrExists(int matrikelnummer) {
        for (Student s : this.getStudentList()) {
            if (matrikelnummer == s.getMatrikelNummer()) {
                throw new ImmatriculationException(
                        "Sorry cant immatriculate someone with existing number"
                );
            }
        }
    }

    public List<Student> getStudentList() {
        return studentList;
    }

    public void exmatrikuliere(Student testStudent) {

        studentList.remove(testStudent);
    }

    public void vorlesungHinzufuegen(Vorlesung addedVorlesung) {
        int vorlesungsNummer = addedVorlesung.getId();
        for (Vorlesung v : this.vorlesungList) {
            if (v.equals(addedVorlesung)) throw new VorlesungExistsException(
                    "Vorlesung already exists");
        }
        this.vorlesungList.add(addedVorlesung);
    }

    public List<Vorlesung> getVorlesungList() {
        return this.vorlesungList;
    }

    public void schreibeStudentenListe() {
        try {
            PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
            printStudentInfo(writer);
            writer.close();
        } catch (IOException e) {
        }
    }

    private void printStudentInfo(PrintWriter writer) {

        for (Student s : this.studentList) {

            StringBuilder builder = new StringBuilder();
            appendStudentInfo(s, builder);
            appendVorlesungsInfo(s, builder);

            writer.write(builder.toString());
            writer.write(System.lineSeparator());
        }
    }

    private void appendVorlesungsInfo(Student s, StringBuilder builder) {
        for (Vorlesung v : this.vorlesungList) {
            if (!v.getList().contains(s)) continue;
            builder.append(v.getId());
            builder.append(ENCAPSULATION_STRING);
            builder.append(v.getName());
            builder.append(ENCAPSULATION_STRING);
        }
    }

    private void appendStudentInfo(Student s, StringBuilder builder) {
        builder.append(s.getFirstName());
        builder.append(ENCAPSULATION_STRING);
        builder.append(s.getLastName());
        builder.append(ENCAPSULATION_STRING);
        builder.append(s.getUniversity());
        builder.append(ENCAPSULATION_STRING);
        builder.append(s.getMatrikelNummer());
        builder.append(ENCAPSULATION_STRING);
    }

    public void schreibeVorlesung(Vorlesung testVorlesung, String fileName) {

        if (!this.vorlesungList.contains(testVorlesung)) throw new RuntimeException(
                "vorlesung nicht vorhanden"
        );
        try {
            PrintWriter writer = new PrintWriter(fileName);

            sortStudents(testVorlesung);
            if (testVorlesung.pointMap.isEmpty()) printMatrikelNumbers(testVorlesung, writer);
            else printTestInfo(testVorlesung, writer);

            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    private void printTestInfo(Vorlesung testVorlesung, PrintWriter writer) {

        printVorlesungHeaderInformation(testVorlesung, writer);

        testVorlesung.getList().forEach(s -> {
            writer.write("" + s.getMatrikelNummer());
            writer.write(ENCAPSULATION_STRING);
            writer.write("" + testVorlesung.getPointMap().get(s));
            writer.write(ENCAPSULATION_STRING);
            writer.write(System.lineSeparator());
        });
    }

    private void printVorlesungHeaderInformation(Vorlesung testVorlesung, PrintWriter writer) {
        writer.write(testVorlesung.getName());
        writer.write(ENCAPSULATION_STRING);
        writer.write(testVorlesung.getId() + "");
        writer.write(ENCAPSULATION_STRING);
        writer.write(System.lineSeparator());
    }

    private void sortStudents(Vorlesung testVorlesung) {
        testVorlesung.getList().sort(new StudentComparator());
    }

    private void printMatrikelNumbers(Vorlesung testVorlesung, PrintWriter writer) {

        printVorlesungHeaderInformation(testVorlesung, writer);

        testVorlesung.getList().forEach(s -> {
            writer.write("" + s.getMatrikelNummer());
            writer.write(ENCAPSULATION_STRING);
            writer.write(System.lineSeparator());
        });
    }

    private BufferedReader getReaderFromFile(String fileName) {
        File outputFile = new File(fileName);
        try {
            return new BufferedReader(new FileReader(outputFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("didn't find output file");
    }

    public void leseStudenten(String output_file_name) {

        BufferedReader fileReader = this.getReaderFromFile(output_file_name);
        String nextInputLine = "";

        do {
            try {
                nextInputLine = fileReader.readLine();
                if (nextInputLine == null) break;
                String[] studentInfo = nextInputLine.split(";");
                Student created = new Student(
                        studentInfo[0],
                        studentInfo[1],
                        UNI_ID.valueOf(studentInfo[2]),
                        Integer.parseInt(studentInfo[3]));

                this.immatrikuliere(created);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (nextInputLine != "");
    }

    public void leseVorlesung(String test_file_name) {

        BufferedReader fileReader = getReaderFromFile(test_file_name);
        String[] inputInfo = null;
        String nextLine;
        try {
            inputInfo = fileReader.readLine().split(";");
            Vorlesung newVorlesung = new Vorlesung(inputInfo[0], Integer.parseInt(inputInfo[1]));

            readStudentInfoFromFileAndParseIntoVorlesung(fileReader, inputInfo, newVorlesung);
            this.vorlesungHinzufuegen(newVorlesung);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readStudentInfoFromFileAndParseIntoVorlesung(BufferedReader fileReader,
                                                              String[] inputInfo,
                                                              Vorlesung newVorlesung) {

        String nextLine;
        do {
            try {
                nextLine = fileReader.readLine();
                if (nextLine != null) inputInfo = nextLine.split(";");
                else break;
                addParsedStudentToVorlesung(inputInfo[0], newVorlesung);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (inputInfo != null);
    }

    private void addParsedStudentToVorlesung(String s, Vorlesung newVorlesung) {
        Student studentFromFile = getStudentById(Integer.parseInt(s));
        if (studentFromFile != null) {
            newVorlesung.anmelden(studentFromFile);
        }
    }

    private Student getStudentById(int id) {
        for (Student s : this.getStudentList()) {
            if (s.getMatrikelNummer() == id) return s;
        }
        return null;
    }
}
