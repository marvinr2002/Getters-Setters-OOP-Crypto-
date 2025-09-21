import java.io.Serializable;
import java.util.Locale;

public class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    private String label;   // user1, user2, ...
    private String name;
    private int age;
    private String gender;
    private String email;
    private String phone;
    private String address;

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = Math.max(0, age); }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override public String toString() {
        return String.format(Locale.ROOT,
            "%s | Name: %s | Age: %d | Gender: %s | Email: %s | Phone: %s | Address: %s",
            label, name, age, gender, email, phone, address);
    }
}
