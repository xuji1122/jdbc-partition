package org.the.force.jdbc.partition.common.tuple;


import org.the.force.jdbc.partition.common.BeanUtils;

/**
 * Created by xuji on 2017/7/2.
 */
public class Pair<L, R> {

    /**
     * Left object
     */
    public final L left;
    /**
     * Right object
     */
    public final R right;


    public static <L, R> Pair<L, R> of(final L left, final R right) {
        return new Pair<L, R>(left, right);
    }

    /**
     * Create a new pair instance.
     *
     * @param left  the left evaluator, may be null
     * @param right the right evaluator, may be null
     */
    public Pair(final L left, final R right) {
        super();
        this.left = left;
        this.right = right;
    }

    public final L getKey() {
        return getLeft();
    }

    public R getValue() {
        return getRight();
    }

    //-----------------------------------------------------------------------
    public L getLeft() {
        return left;
    }

    /**
     * {@inheritDoc}
     */
    public R getRight() {
        return right;
    }

    public R setValue(final R value) {
        throw new UnsupportedOperationException();
    }


    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Pair<?, ?>) {
            final Pair<?, ?> other = (Pair<?, ?>) obj;
            return BeanUtils.equals(getKey(), other.getKey()) && BeanUtils.equals(getValue(), other.getValue());
        }
        return false;
    }

    /**
     * <p>Returns a suitable hash code.
     * The hash code follows the ddl in {@code Map.Entry}.</p>
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return (getKey() == null ? 0 : getKey().hashCode()) ^ (getValue() == null ? 0 : getValue().hashCode());
    }

    /**
     * <p>Returns a String representation of this pair using the format {@code ($left,$right)}.</p>
     *
     * @return a string describing this object, not null
     */
    @Override
    public String toString() {
        return new StringBuilder().append('(').append(getLeft()).append(',').append(getRight()).append(')').toString();
    }


}
