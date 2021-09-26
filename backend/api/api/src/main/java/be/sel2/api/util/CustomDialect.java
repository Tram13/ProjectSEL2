package be.sel2.api.util;

import org.hibernate.dialect.PostgreSQL92Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class CustomDialect extends PostgreSQL92Dialect {
    public CustomDialect() {
        super();
        registerFunction("similarity",
                new StandardSQLFunction("similarity", StandardBasicTypes.FLOAT));
    }
}
