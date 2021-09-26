package be.sel2.api.entities.relations;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class PackageServiceId implements Serializable {
    private Long pack;
    private Long service;

    public PackageServiceId() { // Empty constructor necessary for ID classes
    }

    public PackageServiceId(Long pack, Long service) {
        this.pack = pack;
        this.service = service;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PackageServiceId)) return false;
        PackageServiceId that = (PackageServiceId) o;
        return pack.equals(that.pack) && service.equals(that.service);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pack, service);
    }
}
