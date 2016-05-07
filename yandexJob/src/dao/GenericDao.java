package dao;

import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class GenericDao<T> implements ReflectionJdbcDao<T> {
    private final Connection connection;
    private Class<T> tClass;
    private String tableName = "";

    public GenericDao(Connection connection, Class<T> t) {
        this.connection = connection;
        tClass = t;
        tableName = t.getAnnotation(Table.class).name();
    }

    private static String convertBigger(String input) {
        return String.format(input.replaceAll("\\_(.)", "%S"), input.replaceAll("[^_]*_(.)[^_]*", "$1_").split("_"));
    }

    private static String convertLower(String input) {
        return String.format(input.replaceAll("([A-Z])", "_$1").toLowerCase());
    }

    private String getNameField(Field field) {
        if (field.isAnnotationPresent(ViewName.class)) {
            return field.getAnnotation(ViewName.class).name();
        }
        return convertLower(field.getName());
    }

    private String buildStringWhere(Field[] fields) {
        StringBuilder str = new StringBuilder("");
        for (Field field : fields) {
            if (field.isAnnotationPresent(Key.class)) {
                str.append(getNameField(field) + "=? and ");
            }
        }
        if (str.length() != 0) {
            str = new StringBuilder(" WHERE ").append(str);
            return str.delete(str.length() - 5, str.length()).toString();
        }
        return "";
    }

    private String getPref(Field field) {
        String pref;
        switch (field.getType().getSimpleName()) {
            case "boolean":
            case "Boolean":
                pref = "is";
                break;
            default:
                pref = "get";
        }
        return pref;
    }


    @Override
    public void insert(T tObject) {
        try {
            StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
            PreparedStatement stm;
            Field[] fields = tObject.getClass().getDeclaredFields();

            for (Field field : fields) {
                sql.append(getNameField(field) + ",");
            }
            sql.deleteCharAt(sql.length() - 1).append(") VALUES (");
            for (Field field : fields) {
                sql.append("?,");
            }
            sql.deleteCharAt(sql.length() - 1).append(");");
            stm = connection.prepareStatement(sql.toString());
            for (int i = 0; i < fields.length; i++) {
                stm.setObject(i + 1, tObject.getClass().getMethod(getPref(fields[i]) + convertBigger("_" + fields[i].getName())).invoke(tObject));
            }
            synchronized (connection) {
                stm.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(T tObject){
        try {
            StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
            PreparedStatement stm = null;
            Field[] fields = tObject.getClass().getDeclaredFields();
            for (Field field : fields) {
                sql.append(getNameField(field) + "=?,");
            }
            sql.deleteCharAt(sql.length() - 1);
            sql.append(buildStringWhere(fields));
            sql.append(";");
            stm = connection.prepareStatement(sql.toString());
            for (int i = 0; i < fields.length; i++) {
                stm.setObject(i + 1, tObject.getClass().getMethod(getPref(fields[i]) + convertBigger("_" + fields[i].getName())).invoke(tObject));
            }
            int ind = fields.length;
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].isAnnotationPresent(Key.class)) {
                    stm.setObject(ind + 1, tObject.getClass().getMethod(getPref(fields[i]) + convertBigger("_" + fields[i].getName())).invoke(tObject));
                    ind++;
                }
            }
            synchronized (connection) {
                stm.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteByKey(T tKey){
        try {
            StringBuilder sql = new StringBuilder("DELETE FROM " + tableName);
            Field[] fields = tKey.getClass().getDeclaredFields();
            PreparedStatement stm;

            sql.append(buildStringWhere(fields));
            sql.append(";");
            stm = connection.prepareStatement(sql.toString());
            int ind = 0;
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].isAnnotationPresent(Key.class)) {
                    stm.setObject(ind + 1, tKey.getClass().getMethod(getPref(fields[i]) + convertBigger("_" + fields[i].getName())).invoke(tKey));
                    ind++;
                }
            }
            synchronized (connection) {
                stm.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public T selectByKey(T tKey){
        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM " + tableName);
            Field[] fields = tKey.getClass().getDeclaredFields();
            sql.append(buildStringWhere(fields));
            sql.append(";");
            PreparedStatement stm;
            stm = connection.prepareStatement(sql.toString());
            int ind = 0;
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].isAnnotationPresent(Key.class)) {
                    stm.setObject(ind + 1, tKey.getClass().getMethod(getPref(fields[i]) + convertBigger("_" + fields[i].getName())).invoke(tKey));
                    ind++;
                }
            }

            ResultSet rs;
            synchronized (connection) {
                rs = stm.executeQuery();
            }
            T tObject = null;
            if (rs.next()) {
                tObject = tClass.newInstance();
                for (Field field : fields) {
                    Class type = field.getType();
                    Method method = rs.getClass().getMethod("getObject", String.class);
                    tObject.getClass().getMethod("set" + convertBigger('_' + field.getName()), type)
                            .invoke(tObject, method.invoke(rs, getNameField(field)));
                }
            }
            return tObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<T> selectAll() {
        try {
            List<T> list = new ArrayList<>();
            ResultSet rs;
            synchronized (connection) {
                rs = connection.prepareStatement("SELECT * FROM " + tableName + ";").executeQuery();
            }
            while (rs.next()) {
                T tObject = tClass.newInstance();
                Field[] fields = tObject.getClass().getDeclaredFields();
                for (Field field : fields) {
                    Class type = field.getType();
                    Method method = rs.getClass().getMethod("getObject", String.class);
                    tObject.getClass().getMethod("set" + convertBigger('_' + field.getName()), type)
                            .invoke(tObject, method.invoke(rs, getNameField(field)));
                }
                list.add(tObject);
            }
            return list;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
