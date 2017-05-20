package pos.trabalhojdbc.domain;

import pos.trabalhojdbc.annotation.EntityJdbc;
import pos.trabalhojdbc.annotation.IdJdbc;

import java.math.BigDecimal;
import java.util.UUID;

@EntityJdbc
public class Pessoa {

    @IdJdbc
    private String id;
    private String nome;
    private Integer idade;
    private BigDecimal altura;
    private String teste;
    private String novoTeste;
    private String novoTeste2;
    private String novoTeste3;
    private String novoTeste4;
    private String getNovoTeste5;

    public Pessoa() {}

    public Pessoa(String nome) {
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public void setAltura(BigDecimal altura) {
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
