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
        for (Type primitiveType : typeEnum) {
            types.put(primitiveType.toString(), primitiveType);
        }
    }

    private TypeUtil() {
        // hiding constructor
    }

    public static Type getTypeByIdentifier(String identifier){
        return TypeUtil.types.get(identifier);
    }
}
