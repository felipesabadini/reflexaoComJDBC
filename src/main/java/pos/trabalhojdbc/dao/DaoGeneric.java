package pos.trabalhojdbc.dao;

import pos.trabalhojdbc.annotation.IdJdbc;
import pos.trabalhojdbc.connection.ConnectionFactory;
import pos.trabalhojdbc.connection.DatabaseType;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Stream;

public class DaoGeneric<T extends Object> {

    private Class<T> entityClass;
    private Connection connection;

    public DaoGeneric (Class<T> entityClass) {
        this.entityClass = entityClass;
        try {
            connection = ConnectionFactory.getInstance(DatabaseType.MYSQL, "root", "senha", "posjava");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        generateArchetype();
    }


    private void generateArchetype() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("create table IF NOT EXISTS " + this.entityClass.getSimpleName() + " (");
        for(Field field :this.entityClass.getDeclaredFields()) {
            field.setAccessible(Boolean.TRUE);

            IdJdbc idJdbc = field.getAnnotation(IdJdbc.class);
            if(idJdbc != null) {
                Class<?> type = field.getType();
                switch (type.getSimpleName()) {
                    case "String":
                        stringBuilder.append(field.getName() + " varchar(255) primary key,");
                        break;
                }
            } else {
                Class<?> type = field.getType();
                switch (type.getSimpleName()) {
                    case "String":
                        stringBuilder.append(field.getName() + " varchar(255),");
                        break;
                    case "Integer":
                        stringBuilder.append(field.getName() + " int,");
                        break;
                    case "Double":
                    case "BigDecimal":
                        stringBuilder.append(field.getName() + " decimal(10,2),");
                        break;
                }
            }

        }
        String commandSql = stringBuilder.toString().substring(0, stringBuilder.toString().length()-1) + ")";
        executeQuery(commandSql);
    }

    public void novo(T entity) {
        StringBuilder columns = new StringBuilder();
        columns.append("insert into " + entity.getClass().getSimpleName() + " (");

        StringBuilder values = new StringBuilder();
        values.append(" values (");

        List<Map<String, String>> createFields = new ArrayList<>();
        for(Field field : entity.getClass().getDeclaredFields()) {
            try {
                Map newField = new HashMap();
                field.setAccessible(Boolean.TRUE);
                columns.append(field.getName() + ",");

                Class<?> type = field.getType();
                generateAddColumn(newField, entity, field, type);
                getValueField(entity, values, field, type);

                createFields.add(newField);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        updateArchetype(entity, createFields);

        String commandSql = columns.toString().substring(0, columns.toString().length()-1) + ")" +
                            values.toString().substring(0, values.toString().length()-1) + ")";
        System.out.println(commandSql);
        executeQuery(commandSql);
    }

    private void updateArchetype(T entity, List<Map<String, String>> createFields) {
        Statement statement = null;
        try {
            statement = this.connection.createStatement();
            ResultSet resultSet = null;
            try {
                resultSet = statement.executeQuery("desc " + entity.getClass().getSimpleName());
                List<String> fields = new ArrayList<>();
                while (resultSet.next()) {
                    fields.add(resultSet.getString("Field"));
                }
                createFields.forEach(item -> {
                    item.forEach((key, value) -> {
                        if(fields.stream().filter(field-> field.equals(key)).count() == 0) {
                            executeQuery(value);
                        }
                    });
                });
            }catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                resultSet.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void generateAddColumn(Map<String, String> newField, T entity, Field field, Class<?> type) {
        switch (type.getSimpleName()) {
            case "Integer":
                newField.put(field.getName(), "alter table " + entity.getClass().getSimpleName() + " add column " + field.getName() + " int");
                break;
            case "String":
                newField.put(field.getName(), "alter table " + entity.getClass().getSimpleName() + " add column " + field.getName() + " varchar(256)");
                break;
            case "Double":
            case "BigDecimal":
                newField.put(field.getName(), "alter table " + entity.getClass().getSimpleName() + " add column " + field.getName() + " decimal(10, 2)");
                break;
        }
    }

    public void update(T entity) {
        StringBuilder update = new StringBuilder();
        StringBuilder where = new StringBuilder();
        update.append("update " + entity.getClass().getSimpleName() + " set ");

        List<Map<String, String>> createFields = new ArrayList<>();
        for(Field field : entity.getClass().getDeclaredFields()) {
            try {
                Map newField = new HashMap();
                field.setAccessible(Boolean.TRUE);
                update.append(field.getName() + "=");

                Class<?> type = field.getType();

                whereId(entity, where, field, type);

                generateAddColumn(newField, entity, field, type);
                getValueField(entity, update, field, type);

                createFields.add(newField);

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        String commandSql = update.toString().substring(0, update.toString().length()-1) +
                            where.toString().substring(0, where.toString().length()-1);
        System.out.println(commandSql);

        executeQuery(commandSql);
    }

    private void getValueField(T entity, StringBuilder update, Field field, Class<?> type) throws IllegalAccessException {
        switch (type.getSimpleName()) {
            case "Integer":
                update.append(field.get(entity) + ",");
                break;
            case "String":
            case "Double":
            case "BigDecimal":
                Object value = field.get(entity);
                if(value != null) {
                    update.append("'"+ value + "',");
                } else {
                    update.append(value+",");
                }
                break;
        }
    }

    public void delete(T entity) {
        StringBuilder delete = new StringBuilder();
        StringBuilder where = new StringBuilder();
        delete.append("delete from " + entity.getClass().getSimpleName());
        for(Field field : entity.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(Boolean.TRUE);

                Class<?> type = field.getType();
                whereId(entity, where, field, type);

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        String commandSql = delete + where.toString().substring(0, where.toString().length()-1);
        System.out.println(commandSql);

        executeQuery(commandSql);
    }

    private void whereId(T entity, StringBuilder where, Field field, Class<?> type) throws IllegalAccessException {
        IdJdbc idJdbc = field.getAnnotation(IdJdbc.class);
        if(idJdbc != null) {
            switch (type.getSimpleName()) {
                case "String":
                    where.append( " where " + field.getName() + "=");
                    getValueField(entity, where, field, type);
                    break;
            }
        }
    }

    private void executeQuery(String query) {
        try (Statement statement = this.connection.createStatement()) {
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ResultSet executeQuery2(String query) {
        try (Statement statement = this.connection.createStatement()) {
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("erro ao executar " + query);
    }

    public T getByID(String id) {
        T entity = null;

        try {
            entity = this.entityClass.newInstance();
            Optional<Field> first = Arrays.stream(entity.getClass().getDeclaredFields()).filter(field -> field.getAnnotation(IdJdbc.class) != null).findFirst();
            StringBuilder where = new StringBuilder();
            Field field1 = first.get();
            where.append(" where " + field1.getName() + " = '" + id +"'");
            String query = "select * from " + this.entityClass.getSimpleName() + where.toString();
            try(Statement statement = this.connection.createStatement()) {
                try(ResultSet resultSet = statement.executeQuery(query)) {
                    while(resultSet.next()) {
                        setDataEntityResultSet(resultSet, entity);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return entity;
    }

    public List<T> getAll() {
        List<T> entitys = new ArrayList<T>();

        try {

            String query = "select * from " + this.entityClass.getSimpleName();
            try(Statement statement = this.connection.createStatement()) {
                try(ResultSet resultSet = statement.executeQuery(query)) {

                    while(resultSet.next()) {
                        T entity = this.entityClass.newInstance();
                        setDataEntityResultSet(resultSet, entity);
                        entitys.add(entity);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return entitys;
    }

    private void setDataEntityResultSet(ResultSet resultSet, T entity) throws SQLException {
        for(Field field : entity.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(Boolean.TRUE);
                try {
                    field.set(entity, resultSet.getObject(field.getName()));
                } catch (Exception ex) {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        connection.close();
        super.finalize();
    }
}
