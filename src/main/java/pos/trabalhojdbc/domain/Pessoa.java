package pos.trabalhojdbc.domain;

import pos.trabalhojdbc.annotation.EntityJdbc;
import pos.trabalhojdbc.annotation.IdJdbc;

import java.util.UUID;

@EntityJdbc
public class Pessoa {

    @IdJdbc
    private String id;
    private String nome;
    private Integer idade;
    private Double altura;

    public Pessoa(String nome) {
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public void setAltura(Double altura) {
        this.altura = altura;
    }

    @Override
    public String toString() {
        return "Pessoa{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", idade=" + idade +
                ", altura=" + altura +
                '}';
    }
}
