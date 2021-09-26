package be.sel2.api.models;

import lombok.Getter;

import java.util.Date;

@Getter
public class StatisticsModel {

    private final Date created;
    private final Date lastUpdated;
    private final Boolean deleted;

    public StatisticsModel(Date created, Date lastUpdated, Boolean deleted) {
        this.created = created;
        this.lastUpdated = lastUpdated;
        this.deleted = deleted;
    }
}
