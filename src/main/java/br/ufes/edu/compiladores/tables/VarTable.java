package br.ufes.edu.compiladores.tables;

import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

import br.ufes.edu.compiladores.typing.Type;
import lombok.Getter;

/**
 * Tabela para registro da tabela de símbolos do programa
 */
public final class VarTable {

    private Map<String, Entry> table = new HashMap<>();

    public Entry lookupVar(String s) {
        return this.table.get(s);
    }

    public Entry addVar(String s, int line, Type type) {
        Entry value = new Entry(s, line, type);
        this.table.put(s, value);
        return value;
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

    @Getter
    public static final class Entry {
        private final String name;
        private final int line;
        private final Type type;

        @Override
        public String toString() {
            return this.name;
        }

        private Entry(String name, int line, Type type) {
            this.name = name;
            this.line = line;
            this.type = type;
        }
    }
}
