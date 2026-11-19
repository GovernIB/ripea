package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.AlertaResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "alerta")
@Getter
@Setter
@NoArgsConstructor
public class AlertaResourceEntity extends BaseAuditableEntity<AlertaResource> {

    @Column(name = "text", length = 1024, nullable = false)
    private String text;
    @Column(name = "error", length = 2048)
    private String error;
    @Column(name = "llegida", nullable = false)
    private Boolean llegida;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "contingut_id")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "contingut_alerta_fk")
    protected ContingutResourceEntity contingut;

    /**
     * El mateix contingut vist com a expedient, només per a les consultes (filtres i ordenació sobre
     * "expedient.*"). Una consulta que hi filtra només retorna les alertes de l'expedient mateix, que són
     * les que fan aparèixer la icona del llistat d'expedients i l'avís del detall.
     *
     * LIMITACIÓ CONEGUDA (acceptada el 19/11/2026: la UI REACT mai no carrega alertes de documents per aquest
     * recurs, només en fallaria una crida directa a l'API): carregar-ne una falla (PropertyAccessException),
     * perquè expedients i documents comparteixen jerarquia (ContingutResourceEntity) i Hibernate resol
     * l'associació amb el document ja carregat per "contingut". Amb @JoinFormula no falla, però Oracle
     * rebutja el LEFT JOIN sobre la subconsulta (ORA-01799) que generen els filtres i l'ordenació.
     * Sense clau forana: la columna ja en té una cap a IPA_CONTINGUT.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "contingut_id",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ExpedientResourceEntity expedient;
}
