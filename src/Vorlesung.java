import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Vorlesung {

    public static final String STUDENT_NOT_FOUND_MESSAGE = "Student ist nicht angemeldet";
    protected String name;
    protected int id;
    protected List<Student> subcribedStudents;
    protected HashMap<Student, Integer> pointMap;

    public Vorlesung(String name, int id) {
        this.name = name;
        this.id = id;
        this.subcribedStudents = new ArrayList<>();
        this.pointMap = new HashMap<>();
    }

    public void anmelden(Student student){
        if(this.subcribedStudents.contains(student)) return;
        this.subcribedStudents.add(student);
    }

    List<Student> getList(){
        return this.subcribedStudents;
    }

    HashMap<Student, Integer> getPointMap() {
        return pointMap;
    }

    int getNote(Student s) {
        if (!this.getList().contains(s)) throw new StudentNotFoundExeption(STUDENT_NOT_FOUND_MESSAGE);

        return this.pointMap.get(s);
    }

    public void pruefungDurchfuehren() {
        Random random = new Random();
        this.getList().forEach(s -> pointMap.put(s, random.nextInt(6)+1));
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != Vorlesung.class) return false;
        Vorlesung compareVorlesung = (Vorlesung) obj;
        if(!compareVorlesung.getName().equals(name)) return false;
        if(!(compareVorlesung.getId() == id)) return false;
        //if(!compareVorlesung.getList().equals(subcribedStudents)) return false;
        return true;
    }
}
