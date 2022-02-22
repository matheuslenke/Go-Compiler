package br.ufes.edu.compiladores.tables;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import br.ufes.edu.compiladores.typing.Type;

/**
 * Tabela para registro da tabela de símbolos do programa
 */
public final class VarTable {

    private List<Entry> table = new ArrayList<>();

    public int lookupVar(String s) {
        for (int i = 0; i < table.size(); i++) {
            if (table.get(i).name.equals(s)) {
                return i;
            }
        }
        return -1;
    }

    public Boolean varExists(Integer i) {
        Entry e = table.get(i);
        if (e == null) {
            return false;
        }
        return true;
    }

    public int addVar(String s, int line, Type type) {
        Entry entry = new Entry(s, line, type);
        int idxAdded = table.size();
        table.add(entry);
        return idxAdded;
    }
    public int addVar(String s, int line, Type type, Type subType) {
        Entry entry = new Entry(s, line, type, subType);
        int idxAdded = table.size();
        table.add(entry);
        return idxAdded;
    }

    public String getName(int i) {
        return table.get(i).name;
    }

    public int getLine(int i) {
        return table.get(i).line;
    }

    public Type getType(int i) {
        return table.get(i).type;
    }

    public Type getSubType(int i) {
        return table.get(i).subType;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Formatter f = new Formatter(sb);
        f.format("Variables table:\n");
        for (int i = 0; i < table.size(); i++) {
            if(getType(i) == Type.ARRAY_TYPE) {
                f.format("Entry %d -- name: %s, line: %d, type: %s, subtype: %s\n", i,
                        getName(i), getLine(i), getType(i).toString(), getSubType(i).toString());
            } else {
                f.format("Entry %d -- name: %s, line: %d, type: %s\n", i,
                        getName(i), getLine(i), getType(i).toString());

            }
        }
        f.close();
        return sb.toString();
    }

    public static final class Entry {
        private final String name;
        private final int line;
        private final Type type;
        private final Type subType;

        Entry(String name, int line, Type type) {
            this.name = name;
            this.line = line;
            this.type = type;
            this.subType = null;
        }

        Entry(String name, int line, Type type, Type subType) {
            this.name = name;
            this.line = line;
            this.type = type;
            this.subType = subType;
        }
    }
}
