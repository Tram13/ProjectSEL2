package be.sel2.api.entities.relations;

import be.sel2.api.entities.Package;
import be.sel2.api.entities.Service;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@IdClass(PackageServiceId.class)
@Getter
@Setter //This adds all the getters, setters, equals & hash for us
public class PackageService {

    @JsonIgnore
    @Id
    @ManyToOne
    @JoinColumn(name = "packageId", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "none"))
    private Package pack; // "package" is not a valid variable name in Java

    @Id
    @ManyToOne
    @JoinColumn(name = "serviceId", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "none"))
    private Service service;

    private String source;

    @Enumerated(EnumType.STRING)
    private Service.DeliveryMethod deliveryMethod;

    public PackageService() {
    }

    public PackageService(Package pack, Service service, String source, Service.DeliveryMethod deliveryMethod) {
        this.pack = pack;
        this.service = service;
        this.source = source;
        this.deliveryMethod = deliveryMethod;
    }

    public PackageServiceId getId() { // Samengesteld ID
        return new PackageServiceId(pack.getId(), service.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PackageService that = (PackageService) o;
        return pack.equals(that.pack) && service.equals(that.service) && source.equals(that.source) && deliveryMethod == that.deliveryMethod;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pack, service, source, deliveryMethod);
    }

    @Override
    public String toString() {
        return "PackageService{" +
                "packId=" + pack.getId() +
                ", serviceId=" + service.getId() +
                ", source='" + source + '\'' +
                ", deliveryMethod=" + deliveryMethod +
                '}';
    }
}
