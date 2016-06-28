package project_9.checker;

/**
 * Author:  martijn
 * Date:    28-6-16.
 */
public class Reg {

    /** This register's unique identifier. */
    private String id;

    public Reg(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Reg)) {
            return false;
        }
        return (this.id.equals(((Reg) obj).getId()));
    }

}
