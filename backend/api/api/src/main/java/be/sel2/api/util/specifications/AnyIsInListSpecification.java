package be.sel2.api.util.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * A specification used to query the database
 *
 * Expects criteria List - List
 *                  Object - List
 *
 * The specification returns all values for which at least one element matches
 * across the two lists.
 * If a non-list is passed, it will look for lists that contain this element.
 */
public class AnyIsInListSpecification<T> extends DefaultSpecification<T> {

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        //create a new predicate list
        List<Predicate> predicates = new ArrayList<>();

        //add add criteria to predicates
        for (SearchCriteria criteria : list) {
            Object values = criteria.getValue();
            if (values instanceof List) {
                List<Predicate> subPredicates = new ArrayList<>();
                for (Object value : (List<?>) values) {
                    subPredicates.add(builder.isMember(value,
                            root.get(criteria.getKey())));
                }
                predicates.add(builder.or(subPredicates.toArray(new Predicate[0])));
            } else {
                predicates.add(builder.isMember(criteria.getValue(),
                        root.get(criteria.getKey())));
            }

        }

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}
