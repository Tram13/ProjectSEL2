package be.sel2.api.entities;

import be.sel2.api.serializers.FileLocationSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Entity
@Getter
@Setter //This adds all the getters & setters for us
public class FileMeta implements StatisticsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonSerialize(using = FileLocationSerializer.class)
    private @NotNull String fileLocation;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    // Er moet een default date object toegevoegd worden in de databank. Dit veld moet niet worden geüpdate.
    private Date created = new Date();

    private Long fileSize;

    public FileMeta() {
    }

    public FileMeta(@NotNull String fileLocation) {
        this.fileLocation = fileLocation;
    }

    @Override
    @JsonIgnore
    public Date getLastUpdated() {
        return getCreated();
    }

    @Override
    public String toString() {
        return "FileMeta{" +
                "id=" + id +
                ", fileLocation='" + fileLocation + '\'' +
                ", created=" + created +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileMeta fileMeta = (FileMeta) o;
        return Objects.equals(id, fileMeta.id) && Objects.equals(fileLocation, fileMeta.fileLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fileLocation);
    }
}
