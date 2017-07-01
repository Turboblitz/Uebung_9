public class Student {

    private String firstName;
    private String lastName;
    private UNI_ID university;
    private int matrikelNummer;

    Student() {

    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj.getClass() == Student.class)) return false;
        Student compareStudent = (Student) obj;

        if (!compareStudent.getFirstName().equals(firstName)) return false;
        if (!(compareStudent.getMatrikelNummer() == matrikelNummer)) return false;
        if (!compareStudent.getUniversity().equals(university)) return false;
        return compareStudent.getLastName().equals(lastName);
    }

    public Student(String firstName, String lastName, UNI_ID university, int matrikelNummer) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.university = university;
        this.matrikelNummer = matrikelNummer;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public UNI_ID getUniversity() {
        return university;
    }

    public int getMatrikelNummer() {
        return matrikelNummer;
    }
}
