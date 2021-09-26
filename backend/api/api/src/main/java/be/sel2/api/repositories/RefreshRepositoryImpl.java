package be.sel2.api.repositories;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * A default implementation of {@link RefreshRepository} which is required to
 * create a bean of each implementing repository
 */
public class RefreshRepositoryImpl<T> implements RefreshRepository<T> {
    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void refresh(T entity) {
        em.refresh(entity);
    }
}
