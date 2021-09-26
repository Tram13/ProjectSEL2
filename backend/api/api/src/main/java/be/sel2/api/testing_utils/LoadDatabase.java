package be.sel2.api.testing_utils;

import be.sel2.api.entities.Package;
import be.sel2.api.entities.*;
import be.sel2.api.entities.relations.ContactProposal;
import be.sel2.api.entities.relations.PackageService;
import be.sel2.api.entities.relations.ProposalService;
import be.sel2.api.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);
    private static final String PRELOADING = "Preloading {}";
    private static final UserInfo[] users = {
            new UserInfo("Amber", "Thienpont", "ambethie@artevelde3.be", "Hunter21", UserInfo.Userrole.CUSTOMER),
            new UserInfo("Bilbo", "Baggins", "bilbo.baggins@gmail.com", "1ring2ruleMall", UserInfo.Userrole.ADMIN),
            new UserInfo("German", "Bratwurst", "german.bratwurst@gmail.com", "Kartoffelkopf2000", UserInfo.Userrole.ADMIN),
            new UserInfo("Peter", "File", "peter.file@gmail.com", "hi3Asd54", UserInfo.Userrole.EMPLOYEE),
            new UserInfo("Peter", "Selie", "peter.selie@gmail.com", "54pl1A5sd", UserInfo.Userrole.EMPLOYEE),
            new UserInfo("Paus", "Urbanus", "paus.urbanus@vatican.com", "IMD_p0pe", UserInfo.Userrole.CUSTOMER),
            new UserInfo("Walter", "White", "walter.white@hotmail.com", "IMD1whoknocks", UserInfo.Userrole.CUSTOMER),
            new UserInfo("William", "Wallace", "william.wallace@freedom.scot", "dsk9mG2qsdP", UserInfo.Userrole.CUSTOMER),
            new UserInfo("William", "Shatner", "william.shatner@hotmail.com", "am5qsDf98Sdv", UserInfo.Userrole.CUSTOMER),
            new UserInfo("William", "Shakespeare", "william.shakespear@hotmail.com", "P_sd4qs75m", UserInfo.Userrole.EMPLOYEE),
            new UserInfo("Gandalf", "The Grey", "gandalf.grey@wizard.com", "U_sha11NotPass", UserInfo.Userrole.EMPLOYEE),
            new UserInfo("Gandalf", "The White", "gandalf.white@wizard.com", "U_sha11NotPass", UserInfo.Userrole.ADMIN),
            new UserInfo("Saruman", "The White", "saruman.white@wizard.com", "g7P56_pOm", UserInfo.Userrole.ADMIN)
    };

    private <T> void saveAndLog(JpaRepository<T, ?> repo, T entity) {
        log.info(PRELOADING, repo.saveAndFlush(entity));
    }

    private Proposal generateDefaultProposal(Organisation testOrganisation) {
        Proposal testProposal = new Proposal();
        testProposal.setName("testProposal");
        testProposal.setStatus(Proposal.ProposalStatus.DENIED);
        testProposal.setDeadline(new Date());
        testProposal.setLegalDeadline(new Date());
        testProposal.setTiDeadline(new Date());
        testProposal.setBusinessContext("Dit is de zakelijke context");
        testProposal.setLegalContext("Dit is de legale context");
        testProposal.setFunctionalSetup("Dit is de functionele setup");
        testProposal.setTechnicalSetup("Dit is de technische setup");
        testProposal.setOrganisation(testOrganisation);
        return testProposal;
    }

    @Bean
    @Profile("test")
    @Autowired
    CommandLineRunner initDatabase(UserRepository userRepo,
                                   ServiceRepository serviceRepo,
                                   PackageRepository packageRepo,
                                   PackageServiceRepository packServRepo,
                                   PermissionRepository permRepo,
                                   OrganisationRepository orgRepo,
                                   ProposalRepository propRepo,
                                   ContactRepository contactRepo,
                                   ContactProposalRepository conPropRepo,
                                   ProposalServiceRepository propServRepo,
                                   MemberRepository memberRepo,
                                   FileRepository fileRepository,
                                   CertificateRepository certRepository,
                                   PasswordEncoder passwordEncoder) {
        // Onderstaande code zal dummy-waarden toevoegen aan de databank voor gebruik tijdens debuggen.

        Service testService = new Service("TestService", "Test.domain.org", "A testing service", false, new HashSet<>(), new HashSet<>());
        Package testPackage = new Package("testpackage", new HashSet<>());
        PackageService packageService = new PackageService(testPackage, testService, "Somewhere", Service.DeliveryMethod.FTP);

        Organisation testOrganisation = new Organisation("testOrganisation", "1111111111", "OVO123456", "12345", "testServiceProv");
        testOrganisation.setApproved(true);
        Organisation testOrganisation2 = new Organisation("testOrganisation2", "1111111112", "OVO321456", "12435", "testServiceProv");
        testOrganisation2.setApproved(false);

        Set<Member> membershipRelations = Set.of(
                new Member(testOrganisation, users[0], Member.MemberRole.MEMBER, true),
                new Member(testOrganisation, users[1], Member.MemberRole.MANAGER, true),
                new Member(testOrganisation, users[5], Member.MemberRole.MANAGER, true)
        );

        FileMeta testPermissionFile = new FileMeta("permissionFile");
        testPermissionFile.setFileSize(500L);
        Permission testPermission = new Permission("Permission name", "Permission beschrijving", "Hier staat een code", "www.google.com", testPermissionFile, testOrganisation);

        Proposal testProposal = generateDefaultProposal(testOrganisation);
        ProposalService testPropServ = new ProposalService(testProposal, testService, Service.DeliveryMethod.FTP, "Be");
        Contact testContact = new Contact("Jhon", "Dupont", "Jhon@Dupont.be", "056 66 75 14", testOrganisation);
        Contact testContact2 = new Contact("Gandalf", "The Grey", "gandalf.grey@wizard.com", "056 66 75 15", testOrganisation);
        ContactProposal testRelation = new ContactProposal(testProposal, testContact, ContactProposal.Contactrole.SUBMITTER);
        ContactProposal testRelation2 = new ContactProposal(testProposal, testContact2, ContactProposal.Contactrole.BUSINESS_BACKUP);
        Certificate testCertificate = new Certificate(testPermissionFile, testProposal);
        UserInfo testAdminAccount = new UserInfo("Tester", "Admin", "tester@admin.com", passwordEncoder.encode("A1aaaaaa"), UserInfo.Userrole.ADMIN);
        // Laad data in de tijdelijke repository

        // Opmerking: hier wordt saveAndFlush gebruikt. In prakijk wordt enkel .save() gebruikt. Dit is slechts voor debugging.
        return args -> {
            saveAndLog(orgRepo, testOrganisation);
            saveAndLog(orgRepo, testOrganisation2);
            log.info(PRELOADING, userRepo.saveAll(Arrays.asList(users)));
            log.info(PRELOADING, memberRepo.saveAll(membershipRelations));
            saveAndLog(fileRepository, testPermissionFile);
            saveAndLog(serviceRepo, testService);
            saveAndLog(packageRepo, testPackage);
            saveAndLog(packServRepo, packageService);
            saveAndLog(permRepo, testPermission);
            saveAndLog(contactRepo, testContact);
            saveAndLog(contactRepo, testContact2);
            saveAndLog(propRepo, testProposal);
            saveAndLog(conPropRepo, testRelation);
            saveAndLog(conPropRepo, testRelation2);
            saveAndLog(propServRepo, testPropServ);
            saveAndLog(certRepository, testCertificate);
            saveAndLog(userRepo, testAdminAccount);
        };
    }
}
