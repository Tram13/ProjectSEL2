package be.sel2.api.util.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A specification used to query the database
 *
 * Expects criteria Date - Date
 *
 * The specification returns all values for which the date field
 * occurs before the given date.
 */
public class BeforeDateSpecification<T> extends DefaultSpecification<T> {

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        //create a new predicate list
        List<Predicate> predicates = new ArrayList<>();

        //add add criteria to predicates
        for (SearchCriteria criteria : list) {
            if (criteria.getValue() instanceof Date) {
                LocalDate localDate = ((Date) criteria.getValue())
                        .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                Date endOfDay = Date.from(localDate.atTime(23, 59, 59, 999999999)
                        .atZone(ZoneId.systemDefault()).toInstant());
                predicates.add(builder.lessThanOrEqualTo(
                        root.get(criteria.getKey()), endOfDay));
            }
        }

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}
