package be.sel2.api.controllers;

import be.sel2.api.dtos.PackageDTO;
import be.sel2.api.entities.Package;
import be.sel2.api.entities.Service;
import be.sel2.api.entities.relations.PackageService;
import be.sel2.api.exceptions.not_found.PackageNotFoundException;
import be.sel2.api.exceptions.not_found.ServiceNotFoundException;
import be.sel2.api.models.RichCollectionModel;
import be.sel2.api.repositories.PackageRepository;
import be.sel2.api.repositories.PackageServiceRepository;
import be.sel2.api.repositories.ServiceRepository;
import be.sel2.api.util.AllFieldSearchUtil;
import be.sel2.api.util.SortUtils;
import be.sel2.api.util.specifications.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * This class controls all requests to `/packages`
 */
@RestController
@RequestMapping("/packages")
public class PackageController {

    private final PackageRepository repository;
    private final ServiceRepository servRepo;
    private final PackageServiceRepository packServRepo;

    /**
     * Creates a new {@link PackageController} with given repository.
     * This class controls all requests to `/packages`
     *
     * @param repository   Source {@link PackageRepository} for this controller
     * @param servRepo     Source {@link ServiceRepository} for this controller
     * @param packServRepo Source {@link PackageServiceRepository} for this controller
     */
    public PackageController(PackageRepository repository, ServiceRepository servRepo, PackageServiceRepository packServRepo) {
        this.repository = repository;
        this.servRepo = servRepo;
        this.packServRepo = packServRepo;
    }

    private WebMvcLinkBuilder linkToSearchPackage() {
        return linkTo(PackageController.class);
    }

    private EntityModel<Package> packageToHateoas(Package perm) {
        return EntityModel.of(perm,
                linkTo(methodOn(PackageController.class).one(perm.getId())).withSelfRel(),
                linkToSearchPackage().withRel("packages")
        );
    }

    private Specification<Package> serviceSpecification(Map<String, String> parameters) {
        if (parameters.containsKey("serviceName")) {
            DefaultSpecification<Service> serviceSpec = new PartialMatchSpecification<>();
            serviceSpec.addWithKey(parameters, "serviceName", "name");

            List<Service> services = servRepo.findAll(serviceSpec);

            ContainsSpecification<PackageService> packServSpec = new ContainsSpecification<>();
            packServSpec.addAll(services.stream().map(s -> new SearchCriteria("service", s))
                    .collect(Collectors.toList()));

            List<PackageService> packServices = packServRepo.findAll(packServSpec);

            AnyIsInListSpecification<Package> spec = new AnyIsInListSpecification<>();
            spec.add(new SearchCriteria("services", packServices));
            return spec;
        }

        return new DefaultSpecification<>();
    }

    //AUTH: ALLOW LOGGED IN
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CUSTOMER"})
    @GetMapping //Handles GET requests
    public RichCollectionModel<Package> searchPackage(
            @RequestParam Map<String, String> parameters,
            @RequestParam Optional<Boolean> deprecated,
            @RequestParam(defaultValue = "0") int skip,
            @RequestParam(defaultValue = "50") int limit) {

        List<String> entityFields = List.of("name", "created", "lastUpdated");

        Pageable pageable = new OffsetBasedPageable(skip, limit,
                SortUtils.sortWithParameters(parameters, entityFields));
        DefaultSpecification<Package> specification = new PartialMatchSpecification<>();

        specification.addWithKey(parameters, "name");

        deprecated.ifPresent(dep -> specification.add(new SearchCriteria("deprecated", dep)));

        Specification<Package> resultingSpec = specification
                .and(AllFieldSearchUtil.getSpecification(Package.class, parameters));

        Page<Package> searchResults = repository.findAll(resultingSpec
                .and(serviceSpecification(parameters)), pageable);

        List<EntityModel<Package>> packages = searchResults
                .stream().map(this::packageToHateoas)
                .collect(Collectors.toList());

        return RichCollectionModel.of(packages, searchResults,
                linkToSearchPackage().withSelfRel());
    }

    //AUTH: ADMIN, EMPLOYEE
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE"})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping //Handles POST requests
    public EntityModel<Package> newPackage(@RequestBody PackageDTO createPackage) {

        createPackage.testValidity(true);
        Package newPackage = createPackage.getEntity();

        Set<PackageService> serviceSet = newPackage.getServices();
        newPackage.setServices(new HashSet<>());
        if (serviceSet != null) {
            for (PackageService service : serviceSet) {
                if (!servRepo.existsById(service.getService().getId())) {
                    throw new ServiceNotFoundException(service.getService().getId());
                }
            }
        }

        Package result = repository.save(newPackage);

        if (serviceSet != null) {
            for (PackageService ps : serviceSet) {
                ps.setPack(result);
                packServRepo.save(ps);
            }
            result.setServices(serviceSet);
        }

        return packageToHateoas(result);
    }

    //AUTH: ALLOW LOGGED IN
    // Single item
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CUSTOMER"})
    @GetMapping("/{id}") //Handles GET requests
    public EntityModel<Package> one(@PathVariable Long id) {

        Package pack = repository.findById(id)
                .orElseThrow(() -> new PackageNotFoundException(id));

        return packageToHateoas(pack);
    }

    //AUTH: ADMIN, EMPLOYEE
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE"})
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}") //Handles PATCH requests
    public EntityModel<Package> editPackage(@RequestBody PackageDTO newPackage, @PathVariable Long id) {

        Package pack = repository.findById(id)
                .orElseThrow(() -> new PackageNotFoundException(id));

        Set<PackageService> oldServices = pack.getServices();

        newPackage.testValidity(false);

        newPackage.updateEntity(pack);

        if (newPackage.getServices() != null) {
            Set<PackageService> newServices = pack.getServices();
            for (PackageService service : newServices) {
                if (!servRepo.existsById(service.getService().getId())) {
                    throw new ServiceNotFoundException(service.getService().getId());
                }
            }
            // Delete all relations that are no longer included
            for (PackageService service : oldServices) {
                if (!newServices.contains(service)) {
                    packServRepo.delete(service);
                }
            }
            // Add all relations that are new
            for (PackageService service : newServices) {
                if (!oldServices.contains(service)) {
                    packServRepo.save(service);
                }
            }

        }
        Package res = repository.save(pack);

        return packageToHateoas(res);
    }

    //AUTH: ADMIN, EMPLOYEE
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}") //Handles DELETE requests
    public void deletePackage(@PathVariable Long id) {
        if (repository.findById(id).isEmpty()) {
            throw new PackageNotFoundException(id);
        }
        repository.deleteById(id);
    }

    private EntityModel<PackageService> serviceToHateoas(PackageService serv) {
        return EntityModel.of(serv,
                linkTo(methodOn(ServiceController.class).one(serv.getService().getId())).withSelfRel(),
                linkTo(ServiceController.class).withRel("services")
        );
    }

    //AUTH: ALLOW LOGGED IN
    // Services of a single package
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CUSTOMER"})
    @GetMapping("/{id}/services") //Handles GET requests
    public RichCollectionModel<PackageService> getPackageServices(
            @PathVariable Long id,
            @RequestParam Map<String, String> parameters,
            @RequestParam Optional<Boolean> deprecated,
            @RequestParam(defaultValue = "0") int skip,
            @RequestParam(defaultValue = "50") int limit) {

        Link selfLink = Link.of(linkTo(PackageController.class).toUri() + "/" + id + "/services").withSelfRel();

        List<String> entityFields = List.of("service.name",
                "service.domain", "service.description", "service.created", "service.lastUpdated");

        if (parameters.containsKey("sortBy")) {
            parameters.put("sortBy", "service." + parameters.get("sortBy"));
        }

        Pageable pageable = new OffsetBasedPageable(skip, limit,
                SortUtils.sortWithParameters(parameters, entityFields));
        DefaultSpecification<Service> specification = new PartialMatchSpecification<>();

        specification.addWithKey(parameters, "domain");
        specification.addWithKey(parameters, "name");

        deprecated.ifPresent(dep -> specification.add(new SearchCriteria("deprecated", dep)));

        Package pack = repository.findById(id)
                .orElseThrow(() -> new PackageNotFoundException(id));

        if (pack.getServices().isEmpty()) {
            return RichCollectionModel.of(Set.of(), 0, 0, selfLink);
        }

        ContainsSpecification<Service> packSpec = new ContainsSpecification<>();

        packSpec.addAll(pack.getServices().stream().map(
                s -> new SearchCriteria("id", s.getService().getId())
        ).collect(Collectors.toList()));

        Specification<Service> resSpec = specification.and(AllFieldSearchUtil.getSpecification(Service.class, parameters))
                .and(packSpec);

        List<Service> searchResults = servRepo.findAll(resSpec);

        if (searchResults.isEmpty()) {
            return RichCollectionModel.of(Set.of(), 0, 0, selfLink);
        }

        ContainsSpecification<PackageService> packServSpec = new ContainsSpecification<>();

        packServSpec.addAll(searchResults.stream().map(
                s -> new SearchCriteria("service", s)
        ).collect(Collectors.toList()));

        DefaultSpecification<PackageService> defSpec = new DefaultSpecification<>();
        defSpec.add(new SearchCriteria("pack", pack));

        Page<PackageService> finalResult = packServRepo.findAll(packServSpec.and(defSpec), pageable);

        List<EntityModel<PackageService>> services = finalResult
                .stream().map(this::serviceToHateoas)
                .collect(Collectors.toList());

        return RichCollectionModel.of(services, finalResult,
                selfLink);
    }
}
