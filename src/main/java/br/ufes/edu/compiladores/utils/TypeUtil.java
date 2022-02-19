package br.ufes.edu.compiladores.utils;

import java.util.HashMap;
import java.util.Map;
import br.ufes.edu.compiladores.typing.Type;

/**
 * Utilitário para unificação de tipos primários
 */
public class TypeUtil {

    private static final Map<String, Type> types = new HashMap<>();
    static {
        Type[] typeEnum = Type.values();
        for (Type type : typeEnum) {
            types.put(type.toString(), type);
        }
    }

    private TypeUtil() {
        // hiding constructor
    }

    public static Type getTypeByIdentifier(String identifier) {
        switch (identifier) {
            case "float32":
                identifier = "float";
                break;
            case "float64":
                identifier = "float";
                break;
            case "int32":
                identifier = "int";
                break;
            case "int64":
                identifier = "int";
                break;
            default:
                identifier = "no_type";
                break;
        }
        return TypeUtil.types.get(identifier);
    }
}
