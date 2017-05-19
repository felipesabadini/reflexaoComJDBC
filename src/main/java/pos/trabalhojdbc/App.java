package pos.trabalhojdbc;


import pos.trabalhojdbc.connection.ConnectionFactory;
import pos.trabalhojdbc.connection.DatabaseType;
import pos.trabalhojdbc.dao.DaoGeneric;
import pos.trabalhojdbc.domain.Pessoa;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public class App {
    public static void main( String[] args ) throws IllegalAccessException {

        DaoGeneric<Pessoa> pessoaDaoGeneric = new DaoGeneric<Pessoa>(Pessoa.class);
        String createTablePessoa = pessoaDaoGeneric.generateArchetype();
        Pessoa felipe = new Pessoa("Felipe");
        String insertPessoa = pessoaDaoGeneric.save(felipe);
        felipe.setIdade(24);
        felipe.setAltura(1.74);
        String updatePessoa = pessoaDaoGeneric.update(felipe);
        String deletePessoa = pessoaDaoGeneric.delete(felipe);
        System.out.println(felipe);
//        Connection connection = null;
//        Statement statement = null;
//        try {
//            connection = ConnectionFactory.getInstance(DatabaseType.MYSQL, "root", "senha", "posjava");
//            statement = connection.createStatement();
//            statement.execute("create table teste(id int PRIMARY KEY, nome VARCHAR(256))");
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if(statement != null) {
//                    statement.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//            try {
//                if(connection != null) {
//                    connection.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }


    }
}
