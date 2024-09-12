import java.io.Serializable;

public class Customer implements Serializable {
    public static final int STARTING_ID = 100;
    private static int next_id = STARTING_ID;
    private final int id;
    private String name;
    private String ph_number;
    private int age;
    private String skill_level;

    public Customer(String name, String ph_number, int age, String skill_level) {
        this.id = next_id++;
        this.name = name;
        this.ph_number = ph_number;
        this.age = age;
        this.skill_level = skill_level;
    }

    public int getId() {
        return id;
    }

    public String getName(){
        return name;
    }
    public String getPhNumber(){
        return ph_number;
    }

    public int getAge(){
        return age;
    }

    public String getSkillLevel(){
        return skill_level;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhNumber(String ph_number) {
        this.ph_number = ph_number;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setSkillLevel(String skill_level) {
        this.skill_level = skill_level;
    }

    public static void setNextID(int ID){
        next_id = ID + 1;
    }

    // The only use for toString is for Jlists, so we truncate this by only returning the most useful info.
    //
    public String toString() {
        return id + " : " + name;
    }
}
