package project_9.checker;

/**
 * Author:  Martijn
 * Date:    15-6-2016
 */
public enum TypeKind {

    /** Boolean kind */
    BOOL,

    /** Integer kind */
    INT,

    /** String kind */
    STR;

    /** Returns the TypeKind's size in bytes. */
    public int getSize() {
        int size;
        if (this == BOOL) {
            size = 4;
        } else if (this == INT) {
            size = 4;
        } else { // this == STR
            size = 4; // TODO: replace placeholders with actual values;
        }
        return size;
    }

}
