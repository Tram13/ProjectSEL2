package be.sel2.api.repositories;

/**
 * Custom repository interface to allow for the refresh method
 */
public interface RefreshRepository<T> {
    void refresh(T entity);
}
