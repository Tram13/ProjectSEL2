package be.sel2.api.models;

import org.springframework.data.domain.Page;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

/**
 * Collection model that also returns total and count fields in addition to its
 * default values.
 */
public class RichCollectionModel<T> extends CollectionModel<EntityModel<T>> {

    private long total;
    private long count;

    public RichCollectionModel() {
    }

    @SuppressWarnings("deprecation")
    private RichCollectionModel(Iterable<EntityModel<T>> content, Link... links) {
        super(content, links);  //How else am I gonna do this?
    }

    @SuppressWarnings("deprecation")
    private RichCollectionModel(Iterable<EntityModel<T>> content, Page<T> page, Link... links) {
        super(content, links);  //How else am I gonna do this?
        this.total = page.getTotalElements();
        this.count = page.getNumberOfElements();
    }

    public static <T> RichCollectionModel<T> of(Iterable<EntityModel<T>> content, Page<T> page, Link... links) {
        return new RichCollectionModel<>(content, page, links);
    }

    public static <T> RichCollectionModel<T> of(Iterable<EntityModel<T>> content, long total, long count, Link... links) {
        RichCollectionModel<T> res = new RichCollectionModel<>(content, links);
        res.setTotal(total);
        res.setCount(count);
        return res;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
