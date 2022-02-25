package br.ufes.edu.compiladores.tables;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

/**
 * Classe para registro da tabela de strings para uso posterior
 */
public final class StrTable {
    private List<String> table = new ArrayList<>();

    public Integer add(String s) {
        int idxAdded = table.indexOf(s);
        if (idxAdded == -1) {
            idxAdded = table.size();
            table.add(s);
        }
        return idxAdded;
    }

    public String get(Integer i) {
        return this.table.get(i);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Formatter f = new Formatter(sb);
        f.format("Strings table:%n");
        for (int i = 0; i < table.size(); i++) {
            f.format("Entry %d: %s%n", i, table.get(i));
        }
        f.close();
        return sb.toString();
    }

}
