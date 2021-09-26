package be.sel2.api.controllers;

import be.sel2.api.dtos.ServiceDTO;
import be.sel2.api.entities.Service;
import be.sel2.api.exceptions.not_found.ServiceNotFoundException;
import be.sel2.api.models.RichCollectionModel;
import be.sel2.api.repositories.ServiceRepository;
import be.sel2.api.util.AllFieldSearchUtil;
import be.sel2.api.util.SortUtils;
import be.sel2.api.util.specifications.DefaultSpecification;
import be.sel2.api.util.specifications.OffsetBasedPageable;
import be.sel2.api.util.specifications.PartialMatchSpecification;
import be.sel2.api.util.specifications.SearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * This class controls all requests to `/services`
 */
@RestController
@RequestMapping("/services")
public class ServiceController {

    private final ServiceRepository repository;

    /**
     * Creates a new {@link ServiceController} with given repository.
     * This class controls all requests to `/services`
     *
     * @param repository Source {@link ServiceRepository} for this controller
     */
    public ServiceController(ServiceRepository repository) {
        this.repository = repository;
    }

    private WebMvcLinkBuilder linkToSearchService() {
        return linkTo(ServiceController.class);
    }

    private EntityModel<Service> serviceToHateoas(Service serv) {
        return EntityModel.of(serv,
                linkTo(methodOn(ServiceController.class).one(serv.getId())).withSelfRel(),
                linkToSearchService().withRel("services")
        );
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CUSTOMER"})
    @GetMapping //Handles GET requests
    public RichCollectionModel<Service> searchService(
            @RequestParam Map<String, String> parameters,
            @RequestParam Optional<Boolean> deprecated,
            @RequestParam(defaultValue = "0") int skip,
            @RequestParam(defaultValue = "50") int limit) {

        List<String> entityFields = List.of("name",
                "domain", "description", "created", "lastUpdated");

        Pageable pageable = new OffsetBasedPageable(skip, limit,
                SortUtils.sortWithParameters(parameters, entityFields));
        DefaultSpecification<Service> specification = new PartialMatchSpecification<>();

        specification.addWithKey(parameters, "name");
        specification.addWithKey(parameters, "domain");

        deprecated.ifPresent(dep -> specification.add(new SearchCriteria("deprecated", dep)));

        Specification<Service> resSpec = specification.and(AllFieldSearchUtil.getSpecification(Service.class, parameters));

        Page<Service> searchResults = repository.findAll(resSpec, pageable);

        List<EntityModel<Service>> services = searchResults
                .stream().map(this::serviceToHateoas)
                .collect(Collectors.toList());

        return RichCollectionModel.of(services, searchResults,
                linkToSearchService().withSelfRel());
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE"})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping //Handles POST requests
    public Service newService(@RequestBody ServiceDTO createService) {
        createService.testValidity(true);

        Service newService = createService.getEntity();
        return repository.save(newService);
    }

    // Single item
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CUSTOMER"})
    @GetMapping("/{id}") //Handles GET requests
    public EntityModel<Service> one(@PathVariable Long id) {

        Service service = repository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException(id));

        return serviceToHateoas(service);
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE"})
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}") //Handles PATCH requests
    public EntityModel<Service> editService(@RequestBody ServiceDTO newService, @PathVariable Long id) {

        newService.testValidity(false);

        Service service = repository.findById(id)
                .map(serv -> {
                    newService.updateEntity(serv);
                    return repository.save(serv);
                })
                .orElseThrow(() -> new ServiceNotFoundException(id));

        return serviceToHateoas(service);
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}") //Handles DELETE requests
    public void deleteService(@PathVariable Long id) {
        if (repository.findById(id).isEmpty()) {
            throw new ServiceNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
