package br.ufes.edu.compiladores.tables;

import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe para registro da tabela de strings para uso posterior
 */
public final class StrTable {
    private Map<String, String> table = new HashMap<>();

    public String add(String s) {

        return this.table.computeIfAbsent(s, k -> s);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Formatter f = new Formatter(sb);
        f.format("Strings table:%n");

        table.forEach((k, v) -> f.format("Entry %s%n", v));

        f.close();
        return sb.toString();
    }

}
