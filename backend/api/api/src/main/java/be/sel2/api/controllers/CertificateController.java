package be.sel2.api.controllers;

import be.sel2.api.dtos.CertificateDTO;
import be.sel2.api.entities.Certificate;
import be.sel2.api.entities.*;
import be.sel2.api.entities.relations.ContactProposal;
import be.sel2.api.exceptions.ForbiddenException;
import be.sel2.api.exceptions.not_found.CertificateNotFoundException;
import be.sel2.api.exceptions.not_found.FileNotFoundException;
import be.sel2.api.exceptions.not_found.ProposalNotFoundException;
import be.sel2.api.models.RichCollectionModel;
import be.sel2.api.repositories.*;
import be.sel2.api.users.UserInfoDetails;
import be.sel2.api.util.CSRSigner;
import be.sel2.api.util.SecurityUtils;
import be.sel2.api.util.SortUtils;
import be.sel2.api.util.specifications.*;
import org.bouncycastle.operator.OperatorCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * This class controls all requests to `/certificates`
 */
@RestController
@RequestMapping("/certificates")
public class CertificateController {

    @Value("${file-location}")
    private String uploadedFolder;
    @Value("${server.ssl.key-store-password}")
    private String keyStorePassword;
    @Value("${server.ssl.key-alias}")
    private String keyStoreAlias;
    @Value("${server.ssl.key-store}")
    private String keyStorePath;

    private final CertificateRepository certRepo;
    private final ProposalRepository propRepo;
    private final FileRepository fileRepo;
    private final ContactRepository contactRepo;
    private final UserRepository userRepo;
    private final ContactProposalRepository conPropRepo;

    private final ResourceLoader loader;

    private static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";
    private static final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String PROPOSAL_SEARCHCRITERIA = "proposal";

    /**
     * This class controls all requests to `/certificates`
     *
     * @param certRepo    Source {@link CertificateRepository} for this controller
     * @param propRepo    Source {@link ProposalRepository} for this controller
     * @param fileRepo    Source {@link FileRepository} for this controller
     * @param contactRepo Source {@link ContactRepository} for this controller
     * @param userRepo    Source {@link UserRepository} for this controller
     * @param conPropRepo Source {@link ContactProposalRepository} for this controller
     * @param loader      {@link ResourceLoader} used to load keystore
     */
    public CertificateController(CertificateRepository certRepo, ProposalRepository propRepo, FileRepository fileRepo, ContactRepository contactRepo, UserRepository userRepo, ContactProposalRepository conPropRepo, ResourceLoader loader) {
        this.certRepo = certRepo;
        this.propRepo = propRepo;
        this.fileRepo = fileRepo;
        this.contactRepo = contactRepo;
        this.userRepo = userRepo;
        this.conPropRepo = conPropRepo;
        this.loader = loader;
    }

    private EntityModel<Certificate> certificateToHateoas(Certificate cert) {
        return EntityModel.of(cert,
                linkTo(methodOn(CertificateController.class).getCertificateById(cert.getId())).withSelfRel(),
                linkTo(methodOn(ProposalController.class).one(cert.getProposal().getId())).withRel("proposal"),
                linkTo(methodOn(OrganisationController.class)
                        .one(cert.getProposal().getOrganisation().getId())).withRel("organisation"));
    }

    private Specification<Proposal> getRelatedViaContact(UserInfoDetails userDetails) {
        DefaultSpecification<Contact> contactSpec = new DefaultSpecification<>();

        // Get the contacts for this email address
        contactSpec.add(new SearchCriteria("email", userDetails.getUsername()));

        List<Contact> contacts = contactRepo.findAll(contactSpec);

        DefaultSpecification<ContactProposal> conPropSpec = new ContainsSpecification<>();
        for (Contact conProp : contacts) {
            conPropSpec.add(new SearchCriteria("contact", conProp));
        }
        List<ContactProposal> contactProps = conPropRepo.findAll(conPropSpec);

        // Get all related proposals
        DefaultSpecification<Proposal> propSpec = new AnyIsInListSpecification<>();
        propSpec.add(new SearchCriteria("contacts", contactProps));

        return propSpec;
    }

    private Specification<Proposal> getRelatedViaOrganisation(UserInfoDetails userDetails) {

        UserInfo user = userRepo.getOne(userDetails.getId());

        // Get all organisations this user manages
        List<Organisation> managedOrganisations = user
                .getMemberships().stream()
                .filter(Member::getAccepted)
                .map(Member::getOrganisation)
                .collect(Collectors.toList());

        // Get all proposals for any of these organisations
        ContainsSpecification<Proposal> propSpec = new ContainsSpecification<>();
        propSpec.addAll(managedOrganisations.stream().map(o -> new SearchCriteria("organisation", o))
                .collect(Collectors.toSet()));

        return propSpec;
    }

    private Specification<Certificate> getRelatedViaProposal(UserInfoDetails userInfoDetails) {

        // When either related via Contact
        Specification<Proposal> propSpec = getRelatedViaContact(userInfoDetails);
        // Or when manager from the organisation
        propSpec = propSpec.or(getRelatedViaOrganisation(userInfoDetails));

        List<Proposal> proposals = propRepo.findAll(propSpec);

        return getCertificatesOfProposals(proposals);
    }

    private Specification<Certificate> getCertificatesOfOrganisation(Long organisationId) {
        DefaultSpecification<Proposal> propSpec = new DefaultSpecification<>();
        propSpec.add(new SearchCriteria("organisation", organisationId));

        List<Proposal> proposals = propRepo.findAll(propSpec);

        return getCertificatesOfProposals(proposals);
    }

    private Specification<Certificate> getCertificatesOfProposals(List<Proposal> proposals) {
        ContainsSpecification<Certificate> resSpec = new ContainsSpecification<>();
        resSpec.addAll(proposals.stream().map(p -> new SearchCriteria(PROPOSAL_SEARCHCRITERIA, p))
                .collect(Collectors.toSet()));

        return resSpec;
    }

    // AUTH: ADMIN, EMPLOYEE or
    // (CUSTOMER if he is CONTACT of a proposal where this certificate belongs to OR MANAGER org can view every certificate)
    @Secured({ROLE_ADMIN, ROLE_EMPLOYEE, ROLE_CUSTOMER})
    @GetMapping
    public RichCollectionModel<Certificate> getAllCertificates(
            @RequestParam Map<String, String> parameters,
            @RequestParam Optional<Long> proposalId,
            @RequestParam Optional<Long> organisationId,
            @RequestParam(defaultValue = "0") int skip,
            @RequestParam(defaultValue = "50") int limit
    ) {
        Pageable pageable = new OffsetBasedPageable(skip, limit,
                SortUtils.sortWithParameters(parameters, SortUtils.DATE_FIELDS));

        DefaultSpecification<Certificate> specification = new DefaultSpecification<>();

        proposalId.ifPresent(aLong -> specification.add(new SearchCriteria(PROPOSAL_SEARCHCRITERIA, aLong)));

        SecurityContext context = SecurityContextHolder.getContext();

        Specification<Certificate> resSpec = specification;

        if (SecurityUtils.hasRole(context, ROLE_CUSTOMER)) {
            // The user is a customer
            UserInfoDetails userDetails = SecurityUtils.getDetails(context);

            resSpec = resSpec.and(getRelatedViaProposal(userDetails));
        }

        if (organisationId.isPresent()) {
            resSpec = resSpec.and(getCertificatesOfOrganisation(organisationId.get()));
        }

        Page<Certificate> searchResults = certRepo.findAll(resSpec, pageable);

        List<EntityModel<Certificate>> certificates = searchResults
                .stream().map(this::certificateToHateoas).collect(Collectors.toList());

        return RichCollectionModel.of(certificates, searchResults,
                linkTo(CertificateController.class).withSelfRel());
    }

    // AUTH: ADMIN or (CUSTOMER if (MEMBER or SUBMITTER) if proposal is approved)
    @Secured({ROLE_ADMIN, ROLE_CUSTOMER})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Certificate> createCertificate(@RequestBody CertificateDTO newCertificate) throws
            IOException, CertificateException, NoSuchProviderException, KeyStoreException,
            NoSuchAlgorithmException, UnrecoverableKeyException, OperatorCreationException {

        newCertificate.testValidity(true);

        Long proposalId = newCertificate.getProposalId();
        if (!propRepo.existsById(proposalId)) {
            throw new ProposalNotFoundException(proposalId);
        }

        Long fileId = newCertificate.getFile();
        FileMeta file = fileRepo.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException(fileId));

        SecurityContext context = SecurityContextHolder.getContext();
        Proposal proposal = propRepo.getOne(proposalId);

        if (SecurityUtils.hasRole(context, ROLE_CUSTOMER)) {
            // User is a customer
            if (!(SecurityUtils.isMember(context, false, proposal.getOrganisation()) ||
                    SecurityUtils.isContact(context, true, proposal))) {
                // The customer is not a manager, nor a submitter
                throw new ForbiddenException("Unauthorized patch");
            }

            if (!proposal.getStatus().equals(Proposal.ProposalStatus.ACCEPTED)) {
                // The status of the proposal is not ACCEPTED
                throw new ForbiddenException("Proposal is not approved");
            }
        }

        if (!file.getFileLocation().endsWith(".csr")) {
            throw new ForbiddenException("File is not a certificate file");
        }

        Certificate res = newCertificate.getEntity();

        // Sign certificate before saving it
        Security.setProperty("crypto.policy", "unlimited");

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        if (keyStorePath.startsWith("classpath:")) {
            Resource keyStoreResource = loader.getResource(keyStorePath);
            keyStore.load(keyStoreResource.getInputStream(), keyStorePassword.toCharArray());
        } else {
            keyStore.load(new FileInputStream(keyStorePath), keyStorePassword.toCharArray());
        }
        X509Certificate cert;

        try (Reader fileReader = new FileReader(uploadedFolder + file.getFileLocation())) {
            cert = CSRSigner.sign(fileReader, 365, keyStore, keyStoreAlias, keyStorePassword.toCharArray());
        }

        if (cert == null) {
            throw new ForbiddenException("Certificate could not be signed");
        }

        String newFileName = file.getFileLocation().replaceFirst("\\.csr$", ".crt");
        try (FileOutputStream stream = new FileOutputStream(uploadedFolder + newFileName)) {
            stream.write(cert.getEncoded());
        }


        File oldFile = new File(uploadedFolder + file.getFileLocation());

        try {
            Files.delete(oldFile.toPath());
        } catch (SecurityException | IOException ex) {
            ex.printStackTrace();
        }

        File newFile = new File(uploadedFolder + newFileName);


        file.setFileLocation(newFileName);
        file.setFileSize(newFile.getTotalSpace());
        fileRepo.save(file);
        res.setFile(file);
        res.setProposal(proposal);

        return certificateToHateoas(certRepo.save(res));
    }


    // AUTH: ADMIN or (CUSTOMER if MANAGER or SUBMITTER)
    @Secured({ROLE_ADMIN, ROLE_CUSTOMER})
    @GetMapping("/{id}")
    public EntityModel<Certificate> getCertificateById(@PathVariable Long id) {

        SecurityContext context = SecurityContextHolder.getContext();

        Certificate cert = certRepo.findById(id)
                .orElseThrow(() -> new CertificateNotFoundException(id));

        Proposal proposal = cert.getProposal();
        if (SecurityUtils.hasRole(context, ROLE_CUSTOMER) && !(SecurityUtils.isContactOrMember(context, false, true, proposal))) {
            // User is a CUSTOMER and
            // The customer is not a manager, nor a submitter
            throw new ForbiddenException("Unauthorized get, only a member or submitter can get this");
        }

        return certificateToHateoas(cert);
    }

    // AUTH: ADMIN or (CUSTOMER if MEMBER or SUBMITTER)
    @Secured({ROLE_ADMIN, ROLE_CUSTOMER})
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCertificateById(@PathVariable Long id) {

        SecurityContext context = SecurityContextHolder.getContext();

        Certificate cert = certRepo.findById(id)
                .orElseThrow(() -> new CertificateNotFoundException(id));

        Proposal proposal = cert.getProposal();

        if (SecurityUtils.hasRole(context, ROLE_CUSTOMER) && // User is a CUSTOMER
                !(SecurityUtils.isMember(context, false, proposal.getOrganisation()) ||
                        SecurityUtils.isContact(context, true, proposal))
        ) {
            // The customer is not a manager, nor a submitter
            throw new ForbiddenException("Unauthorized delete, only a member or submitter can delete this");
        }

        certRepo.delete(cert);
    }
}
