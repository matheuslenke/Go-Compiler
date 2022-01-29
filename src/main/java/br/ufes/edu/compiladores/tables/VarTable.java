package br.ufes.edu.compiladores.tables;

import java.util.Formatter;
import java.util.HashMap;
import br.ufes.edu.compiladores.typing.PrimitiveType;

/**
 * Tabela para registro da tabela de símbolos do programa
 */
public final class VarTable {

    private HashMap<String, Entry> table = new HashMap<>();

    public boolean lookupVar(String s) {
        return this.table.containsKey(s);
    }

    public void addVar(String s, int line, PrimitiveType type) {
        this.table.put(s, new Entry(s, line, type));
    }

    public String getName(String key) {
        return this.table.get(key).name;
    }

    public int getLine(String key) {
        return this.table.get(key).line;
    }

    public PrimitiveType getType(String key) {
        return this.table.get(key).type;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Formatter f = new Formatter(sb);
        f.format("Variables table:%n");
        this.table.forEach((String key, Entry value) -> f.format("Entry %s -- name: %s, line: %d, type: %s%n", key,
                value.name, value.line, value.type.toString()));
        f.close();
        return sb.toString();
    }

    private final class Entry {
        private final String name;
        private final int line;
        private final PrimitiveType type;

        private Entry(String name, int line, PrimitiveType type) {
            this.name = name;
            this.line = line;
            this.type = type;
        }
    }
}
