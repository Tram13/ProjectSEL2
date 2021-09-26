package be.sel2.api.util.specifications;

import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * A specification used to query the database on all string fields
 *
 * Expects criteria String - String
 *
 * The specification returns all values for which
 * + Strings are an approximate match of any of the String fields
 */
public class AllFieldSpecification<T> extends PartialMatchSpecification<T> {

    public void addForClass(Class<?> c, String value) {
        for (Field field : c.getDeclaredFields()) {
            if (field.getType() == String.class && !field.getName().equals("password")) { //Only match strings, never search passwords
                list.add(new SearchCriteria(field.getName(), value));
            }
        }
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        //create a new predicate list
        List<Predicate> predicates = new ArrayList<>();

        List<Expression<Float>> similarities = new ArrayList<>();

        //add add criteria to predicates
        for (SearchCriteria criteria : list) {

            if (criteria.getValue() instanceof String) {
                Expression<Float> similarityFunction =
                        getSimilarityFunction(criteria.getKey(),
                                (String) criteria.getValue(),
                                root, builder);

                predicates.add(
                        getSimilarityOrSubstring(similarityFunction,
                                criteria.getKey(), (String) criteria.getValue(),
                                root, builder)
                );

                similarities.add(similarityFunction);
            }
        }

        addOrderBySimilarity(similarities, builder, query);

        return builder.or(predicates.toArray(new Predicate[0]));
    }
}
