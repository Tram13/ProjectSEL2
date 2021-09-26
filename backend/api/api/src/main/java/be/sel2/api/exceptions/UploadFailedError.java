package be.sel2.api.exceptions;

public class UploadFailedError extends RuntimeException {
    public UploadFailedError() {
        super("Failed to upload file");
    }
}
