package be.sel2.api.util.specifications;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * A pageable that uses limit and offset parameters
 * rather than the default pageSize and pageNumber
 */
public class OffsetBasedPageable implements Pageable {

    private int limit;
    private int offset;
    private final Sort sort;

    /**
     * Creates a new {@link OffsetBasedPageable} with sort parameters applied.
     *
     * @param offset zero-based offset.
     * @param limit  the size of the elements to be returned.
     * @param sort   can be {@literal null}.
     */
    public OffsetBasedPageable(int offset, int limit, Sort sort) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset index must not be less than zero!");
        }

        if (limit < 1) {
            throw new IllegalArgumentException("Limit must not be less than one!");
        }
        this.limit = limit;
        this.offset = offset;
        this.sort = sort;
    }

    /**
     * Creates a new {@link OffsetBasedPageable} without sort parameters applied.
     *
     * @param offset zero-based offset.
     * @param limit  the size of the elements to be returned.
     */
    public OffsetBasedPageable(int offset, int limit) {
        this(offset, limit, Sort.unsorted());
    }

    @Override
    public int getPageNumber() {
        return offset / limit;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    @NonNull
    public Sort getSort() {
        return sort;
    }

    @Override
    @NonNull
    public Pageable next() {
        return new OffsetBasedPageable(offset + getPageSize(), getPageSize(), getSort());
    }

    public OffsetBasedPageable previous() {
        return hasPrevious() ? new OffsetBasedPageable(offset - getPageSize(), getPageSize(), getSort()) : this;
    }


    @Override
    @NonNull
    public Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    @Override
    @NonNull
    public Pageable first() {
        return new OffsetBasedPageable(0, getPageSize(), getSort());
    }

    @Override
    public boolean hasPrevious() {
        return offset > limit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof OffsetBasedPageable)) return false;

        OffsetBasedPageable that = (OffsetBasedPageable) o;

        return Objects.equals(limit, that.limit) &&
                Objects.equals(offset, that.offset) &&
                Objects.equals(sort, that.sort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(limit, offset, sort);
    }

    @Override
    public String toString() {
        return "OffsetBasedPageable{" + "limit=" + this.limit + ", offset=" + this.offset + ", sort=" + this.sort.toString() + "" + '}';
    }


}
