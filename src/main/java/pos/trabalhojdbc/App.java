package pos.trabalhojdbc;


import pos.trabalhojdbc.connection.ConnectionFactory;
import pos.trabalhojdbc.connection.DatabaseType;
import pos.trabalhojdbc.dao.DaoGeneric;
import pos.trabalhojdbc.domain.Pessoa;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class App {
    public static void main( String[] args ) throws IllegalAccessException {

        DaoGeneric<Pessoa> pessoaDaoGeneric = new DaoGeneric<Pessoa>(Pessoa.class);
//        String createTablePessoa = pessoaDaoGeneric.generateArchetype();

        Pessoa byID = pessoaDaoGeneric.getByID("bbbb7bef-fc54-42e2-9da0-309ef13cbd63");
        System.out.println(byID);
//        List<Pessoa> all = pessoaDaoGeneric.getAll();
//        System.out.println(all.size());
        Pessoa teste = new Pessoa("teste");
        pessoaDaoGeneric.novo(teste);

//        Pessoa felipe = new Pessoa("Felipe");
//        pessoaDaoGeneric.novo(felipe);
//        felipe.setAltura(7.00);
//        pessoaDaoGeneric.update(felipe);
//        felipe.setIdade(24);
//        felipe.setAltura(1.74);
//        String updatePessoa = pessoaDaoGeneric.update(felipe);
//        String deletePessoa = pessoaDaoGeneric.delete(felipe);
//        System.out.println(felipe);
    }
}
