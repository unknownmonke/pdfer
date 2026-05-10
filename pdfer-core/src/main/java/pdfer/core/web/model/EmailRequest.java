package pdfer.core.web.model;

import lombok.Getter;
import pdfer.core.mail.model.Email;

@Getter
public class EmailRequest extends GenerationRequest {

    private final Email email;

    public EmailRequest(Object payload, Email email) {
        super(payload);
        this.email = email;
    }
}
