package pos.trabalhojdbc.dao;

import pos.trabalhojdbc.annotation.IdJdbc;
import pos.trabalhojdbc.domain.Pessoa;

import java.lang.reflect.Field;

public class DaoGeneric<T extends Object> {
    private Class<T> entityClass;

    public DaoGeneric (Class<T> entityClass) {
        this.entityClass = entityClass;
    }


    public String generateArchetype() {
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
                        stringBuilder.append(field.getName() + " decimal(10,2),");
                        break;
                }
            }

        }
        String commandSql = stringBuilder.toString().substring(0, stringBuilder.toString().length()-1) + ")";
        System.out.println(commandSql);

        return commandSql;
    }

    public String save(T entity) {
        StringBuilder columns = new StringBuilder();
        columns.append("insert into " + entity.getClass().getSimpleName() + " (");

        StringBuilder values = new StringBuilder();
        values.append(" values (");

        for(Field field : entity.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(Boolean.TRUE);
                columns.append(field.getName() + ",");

                Class<?> type = field.getType();

                getValueField(entity, values, field, type);

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        String commandSql = columns.toString().substring(0, columns.toString().length()-1) + ")" +
                            values.toString().substring(0, values.toString().length()-1) + ")";
        System.out.println(commandSql);
        return commandSql;
    }

    public String update(T entity) {
        StringBuilder update = new StringBuilder();
        StringBuilder where = new StringBuilder();
        update.append("update " + entity.getClass().getSimpleName() + " set ");
        for(Field field : entity.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(Boolean.TRUE);
                update.append(field.getName() + "=");

                Class<?> type = field.getType();

                whereId(entity, where, field, type);

                getValueField(entity, update, field, type);

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        String commandSql = update.toString().substring(0, update.toString().length()-1) +
                            where.toString().substring(0, where.toString().length()-1);
        System.out.println(commandSql);

        return commandSql;
    }

    private void getValueField(T entity, StringBuilder update, Field field, Class<?> type) throws IllegalAccessException {
        switch (type.getSimpleName()) {
            case "Integer":
                update.append(field.get(entity) + ",");
                break;
            case "String":
            case "Double":
                Object value = field.get(entity);
                if(value != null) {
                    update.append("'"+ value + "',");
                } else {
                    update.append(value+",");
                }
                break;
        }
    }

    public String delete(T entity) {
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

        return commandSql;
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
}
