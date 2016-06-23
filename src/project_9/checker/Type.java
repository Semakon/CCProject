package project_9.checker;

/**
 * Author:  Martijn
 * Date:    14-6-2016
 */
abstract public class Type {

    private TypeKind kind;

    /** Singleton instance of Integer */
    public static final Type INT = new Int();
    /** Singleton instance of Boolean */
    public static final Type BOOL = new Bool();
    /** Singleton instance of String */
    public static final Type STR = new Str();

    public Type(TypeKind kind) {
        this.kind = kind;
    }

    /** Returns this Type's TypeKind. */
    public TypeKind getKind() {
        return this.kind;
    }

    /** Returns this Type's size in bytes. */
    abstract public int getSize();

    /** Checks whether a given object is the same type as this Type. */
    abstract public boolean sameType(Object obj);

    /** Representation of the Boolean type. */
    static public class Bool extends Type {

        private Bool() {
            super(TypeKind.BOOL);
        }

        @Override
        public int getSize() {
            return getKind().getSize();
        }

        @Override
        public boolean sameType(Object obj) {
            return this == obj || obj instanceof Bool;
        }

        @Override
        public String toString() {
            return "Boolean";
        }

    }

    /** Representation of the Integer type. */
    static public class Int extends Type {

        private Int() {
            super(TypeKind.INT);
        }

        @Override
        public int getSize() {
            return getKind().getSize();
        }

        @Override
        public boolean sameType(Object obj) {
            return this == obj || obj instanceof Int;
        }

        @Override
        public String toString() {
            return "Integer";
        }

    }

    /** Representation of the String type. */
    static public class Str extends Type {

        private Str() {
            super(TypeKind.STR);
        }

        @Override
        public int getSize() {
            return getKind().getSize();
        }

        @Override
        public boolean sameType(Object obj) {
            return this == obj || obj instanceof Str;
        }

        @Override
        public String toString() {
            return "String";
        }

    }

}
