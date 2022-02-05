package br.ufes.edu.compiladores.typing;

// Enumeração dos tipos nativos que podem existir em GO.
public enum Type {
    // Set of Boolean truth values denoted by the predeclared constants true and
    // false
    BOOL_TYPE("bool"),
    // either 32 or 64 bits
    UINT_TYPE("uint"),
    // an unsigned integer large enough to store the uninterpreted bits of a pointer
    // value
    UINTPTR_TYPE("uintptr"),
    // the set of all unsigned 8-bit integers (0 to 255)
    UINT8_TYPE("uint8"),
    // the set of all unsigned 16-bit integers (0 to 65535)
    UINT16_TYPE("uint16"),
    // the set of all unsigned 32-bit integers (0 to 4294967295)
    UINT32_TYPE("uint32"),
    // the set of all unsigned 64-bit integers (0 to 18446744073709551615)
    UINT64_TYPE("uint64"),

    // same size as uint
    INT_TYPE("int"),
    // the set of all signed 8-bit integers (-128 to 127)
    INT8_TYPE("int8"),
    // the set of all signed 16-bit integers (-32768 to 32767)
    INT16_TYPE("int16"),
    // the set of all signed 32-bit integers (-2147483648 to 2147483647)
    INT32_TYPE("int32"),
    // the set of all signed 64-bit integers (-9223372036854775808 to
    // 9223372036854775807)
    INT64_TYPE("int64"),

    // the set of all IEEE-754 32-bit floating-point numbers
    FLOAT32_TYPE("float32"),
    // the set of all IEEE-754 64-bit floating-point numbers
    FLOAT64_TYPE("float64"),

    // the set of all complex numbers with float32 real and imaginary parts
    COMPLEX64_TYPE("complex64"),
    // the set of all complex numbers with float64 real and imaginary parts
    COMPLEX128_TYPE("complex128"),

    // alias for uint8
    BYTE_TYPE("byte"),
    // alias for int32
    RUNE_TYPE("rune"),

    // Set of string values. A string value is a (possibly empty) sequence of bytes.
    STR_TYPE("string"),

    // A struct is a sequence of named elements, called fields, each of which has a name and a type
    STRUCT_TYPE("struct"),

    ARRAY_TYPE("array"),

    MAP_TYPE("map"),

    NO_TYPE(null);

    private String name;

    private Type(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
