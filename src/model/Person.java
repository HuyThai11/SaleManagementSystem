
package model;


public class Person {
    private String id;
    private String name;

    public Person(String id, String name) {
        setId(id);
        setName(name);
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be empty.");
        }
        this.id = id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        this.name = name.trim();
    }

    public void displayInfo() {
        System.out.println(id + " - " + name);
    }
}

