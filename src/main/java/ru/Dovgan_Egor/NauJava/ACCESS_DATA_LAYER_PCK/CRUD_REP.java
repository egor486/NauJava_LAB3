package ru.Dovgan_Egor.NauJava.ACCESS_DATA_LAYER_PCK;

public interface CRUD_REP<T, ID> {
    void create (T Task);
    T read(ID id);
    void update(T Task);
    void delete(ID id);

}
