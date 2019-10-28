package ru.kupryanov.server.dao;

import com.googlecode.objectify.Key;

import java.util.ArrayList;

import static ru.kupryanov.server.dao.objectify.OfyService.ofy;

public abstract class BaseDAO<T> {
    private final Class<T> clazz;

    BaseDAO(final Class<T> clazz) {
        this.clazz = clazz;
    }

    void save(T entity){
        ofy().save().entity(entity).now();
    }

    public ArrayList<T> getAll() {
        return new ArrayList<T>(ofy().load().type(clazz).list());
    }

    public T get(Long id) {
        return ofy().load().type(clazz).id(id).now();
    }

    private T get(Key<T> key) {
        return ofy().load().key(key).now();
    }

    private Key<T> saveNow(T entity) {
        return ofy().save().entity(entity).now();
    }

    public T saveAndReturn(T entity) {
        return get(saveNow(entity));
    }

    public void deleteById(long id){
        ofy().delete().entity(get(id));
    }

}
