package be.sel2.api.util.specifications;

import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A specification used to query the database
 *
 * Expects criteria String - String
 * Expects criteria Date   - Date
 * Expects criteria Value  - Value
 *
 * The specification returns all values for which
 * + Strings are a substring of the field
 * + Dates (Timestamps) fall within the same day
 * + Otherwise values are an exact match
 */
public class PartialMatchSpecification<T> extends DefaultSpecification<T> {

    private static final float MATCH_THRESHOLD = 0.2f;

    protected Expression<Float> getSimilarityFunction(String key, String value, Root<T> root, CriteriaBuilder builder) {
        return builder.function("similarity",
                Float.class,
                root.get(key),
                builder.literal(value));
    }

    protected Predicate getSimilarityOrSubstring(Expression<Float> similarity, String key, String value,
                                                 Root<T> root, CriteriaBuilder builder) {
        value = value.toLowerCase();
        final String metaCharacters = "\\^()+|%";
        String escaped = Arrays.stream(value.split("")).map(c -> {
            if (metaCharacters.contains(c)) return '\\' + c;
            else return c;
        }).collect(Collectors.joining());

        String regex = "%" + escaped + "%";
        return builder.or(
                builder.ge(similarity, MATCH_THRESHOLD),
                builder.like(builder.lower(root.get(key)),
                        regex)
        );
    }

    protected void addOrderBySimilarity(List<Expression<Float>> similarities, CriteriaBuilder builder,
                                        CriteriaQuery<?> query) {
        if (!similarities.isEmpty()) {
            Expression<Float> totalSimilarity = null;
            for (Expression<Float> sim : similarities) {
                if (totalSimilarity == null) {
                    totalSimilarity = sim;
                } else {
                    totalSimilarity = builder.sum(totalSimilarity, sim);
                }
            }
            query.orderBy(builder.desc(totalSimilarity));
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
                // If string: match with similarity
                Expression<Float> similarityFunction = getSimilarityFunction(
                        criteria.getKey(),
                        (String) criteria.getValue(),
                        root, builder);

                predicates.add(
                        getSimilarityOrSubstring(similarityFunction,
                                criteria.getKey(), (String) criteria.getValue(),
                                root, builder)
                );

                similarities.add(
                        similarityFunction
                );

            } else if (criteria.getValue() instanceof Date) {
                // If date, get between start & end of day
                LocalDate localDate = ((Date) criteria.getValue())
                        .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                Date startOfDay = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                Date endOfDay = Date.from(localDate.atTime(23, 59, 59, 999999999)
                        .atZone(ZoneId.systemDefault()).toInstant());

                predicates.add(builder.between(
                        root.get(criteria.getKey()), startOfDay, endOfDay));
            } else {
                // Else, just get equal
                predicates.add(builder.equal(
                        root.get(criteria.getKey()), criteria.getValue()));
            }
        }

        addOrderBySimilarity(similarities, builder, query);

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}
