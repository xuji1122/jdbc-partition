package org.the.force.jdbc.partition.common.tuple;



import org.the.force.jdbc.partition.common.BeanUtils;

/**
 * Created by xuji on 2017/7/2.
 */
public class Triple<L, M, R> {

    public final L left;
    public final M middle;
    public final R right;

    public static <L, M, R> Triple<L, M, R> of(final L left, final M middle, final R right) {
        return new Triple<L, M, R>(left, middle, right);
    }

    public Triple(final L left, final M middle, final R right) {
        super();
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    //-----------------------------------------------------------------------
    public L getLeft() {
        return left;
    }

    public M getMiddle() {
        return middle;
    }

    public R getRight() {
        return right;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Triple<?, ?, ?>) {
            final Triple<?, ?, ?> other = (Triple<?, ?, ?>) obj;
            return BeanUtils.equals(getLeft(), other.getLeft()) && BeanUtils.equals(getMiddle(), other.getMiddle()) && BeanUtils.equals(getRight(), other.getRight());
        }
        return false;
    }

    /**
     * <p>Returns a suitable hash code.</p>
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return (getLeft() == null ? 0 : getLeft().hashCode()) ^ (getMiddle() == null ? 0 : getMiddle().hashCode()) ^ (getRight() == null ? 0 : getRight().hashCode());
    }
}
