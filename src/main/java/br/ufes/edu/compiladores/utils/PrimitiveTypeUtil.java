package br.ufes.edu.compiladores.utils;

import java.util.EnumMap;
import java.util.Map;

import br.ufes.edu.compiladores.typing.PrimitiveType;

/**
 * Utilitário para unificação de tipos primários
 */
public class PrimitiveTypeUtil {
    // Tabela de unificação de tipos primitivos para os
    // operador de soma '+'.
    private static final Map<PrimitiveType, Map<PrimitiveType, PrimitiveType>> plusOp = new EnumMap<>(PrimitiveType.class);

    // Tabela de unificação de tipos primitivos para os
    // demais operadores aritméticos.
    private static final Map<PrimitiveType, Map<PrimitiveType, PrimitiveType>> otherArithmeticOp = new EnumMap<>(PrimitiveType.class);

    // Tabela de unificação de tipos primitivos para os
    // operadores de comparação.
    private static final Map<PrimitiveType, Map<PrimitiveType, PrimitiveType>> compOp = new EnumMap<>(PrimitiveType.class);

    static {
        
    }

    private PrimitiveTypeUtil() {
        // hiding constructor
    }

    public static PrimitiveType unifyPlus(PrimitiveType t1, PrimitiveType t2) {
        return PrimitiveTypeUtil.plusOp.get(t1).get(t2);
    }

    public static PrimitiveType unifyOtherArith(PrimitiveType t1, PrimitiveType t2) {
        return PrimitiveTypeUtil.otherArithmeticOp.get(t1).get(t2);
    }

    public static PrimitiveType unifyComp(PrimitiveType t1, PrimitiveType t2) {
        return PrimitiveTypeUtil.compOp.get(t1).get(t2);
    }
}
