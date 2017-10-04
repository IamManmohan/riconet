package dto;

import com.rivigo.zoom.common.enums.DEPSDocumentType;
import com.rivigo.zoom.common.model.DEPSDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Ankit on 8/31/16.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class DEPSDocumentDTO {

    private Long id;

    private Long depsId;

    private DEPSDocumentType depsDocumentType;

    private String documentUrl;

    private MultipartFile multipartFile;

    public DEPSDocumentDTO(DEPSDocument dEPSDocument){
        this.id = dEPSDocument.getId();
        this.depsId = dEPSDocument.getDepsId();
        this.depsDocumentType = dEPSDocument.getDepsDocumentType();
        this.documentUrl = dEPSDocument.getDocumentUrl();
    }
}
