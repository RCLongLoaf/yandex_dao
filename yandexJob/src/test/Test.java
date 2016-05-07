package test;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.List;
import dao.*;
/**
 * Created by user on 06.05.2016.
 */
public class Test {

    public static void main(String[] args) throws SQLException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        MySqlDaoFactory mySql = new MySqlDaoFactory();
        Connection connection = mySql.getConnection();
        GenericDao<TestClass> testDao = new GenericDao<>(connection, TestClass.class);
        List<TestClass> list;

        TestClass object1 = new TestClass();
        object1.setKey(1);
        object1.setName("name1");
        object1.setTheBoolean(true);

        TestClass object2 = new TestClass();
        object2.setKey(2);
        object2.setName("name2");
        object2.setTheBoolean(false);

        System.out.println("test insert(objcect1) and insert(object2)");
        testDao.insert(object1);
        testDao.insert(object2);
        list = testDao.selectAll();
        for (TestClass aList : list) {
            System.out.println(aList.getKey() + " " + aList.getName() + " " + aList.isTheBoolean());
        }
        System.out.println();

        object1.setName("changed name1");
        System.out.println("test update(object1)");
        testDao.update(object1);
        list = testDao.selectAll();
        for (TestClass aList : list) {
            System.out.println(aList.getKey() + " " + aList.getName() + " " + aList.isTheBoolean());
        }
        System.out.println();

        TestClass selectObject = new TestClass();
        selectObject.setKey(1);
        System.out.println("test selectByKey(selectObject)");
        selectObject = testDao.selectByKey(selectObject);
        System.out.println(selectObject.getKey() + " " + selectObject.getName() + " " + selectObject.isTheBoolean());
        System.out.println();

        System.out.println("test delete(object1) and selectAll");
        testDao.deleteByKey(object1);
        list = testDao.selectAll();
        for (TestClass aList : list) {
            System.out.println(aList.getKey() + " " + aList.getName() + " " + aList.isTheBoolean());
        }

        connection.close();
    }
}


