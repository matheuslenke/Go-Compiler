package br.ufes.edu.compiladores.tables;

import java.util.Formatter;
import java.util.HashSet;

/**
 * Classe para registro da tabela de strings para uso posterior
 */
public final class StrTable {
    private HashSet<String> table = new HashSet<>();

    public boolean add(String s) {
        return table.add(s);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Formatter f = new Formatter(sb);
        f.format("Strings table:%n");

        table.forEach(v -> f.format("Entry %s%n", v));

        f.close();
        return sb.toString();
    }

}
