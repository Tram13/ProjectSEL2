package be.sel2.api.controllers;

import be.sel2.api.entities.FileMeta;
import be.sel2.api.exceptions.EmptyFileException;
import be.sel2.api.exceptions.UploadFailedError;
import be.sel2.api.exceptions.not_found.FileNotFoundException;
import be.sel2.api.repositories.FileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * This class controls all requests to `/files`
 */
@RestController
@RequestMapping("/files")
public class FileController {

    @Value("${file-location}")
    private String uploadedFolder;

    private final FileRepository fileRepo;
    private final Random random;
    private final SimpleDateFormat sdf;

    /**
     * This class controls all requests to `/files`
     *
     * @param fileRepo Source {@link FileRepository for this controller}
     */
    public FileController(FileRepository fileRepo) {
        this.fileRepo = fileRepo;
        this.random = new SecureRandom();
        this.sdf = new SimpleDateFormat("ddMMyyyy-hhmmss_SSS");
    }

    private String getFileName(MultipartFile file) {
        return String.format("Upload_%s_%s_%s", sdf.format(new Date()),
                random.nextInt(9), file.getOriginalFilename());
    }

    private EntityModel<FileMeta> fileToHateoas(FileMeta file) {
        return EntityModel.of(file,
                linkTo(methodOn(FileController.class)
                        .getFileById(file.getId())).withSelfRel());
    }

    // AUTH: ADMIN, EMPLOYEE, CUSTOMER
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CUSTOMER"})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<FileMeta> createFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new EmptyFileException();
        }

        FileMeta result;

        try {
            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            String fileName = getFileName(file);
            Path path = Paths.get(uploadedFolder + fileName);
            Files.write(path, bytes);

            result = new FileMeta();
            result.setFileLocation(fileName); // Dus zonder de directory!
            result.setFileSize(file.getSize());
            result = fileRepo.save(result);

        } catch (IOException e) {
            throw new UploadFailedError();
        }

        return fileToHateoas(result);
    }

    // AUTH: ADMIN, EMPLOYEE, CUSTOMER
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CUSTOMER"})
    @GetMapping("/{id}")
    public EntityModel<FileMeta> getFileById(@PathVariable Long id) {

        FileMeta file = fileRepo.findById(id)
                .orElseThrow(() -> new FileNotFoundException(id));

        return fileToHateoas(file);
    }

    // AUTH: ADMIN
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFileById(@PathVariable Long id) {
        FileMeta file = fileRepo.findById(id)
                .orElseThrow(() -> new FileNotFoundException(id));

        fileRepo.delete(file);
    }
}
