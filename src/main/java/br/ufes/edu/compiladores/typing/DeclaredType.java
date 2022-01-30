package br.ufes.edu.compiladores.typing;

import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DeclaredType implements IType {
    private String identifier;

    public DeclaredType(String identifier){
        this.identifier = identifier;
    }

    @Override
    public String getIdentifier() {
        return this.identifier;
    }

}
