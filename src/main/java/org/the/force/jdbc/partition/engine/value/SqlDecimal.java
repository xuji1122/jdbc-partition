package org.the.force.jdbc.partition.engine.value;

import java.math.BigDecimal;

/**
 * Created by xuji on 2017/7/21.
 */
public interface SqlDecimal  extends SqlNumber{

    BigDecimal getNumber();

}
