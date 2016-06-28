package project_9.checker;

import project_9.Utils;

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
            return Utils.BOOL_SIZE;
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
            return Utils.INT_SIZE;
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

}
