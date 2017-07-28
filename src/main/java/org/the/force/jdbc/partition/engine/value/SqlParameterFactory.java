package org.the.force.jdbc.partition.engine.value;

/**
 * Created by xuji on 2017/7/27.
 */
public interface SqlParameterFactory {

    SqlParameter parse(String input);

    
}
