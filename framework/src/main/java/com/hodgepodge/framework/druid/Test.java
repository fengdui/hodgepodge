package com.hodgepodge.framework.druid;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import java.util.Iterator;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        String sql = "select a.*, *, b.x as y from table_1 a, table_2 b where a.x = 1 and b.x = 2";
        SQLStatementParser sqlStatementParser = new MySqlStatementParser(sql);
        Mysql2DMMysqlVisitor visitor = new Mysql2DMMysqlVisitor();
        visitor.setQuoteSymbol(true);
        visitor.setUpperLowerCase(0);
        StringBuilder result = new StringBuilder();
        List<SQLStatement> statements = sqlStatementParser.parseStatementList();
       for (SQLStatement statement : statements) {

            statement.accept(visitor);
            result.append(statement.toString()).append("\n");
        }

        System.out.println(result.toString());
    }
}
