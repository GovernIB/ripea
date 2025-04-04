package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.service.intf.dto.DocumentPublicacioTipusEnumDto;
import es.caib.ripea.service.intf.model.DocumentPublicacioResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("DocumentPublicacioEntity")
public class DocumentPublicacioResourceEntity extends DocumentEnviamentResourceEntity<DocumentPublicacioResource> {

    @Column(name = "pub_tipus")
    private DocumentPublicacioTipusEnumDto tipus;

}