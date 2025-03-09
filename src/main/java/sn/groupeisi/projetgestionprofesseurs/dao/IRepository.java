package sn.groupeisi.projetgestionprofesseurs.dao;

import javafx.collections.ObservableList;

public interface IRepository<T> {
    public void add(T t);
    public void update(T t);
    public void delete(Long id);
    public ObservableList<T> getAll();
    public T get(Long id);
}
