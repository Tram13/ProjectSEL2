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
 * Expects criteria Value - Value
 *
 * The specification returns all values for which at least one criteria succeeds.
 */
public class ContainsSpecification<T> extends DefaultSpecification<T> {

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        //create a new predicate list
        List<Predicate> predicates = new ArrayList<>();

        //add add criteria to predicates
        for (SearchCriteria criteria : list) {
            predicates.add(builder.equal(root.get(criteria.getKey()), criteria.getValue()));
        }

        return builder.or(predicates.toArray(new Predicate[0]));
    }
}
