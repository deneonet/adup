package net.deneo.adup.database;

public interface DeleteFunc<T> {
    boolean delete(T val);
}
