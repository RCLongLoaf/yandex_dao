package dao;

import java.util.List;

public interface ReflectionJdbcDao<T> {
    public void insert(T Tobject);

    public void update(T Tobject);

    public void deleteByKey(T Tkey);

    public T selectByKey(T Tkey);

    public List<T> selectAll();
}
