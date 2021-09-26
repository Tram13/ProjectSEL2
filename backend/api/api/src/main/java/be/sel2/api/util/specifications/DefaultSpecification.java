package be.sel2.api.util.specifications;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A specification used to query the database
 *
 * Expects criteria Value - Value
 *
 * The specification returns all values for which both values are an exact match.
 */
public class DefaultSpecification<T> implements Specification<T> {

    protected final transient List<SearchCriteria> list;

    /**
     * Creates a new {@link DefaultSpecification} to search repositories
     */
    public DefaultSpecification() {
        this.list = new ArrayList<>();
    }


    /**
     * Adds a given criterion to the specifications
     *
     * @param criteria the criterion that needs to be added
     */
    public void add(SearchCriteria criteria) {
        list.add(criteria);
    }

    public void addAll(Collection<SearchCriteria> searchCriteria) {
        list.addAll(searchCriteria);
    }

    public <X> void addWithKey(Map<String, X> map, String key) {
        addWithKey(map, key, key);
    }

    public <X> void addWithKey(Map<String, X> map, String key, String fieldName) {
        if (map.containsKey(key)) {
            list.add(new SearchCriteria(fieldName, map.get(key)));
        }
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        //create a new predicate list
        List<Predicate> predicates = new ArrayList<>();

        //add add criteria to predicates
        for (SearchCriteria criteria : list) {
            if (criteria.getValue() != null) {
                predicates.add(builder.equal(
                        root.get(criteria.getKey()), criteria.getValue()));
            } else {
                predicates.add(builder.isNull(
                        root.get(criteria.getKey())));
            }
        }

        return builder.and(predicates.toArray(new Predicate[0]));
    }

    public List<SearchCriteria> getList() {
        return list;
    }
}
