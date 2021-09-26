package be.sel2.api.testing_utils;

import org.hibernate.dialect.PostgreSQL92Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;

// Creates a mock implementation of the similarity function
public class TestDialect extends PostgreSQL92Dialect {
    public TestDialect() {
        super();
        registerFunction("similarity",
                new SQLFunctionTemplate(StandardBasicTypes.FLOAT, "(CASE WHEN (?1 LIKE CONCAT('%', ?2, '%')) THEN 1 ELSE 0 END)"));
    }
}
