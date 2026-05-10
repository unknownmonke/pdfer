package pdfer.core.web.model;

import lombok.Getter;

@Getter
public class DownloadRequest extends GenerationRequest {

    private final String filename;

    public DownloadRequest(Object payload, String filename) {
        super(payload);
        this.filename = filename;
    }
}
