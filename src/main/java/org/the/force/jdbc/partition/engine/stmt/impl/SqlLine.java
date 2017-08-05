package org.the.force.jdbc.partition.engine.stmt.impl;

import org.the.force.jdbc.partition.engine.stmt.LogicStmt;

/**
 * Created by xuji on 2017/7/30.
 */
public interface SqlLine extends LogicStmt {

    int getLineNumber();

    void setLineNumber(int lineNumber);

}
