/**
 * 
 */
package es.caib.ripea.core.helper;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.dto.AlertaDto;
import es.caib.ripea.core.api.dto.CarpetaDto;
import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExecucioMassivaContingutDto;
import es.caib.ripea.core.api.dto.ExecucioMassivaContingutDto.ExecucioMassivaEstatDto;
import es.caib.ripea.core.api.dto.ExecucioMassivaDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientTascaDto;
import es.caib.ripea.core.api.dto.InteressatAdministracioDto;
import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.dto.InteressatPersonaFisicaDto;
import es.caib.ripea.core.api.dto.InteressatPersonaJuridicaDto;
import es.caib.ripea.core.api.dto.MetaExpedientTascaDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.entity.AlertaEntity;
import es.caib.ripea.core.entity.CarpetaEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.ExecucioMassivaContingutEntity;
import es.caib.ripea.core.entity.ExpedientTascaEntity;
import es.caib.ripea.core.entity.InteressatAdministracioEntity;
import es.caib.ripea.core.entity.InteressatEntity;
import es.caib.ripea.core.entity.InteressatPersonaFisicaEntity;
import es.caib.ripea.core.entity.InteressatPersonaJuridicaEntity;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;

/**
 * Helper per a convertir entre diferents formats de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ConversioTipusHelper {

	private MapperFactory mapperFactory;

	@Autowired
	private ContingutHelper contingutHelper;
	
	public ConversioTipusHelper() {
		mapperFactory = new DefaultMapperFactory.Builder().build();
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<DateTime, Date>() {
					public Date convert(DateTime source, Type<? extends Date> destinationClass) {
						return source.toDate();
					}
				});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<ExecucioMassivaContingutEntity, ExecucioMassivaContingutDto>() {
					public ExecucioMassivaContingutDto convert(ExecucioMassivaContingutEntity source, Type<? extends ExecucioMassivaContingutDto> destinationClass) {
						ExecucioMassivaContingutDto target = new ExecucioMassivaContingutDto();
						target.setDataInici(source.getDataInici());
						target.setDataFi(source.getDataFi());
						target.setEstat(ExecucioMassivaEstatDto.valueOf(source.getEstat().name()));
						target.setError(source.getError());
						target.setOrdre(source.getOrdre());
						target.setExecucioMassiva(convertir(source.getExecucioMassiva(), ExecucioMassivaDto.class));
						if (source.getContingut() instanceof DocumentEntity)
							target.setContingut(convertir((DocumentEntity)source.getContingut(), DocumentDto.class));
						return target;
					}
				});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<AlertaEntity, AlertaDto>() {
					public AlertaDto convert(AlertaEntity source, Type<? extends AlertaDto> destinationClass) {
						AlertaDto target = new AlertaDto();
						target.setId(source.getId());
						target.setText(source.getText());
						target.setError(source.getError());
						target.setLlegida(source.getLlegida().booleanValue());
						target.setContingutId(source.getContingut().getId());
						return target;
					}
				});
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<ExpedientTascaEntity, ExpedientTascaDto>() {
					public ExpedientTascaDto convert(ExpedientTascaEntity source, Type<? extends ExpedientTascaDto> destinationClass) {
						ExpedientTascaDto target = new ExpedientTascaDto();
						target.setId(source.getId());
						target.setExpedient((ExpedientDto) contingutHelper.toContingutDto(source.getExpedient()));
						target.setMetaExpedientTasca(convertir(source.getMetaExpedientTasca(), MetaExpedientTascaDto.class));
						target.setResponsable(convertir(source.getResponsable(), UsuariDto.class));
						target.setDataInici(source.getDataInici());
						target.setDataFi(source.getDataFi());
						target.setEstat(source.getEstat());
						target.setMotiuRebuig(source.getMotiuRebuig());
						target.setCreatedBy(convertir(source.getCreatedBy(), UsuariDto.class));
						target.setDataLimit(source.getDataLimit());
						target.setShouldNotifyAboutDeadline(TascaHelper.shouldNotifyAboutDeadline(source.getDataLimit()));
						
						return target;
					}
				});
		
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<InteressatEntity, InteressatDto>() {
					public InteressatDto convert(InteressatEntity source, Type<? extends InteressatDto> destinationClass) {
						InteressatDto target = null;
						if(source instanceof HibernateProxy) {
							HibernateProxy hibernateProxy = (HibernateProxy) source;
							LazyInitializer initializer = hibernateProxy.getHibernateLazyInitializer();
							source = (InteressatEntity)initializer.getImplementation();
						}
						if (source instanceof  InteressatAdministracioEntity) {
							target = new InteressatAdministracioDto();
							((InteressatAdministracioDto)target).setOrganCodi(((InteressatAdministracioEntity)source).getOrganCodi());
							((InteressatAdministracioDto)target).setOrganNom(((InteressatAdministracioEntity)source).getOrganNom());
						} else if (source instanceof  InteressatPersonaFisicaEntity) {
							target = new InteressatPersonaFisicaDto();
							((InteressatPersonaFisicaDto)target).setNom(((InteressatPersonaFisicaEntity)source).getNom());
							((InteressatPersonaFisicaDto)target).setLlinatge1(((InteressatPersonaFisicaEntity)source).getLlinatge1());
							((InteressatPersonaFisicaDto)target).setLlinatge2(((InteressatPersonaFisicaEntity)source).getLlinatge2());
						} else if (source instanceof  InteressatPersonaJuridicaEntity) {
							target = new InteressatPersonaJuridicaDto();
							((InteressatPersonaJuridicaDto)target).setRaoSocial(((InteressatPersonaJuridicaEntity)source).getRaoSocial());
						} 
						target.setId(source.getId());
						target.setDocumentNum(source.getDocumentNum());
						target.setPais(source.getPais());
						target.setProvincia(source.getProvincia());
						target.setMunicipi(source.getMunicipi());
						target.setAdresa(source.getAdresa());
						target.setCodiPostal(source.getCodiPostal());
						target.setEmail(source.getEmail());
						target.setTelefon(source.getTelefon());
						target.setObservacions(source.getObservacions());
						target.setPreferenciaIdioma(source.getPreferenciaIdioma());
						target.setNotificacioAutoritzat(source.isNotificacioAutoritzat());
						target.setRepresentantId(source.getRepresentantId());
						target.setRepresentantIdentificador(source.getRepresentantIdentificador());
						target.setEntregaDeh(source.getEntregaDeh());
						target.setEntregaDehObligat(source.getEntregaDehObligat());
						target.setIncapacitat(source.getIncapacitat());
						target.setRepresentant(source.getRepresentant() != null ? convertir(source.getRepresentant(),InteressatDto.class) : null);
						return target;
					}
				});
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<CarpetaEntity, ContingutDto>() {
					@Override
					public CarpetaDto convert(CarpetaEntity source, Type<? extends ContingutDto> destinationType) {
						CarpetaDto target = new CarpetaDto();
						if(source instanceof HibernateProxy) {
							HibernateProxy hibernateProxy = (HibernateProxy) source;
							LazyInitializer initializer = hibernateProxy.getHibernateLazyInitializer();
							source = (CarpetaEntity)initializer.getImplementation();
						}
						target.setArxiuDataActualitzacio(source.getArxiuDataActualitzacio());
						target.setArxiuUuid(source.getArxiuUuid());
						target.setCreatedBy(convertir(
								source.getCreatedBy(), 
								UsuariDto.class));
						target.setCreatedDate(source.getCreatedDate().toDate());
						target.setEntitat(convertir(
								source.getEntitat(),
								EntitatDto.class));
						target.setEsborrat(source.getEsborrat());
						target.setEsborratData(source.getEsborratData());
						target.setId(source.getId());
						target.setLastModifiedBy(convertir(
								source.getLastModifiedBy(), 
								UsuariDto.class));
						target.setLastModifiedDate(source.getLastModifiedDate().toDate());
						target.setNom(source.getNom());
						return target;
					}
				});
	}


	public <T> T convertir(Object source, Class<T> targetType) {
		if (source == null)
			return null;
		return getMapperFacade().map(source, targetType);
	}
	public <T> List<T> convertirList(List<?> items, Class<T> targetType) {
		if (items == null)
			return null;
		return getMapperFacade().mapAsList(items, targetType);
	}
	public <T> Set<T> convertirSet(Set<?> items, Class<T> targetType) {
		if (items == null)
			return null;
		return getMapperFacade().mapAsSet(items, targetType);
	}



	private MapperFacade getMapperFacade() {
		return mapperFactory.getMapperFacade();
	}

}
