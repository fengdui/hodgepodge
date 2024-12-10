package com.hodgepodge.framework.druid;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLDataTypeRefExpr;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLIndexDefinition;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLReplaceable;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLBooleanExpr;
import com.alibaba.druid.sql.ast.expr.SQLCaseExpr;
import com.alibaba.druid.sql.ast.expr.SQLCastExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntervalExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLValuableExpr;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddColumn;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddIndex;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateIndexStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUnionQueryTableSource;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.springframework.util.StringUtils;

public class Mysql2DMMysqlVisitor extends MySqlASTVisitorAdapter {
    private static boolean quoteSymbol;
    private static int upperLowerCase;
    private static final String LOG_PREFIX = "-----达梦语句适配-----, ";
    private static final String DM_ESCAPE = "\"";
    private static final String MYSQL_ESCAPE = "`";

    public Mysql2DMMysqlVisitor() {
    }

    public void setQuoteSymbol(boolean quoteSymbol) {
        Mysql2DMMysqlVisitor.quoteSymbol = quoteSymbol;
    }

    public void setUpperLowerCase(int upperLowerCase) {
        Mysql2DMMysqlVisitor.upperLowerCase = upperLowerCase;
    }

    private static String replaceName(String str) {
        if (!StringUtils.isEmpty(str)) {
            if (quoteSymbol) {
                if (upperLowerCase == 1) {
                    str = str.toUpperCase();
                } else if (upperLowerCase == 2) {
                    str = str.toLowerCase();
                }

                return str.contains("`") ? str.replace("`", "\"") : "\"" + str + "\"";
            } else {
                return str.contains("`") ? str.replace("`", "\"") : str;
            }
        } else {
            return str;
        }
    }

    private static String resolveForDateFormat(String content) {
        boolean flag = false;
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < content.length(); ++i) {
            char thisChar = content.charAt(i);
            if (thisChar == '%') {
                if (i == 0) {
                    sb.append(thisChar);
                } else {
                    sb.append("\"").append(thisChar);
                }

                flag = true;
            } else {
                if (flag) {
                    if (i == content.length() - 1) {
                        sb.append(thisChar);
                    } else {
                        sb.append(thisChar).append("\"");
                    }
                } else if (i == content.length() - 1) {
                    sb.append(thisChar).append("\"");
                } else {
                    sb.append(thisChar);
                }

                flag = false;
            }
        }

        return sb.toString();
    }

    public boolean visit(SQLSelectItem x) {
        x.getExpr().accept(this);
        x.setAlias(replaceName(x.getAlias()));
        return false;
    }

    public boolean visit(SQLPropertyExpr x) {
        x.getOwner().accept(this);
        x.setName(replaceName(x.getName()));
        return false;
    }

    public boolean visit(SQLIdentifierExpr x) {
        x.setName(replaceName(x.getName()));
        return false;
    }

    public boolean visit(SQLExprTableSource x) {
        x.getExpr().accept(this);
        x.setAlias(replaceName(x.getAlias()));
        return false;
    }

    public boolean visit(SQLSubqueryTableSource x) {
        x.getSelect().accept(this);
        x.setAlias(replaceName(x.getAlias()));
        return false;
    }

    public boolean visit(SQLUnionQueryTableSource x) {
        x.getUnion().accept(this);
        x.setAlias(replaceName(x.getAlias()));
        return false;
    }

    public boolean visit(SQLJoinTableSource x) {
        x.setAlias(replaceName(x.getAlias()));
        return true;
    }

    public boolean visit(SQLAggregateExpr x) {
        String methodName = x.getMethodName();
        String groupConcatMysqlName = "GROUP_CONCAT";
        String groupConcatDmName = "LISTAGG";
        String anyValueMysqlName = "ANY_VALUE";
        String anyValueDmName = "FIRST_VALUE";
        if (groupConcatMysqlName.equalsIgnoreCase(methodName)) {
            x.setMethodName(groupConcatDmName);
            Object left;
            if (x.getArguments().size() > 1) {
                left = (SQLExpr)x.getArguments().get(0);

                for(int i = 1; i < x.getArguments().size(); ++i) {
                    SQLBinaryOpExpr binaryOpExpr = new SQLBinaryOpExpr();
                    binaryOpExpr.setLeft((SQLExpr)left);
                    binaryOpExpr.setRight((SQLExpr)x.getArguments().get(i));
                    binaryOpExpr.setOperator(SQLBinaryOperator.Concat);
                    left = binaryOpExpr;
                }

                ((SQLExpr)left).setParent(x);
                x.getArguments().clear();
                x.addArgument((SQLExpr)left);
            }

            left = x.getAttributes().get("SEPARATOR");
            if (left instanceof SQLExpr) {
                x.addArgument((SQLExpr)left);
            } else {
                x.addArgument(new SQLCharExpr(","));
            }

            if (x.getOrderBy() != null) {
                x.setWithinGroup(true);
            }

            if (x.getAttributes() != null && x.getAttributes().size() != 0) {
                x.getAttributes().clear();
            }
        } else if (anyValueMysqlName.equalsIgnoreCase(methodName)) {
            x.setMethodName(anyValueDmName);
        }

        return true;
    }

    public boolean visit(SQLMethodInvokeExpr x) {
        String methodName = x.getMethodName();
        methodName = methodName.toLowerCase();
        SQLObject parent = x.getParent();
        List<SQLExpr> arguments = x.getArguments();
        SQLExpr sqlExpr1;
        SQLMethodInvokeExpr sqlMethodInvokeExpr;
        SQLReplaceable sqlReplaceable;
        SQLExpr sqlExpr0;
        switch (methodName) {
            case "if":
                if (parent instanceof SQLReplaceable) {
                    sqlReplaceable = (SQLReplaceable)parent;
                    sqlExpr0 = (SQLExpr)arguments.get(0);
                    sqlExpr1 = (SQLExpr)arguments.get(1);
                    SQLExpr sqlExpr2 = (SQLExpr)arguments.get(2);
                    SQLCaseExpr sqlCaseExpr = new SQLCaseExpr();
                    sqlCaseExpr.addItem(new SQLCaseExpr.Item(sqlExpr0, sqlExpr1));
                    sqlCaseExpr.setElseExpr(sqlExpr2);
                    sqlReplaceable.replace(x, sqlCaseExpr);
                    sqlExpr0.accept(this);
                    sqlExpr1.accept(this);
                    sqlExpr2.accept(this);
                    return false;
                }
                break;
            case "convert":
                if (parent instanceof SQLReplaceable) {
                    sqlReplaceable = (SQLReplaceable)parent;
                    sqlExpr0 = (SQLExpr)arguments.get(0);
                    sqlExpr1 = (SQLExpr)arguments.get(1);
                    if (sqlExpr1 instanceof SQLDataTypeRefExpr) {
                        SQLCastExpr sqlCastExpr = new SQLCastExpr(sqlExpr0, ((SQLDataTypeRefExpr)sqlExpr1).getDataType());
                        sqlReplaceable.replace(x, sqlCastExpr);
                        sqlExpr0.accept(this);
                        sqlExpr1.accept(this);
                        return false;
                    }
                }
                break;
            case "date_format":
                SQLExpr sqlExpr = (SQLExpr)arguments.get(1);
                SQLCharExpr sqlCharExpr = (SQLCharExpr)sqlExpr;
                sqlCharExpr.setText(resolveForDateFormat(sqlCharExpr.getText()));
                return true;
            case "json_unquote":
                sqlExpr1 = (SQLExpr)arguments.get(0);
                x.setArgument(0, new SQLCharExpr("\""));
                x.setMethodName("TRIM");
                x.setFrom(sqlExpr1);
                sqlExpr1.accept(this);
                return false;
            case "st_contains":
                x.setMethodName("dmgeo.st_contains");
                return true;
            case "st_distance_sphere":
                x.setMethodName("dmgeo.ST_Distance");
                return true;
            case "point":
                if (parent instanceof SQLReplaceable) {
                    sqlMethodInvokeExpr = new SQLMethodInvokeExpr("dmgeo.ST_GeomFromText");
                    SQLMethodInvokeExpr concatMethod = new SQLMethodInvokeExpr("CONCAT");
                    concatMethod.addArgument(new SQLCharExpr("POINT("));
                    concatMethod.addArgument((SQLExpr)arguments.get(0));
                    concatMethod.addArgument(new SQLCharExpr(String.valueOf(' ')));
                    concatMethod.addArgument((SQLExpr)arguments.get(1));
                    concatMethod.addArgument(new SQLCharExpr(")"));
                    sqlMethodInvokeExpr.addArgument(concatMethod);
                    sqlMethodInvokeExpr.addArgument(new SQLNumberExpr(0));
                    ((SQLReplaceable)parent).replace(x, sqlMethodInvokeExpr);
                    Iterator var20 = arguments.iterator();

                    while(var20.hasNext()) {
                        SQLExpr argument = (SQLExpr)var20.next();
                        argument.accept(this);
                    }

                    return false;
                }
                break;
            case "geometryfromtext":
                if (parent instanceof SQLReplaceable) {
                    sqlMethodInvokeExpr = new SQLMethodInvokeExpr("dmgeo.ST_GeomFromText");
                    sqlMethodInvokeExpr.addArgument((SQLExpr)arguments.get(0));
                    sqlMethodInvokeExpr.addArgument(new SQLNumberExpr(0));
                    ((SQLReplaceable)parent).replace(x, sqlMethodInvokeExpr);
                    Iterator var11 = sqlMethodInvokeExpr.getArguments().iterator();

                    while(var11.hasNext()) {
                        SQLExpr argument = (SQLExpr)var11.next();
                        argument.accept(this);
                    }

                    return false;
                }
        }

        return true;
    }

    public boolean visit(SQLBinaryOpExpr x) {
        if (x.getRight() instanceof SQLBooleanExpr) {
            boolean booleanValue = ((SQLBooleanExpr)x.getRight()).getBooleanValue();
            x.setRight(new SQLNumberExpr(booleanValue ? 1 : 0));
        }

        SQLObject parent = x.getParent();
        SQLMethodInvokeExpr sqlMethodInvokeExpr;
        SQLExpr left;
        SQLExpr right;
        if (x.getOperator() == SQLBinaryOperator.SubGt) {
            if (parent instanceof SQLReplaceable) {
                sqlMethodInvokeExpr = new SQLMethodInvokeExpr("JSON_EXTRACT");
                left = x.getLeft();
                right = x.getRight();
                sqlMethodInvokeExpr.addArgument(left);
                sqlMethodInvokeExpr.addArgument(right);
                ((SQLReplaceable)parent).replace(x, sqlMethodInvokeExpr);
                left.accept(this);
                right.accept(this);
                return false;
            }
        } else if (x.getOperator() == SQLBinaryOperator.SubGtGt) {
            if (parent instanceof SQLReplaceable) {
                sqlMethodInvokeExpr = new SQLMethodInvokeExpr("JSON_VALUE");
                left = x.getLeft();
                right = x.getRight();
                sqlMethodInvokeExpr.addArgument(left);
                sqlMethodInvokeExpr.addArgument(right);
                ((SQLReplaceable)parent).replace(x, sqlMethodInvokeExpr);
                left.accept(this);
                right.accept(this);
                return false;
            }
        } else if (x.getOperator() == SQLBinaryOperator.RegExp) {
            sqlMethodInvokeExpr = new SQLMethodInvokeExpr("REGEXP_LIKE");
            left = x.getLeft();
            right = x.getRight();
            sqlMethodInvokeExpr.addArgument(left);
            sqlMethodInvokeExpr.addArgument(right);
            left.accept(this);
            right.accept(this);
            sqlMethodInvokeExpr.setParent(parent);
            if (parent instanceof SQLBinaryOpExpr) {
                if (((SQLBinaryOpExpr)parent).getLeft() == x) {
                    ((SQLBinaryOpExpr)parent).setLeft(sqlMethodInvokeExpr);
                } else if (((SQLBinaryOpExpr)parent).getRight() == x) {
                    ((SQLBinaryOpExpr)parent).setRight(sqlMethodInvokeExpr);
                }
            } else if (parent instanceof SQLSelectQueryBlock && ((SQLSelectQueryBlock)parent).getWhere() == x) {
                ((SQLSelectQueryBlock)parent).setWhere(sqlMethodInvokeExpr);
            }

            return false;
        }

        return true;
    }

    public boolean visit(SQLAlterTableAddColumn x) {
        x.getColumns().forEach((col) -> {
            col.accept(this);
        });
        if (x.getAfterColumn() != null) {
            x.getAfterColumn().accept(this);
        }

        return false;
    }

    public boolean visit(SQLColumnDefinition x) {
        SQLDataType dataType = x.getDataType();
        if (dataType != null) {
            switch (dataType.getName()) {
                case "bit":
                case "int":
                case "bigint":
                case "smallint":
                case "double":
                    if (dataType.getArguments() != null && dataType.getArguments().size() != 0) {
                        dataType.getArguments().clear();
                    }
            }
        }

        return true;
    }

    public boolean visit(SQLBinaryExpr x) {
        SQLObject parent = x.getParent();
        if (parent instanceof SQLReplaceable) {
            ((SQLReplaceable)parent).replace(x, new SQLCharExpr(x.getText()));
        }

        return true;
    }

    public boolean visit(SQLIntervalExpr x) {
        SQLExpr valueSqlExpr = x.getValue();
        if (valueSqlExpr instanceof SQLValuableExpr) {
            Object value = ((SQLValuableExpr)valueSqlExpr).getValue();
            x.setValue(new SQLCharExpr(Objects.toString(value)));
            return false;
        } else {
            return true;
        }
    }

    public boolean visit(SQLCreateIndexStatement x) {
        String tableName = x.getTableName();
        SQLIndexDefinition indexDefinition = x.getIndexDefinition();
        this.modifyIndexName(tableName, indexDefinition);
        return true;
    }

    private void modifyIndexName(String tableName, SQLIndexDefinition indexDefinition) {
        if (tableName != null && indexDefinition != null && indexDefinition.getName() instanceof SQLIdentifierExpr) {
            SQLName sqlName = indexDefinition.getName();
            String indexName = ((SQLIdentifierExpr)sqlName).getName();
            indexName = "\"" + tableName + "_" + indexName + "\"";
            ((SQLIdentifierExpr)sqlName).setName(indexName);
        } else {
            throw new IllegalArgumentException("-----达梦语句适配-----, 获取索引名失败");
        }
    }

    public boolean visit(SQLAlterTableAddIndex x) {
        String tableName = null;
        SQLObject parent = x.getParent();
        if (parent instanceof SQLAlterTableStatement) {
            SQLExprTableSource sqlExprTableSource = ((SQLAlterTableStatement)parent).getTableSource();
            if (sqlExprTableSource != null && sqlExprTableSource.getExpr() != null && sqlExprTableSource.getExpr() instanceof SQLIdentifierExpr) {
                tableName = ((SQLIdentifierExpr)sqlExprTableSource.getExpr()).getName();
            }
        }

        this.modifyIndexName(tableName, x.getIndexDefinition());
        return true;
    }
}

