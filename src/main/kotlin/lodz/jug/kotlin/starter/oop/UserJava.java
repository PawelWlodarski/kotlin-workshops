package lodz.jug.kotlin.starter.oop;

public class UserJava {
    private String name;
    private int age;

    public UserJava(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "UserJava{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
