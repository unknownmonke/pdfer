package pdfer.core.web.model;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class GenerationRequest {

    private Object payload;
}