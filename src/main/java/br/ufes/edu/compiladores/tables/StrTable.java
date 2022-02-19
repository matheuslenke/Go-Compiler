package br.ufes.edu.compiladores.tables;

import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe para registro da tabela de strings para uso posterior
 */
public final class StrTable {
    private Map<Integer, String> table = new HashMap<>();
    private Integer index = 0;

    public Integer add(String s) {
        Integer current = index;
        this.table.putIfAbsent(current, s);
        index += 1;
        return current;
    }

    public String get(Integer i) {
        return this.table.get(i);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Formatter f = new Formatter(sb);
        f.format("Strings table:%n");

        table.forEach((k, v) -> f.format("Entry %d: %s%n", k, v));

        f.close();
        return sb.toString();
    }

}
