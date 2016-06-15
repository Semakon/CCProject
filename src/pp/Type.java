package pp;

/**
 * Created by martijn on 14-6-16.
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

    public TypeKind getKind() {
        return this.kind;
    }

    abstract public int getSize();

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
        public String toString() {
            return "String";
        }

    }

    /** Representation of the Array type. */
    static public class Array extends Type {
        private final int lower;
        private final int upper;
        private final Type elemType;

        private Array(int lower, int upper, Type elemType) {
            super(TypeKind.ARRAY);
            assert upper >= lower;
            this.lower = lower;
            this.upper = upper;
            this.elemType = elemType;
        }

        /** Returns the lower bound of this array type. */
        public int getLower() {
            return this.lower;
        }

        /** Returns the upper bound of this array type. */
        public int getUpper() {
            return this.upper;
        }

        /** Returns the element bound of this array type. */
        public Type getElemType() {
            return this.elemType;
        }

        @Override
        public int getSize() {
            return (getUpper() - getLower() + 1) * this.elemType.getSize();
        }

        @Override
        public String toString() {
            return "Array [" + this.lower + ".." + this.upper + "] of "
                    + this.elemType;
        }

        @Override
        public int hashCode() { //TODO: necessary?
            final int prime = 31;
            int result = 1;
            result = prime * result + this.elemType.hashCode();
            result = prime * result + this.lower;
            result = prime * result + this.upper;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Array)) {
                return false;
            }
            Array other = (Array) obj;
            if (!this.elemType.equals(other.elemType)) return false;
            return this.lower == other.lower && this.upper == other.upper;
        }
    }

}
