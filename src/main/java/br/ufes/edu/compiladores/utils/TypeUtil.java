package br.ufes.edu.compiladores.utils;

import java.util.HashMap;
import java.util.Map;
import br.ufes.edu.compiladores.typing.IType;
import br.ufes.edu.compiladores.typing.PrimitiveType;

/**
 * Utilitário para unificação de tipos primários
 */
public class TypeUtil {

    private static final Map<String, IType> types = new HashMap<>();
    static {
        PrimitiveType[] typeEnum = PrimitiveType.values();
        for (PrimitiveType primitiveType : typeEnum) {
            types.put(primitiveType.toString(), primitiveType);
        }
    }

    private TypeUtil() {
        // hiding constructor
    }

    public static IType getTypeByIdentifier(String identifier){
        return TypeUtil.types.get(identifier);
    }
}
