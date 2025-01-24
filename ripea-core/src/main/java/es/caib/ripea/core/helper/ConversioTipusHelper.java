/**
 * 
 */
package es.caib.ripea.core.helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import es.caib.distribucio.rest.client.integracio.domini.InteressatTipus;
import es.caib.ripea.core.aggregation.HistoricAggregation;
import es.caib.ripea.core.aggregation.HistoricExpedientAggregation;
import es.caib.ripea.core.aggregation.HistoricUsuariAggregation;
import es.caib.ripea.core.api.dto.AlertaDto;
import es.caib.ripea.core.api.dto.CarpetaDto;
import es.caib.ripea.core.api.dto.CodiValorDto;
import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExecucioMassivaContingutDto;
import es.caib.ripea.core.api.dto.ExecucioMassivaDto;
import es.caib.ripea.core.api.dto.ExecucioMassivaEstatDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioEstatPendentDistribucioEnumDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioListDto;
import es.caib.ripea.core.api.dto.ExpedientTascaDto;
import es.caib.ripea.core.api.dto.InteressatAdministracioDto;
import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.dto.InteressatPersonaFisicaDto;
import es.caib.ripea.core.api.dto.InteressatPersonaJuridicaDto;
import es.caib.ripea.core.api.dto.ItemValidacioTascaEnum;
import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.dto.MetaDadaTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaExpedientTascaDto;
import es.caib.ripea.core.api.dto.MetaExpedientTascaValidacioDto;
import es.caib.ripea.core.api.dto.NtiTipoDocumentoEnumDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.dto.PermisOrganGestorDto;
import es.caib.ripea.core.api.dto.PrioritatEnumDto;
import es.caib.ripea.core.api.dto.RegistreAnnexDto;
import es.caib.ripea.core.api.dto.RegistreDto;
import es.caib.ripea.core.api.dto.SeguimentArxiuPendentsDto;
import es.caib.ripea.core.api.dto.SeguimentConsultaPinbalDto;
import es.caib.ripea.core.api.dto.SeguimentDto;
import es.caib.ripea.core.api.dto.SicresTipoDocumentoEnumDto;
import es.caib.ripea.core.api.dto.SicresValidezDocumentoEnumDto;
import es.caib.ripea.core.api.dto.TipusDocumentalDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.dto.config.OrganConfigDto;
import es.caib.ripea.core.api.dto.historic.HistoricExpedientDto;
import es.caib.ripea.core.api.dto.historic.HistoricInteressatDto;
import es.caib.ripea.core.api.dto.historic.HistoricUsuariDto;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.core.entity.AlertaEntity;
import es.caib.ripea.core.entity.CarpetaEntity;
import es.caib.ripea.core.entity.ConsultaPinbalEntity;
import es.caib.ripea.core.entity.DadaEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.DocumentNotificacioEntity;
import es.caib.ripea.core.entity.DocumentPortafirmesEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExecucioMassivaContingutEntity;
import es.caib.ripea.core.entity.ExecucioMassivaEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientPeticioEntity;
import es.caib.ripea.core.entity.ExpedientTascaEntity;
import es.caib.ripea.core.entity.InteressatAdministracioEntity;
import es.caib.ripea.core.entity.InteressatEntity;
import es.caib.ripea.core.entity.InteressatPersonaFisicaEntity;
import es.caib.ripea.core.entity.InteressatPersonaJuridicaEntity;
import es.caib.ripea.core.entity.MetaDadaEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.MetaExpedientTascaEntity;
import es.caib.ripea.core.entity.MetaExpedientTascaValidacioEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.entity.PinbalServeiEntity;
import es.caib.ripea.core.entity.RegistreAnnexEntity;
import es.caib.ripea.core.entity.RegistreInteressatEntity;
import es.caib.ripea.core.entity.TipusDocumentalEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.entity.config.ConfigEntity;
import es.caib.ripea.core.repository.MetaDadaRepository;
import es.caib.ripea.core.repository.MetaDocumentRepository;
import es.caib.ripea.core.repository.OrganGestorRepository;
import es.caib.ripea.core.repository.PinbalServeiRepository;
import es.caib.ripea.core.repository.TipusDocumentalRepository;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
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

	@Autowired private ContingutHelper contingutHelper;
	@Autowired private OrganGestorRepository organGestorRepository;
	@Autowired private MetaDadaRepository metaDadaRepository;
	@Autowired private MetaDocumentRepository metaDocumentRepository;
	@Autowired private OrganGestorHelper organGestorHelper;
	@Autowired private TascaHelper tascaHelper;
	@Autowired private MessageHelper messageHelper;
	@Autowired private TipusDocumentalRepository tipusDocumentalRepository;
	@Autowired private PinbalServeiRepository pinbalServeiRepository;
	
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
						target.setElementNom(source.getElementNom());
						target.setElementTipus(source.getElementTipus());
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
		
	      mapperFactory.classMap(MetaExpedientTascaValidacioEntity.class, MetaExpedientTascaValidacioDto.class) 
	        .byDefault()
	        .customize(new CustomMapper<MetaExpedientTascaValidacioEntity, MetaExpedientTascaValidacioDto>() {
	        		@Override
					public void mapAtoB(MetaExpedientTascaValidacioEntity source, MetaExpedientTascaValidacioDto dest, MappingContext context) {
						if (ItemValidacioTascaEnum.DADA.equals(source.getItemValidacio())) {
							dest.setItemNom(metaDadaRepository.findOne(source.getItemId()).getNom());
						} else {
							dest.setItemNom(metaDocumentRepository.findOne(source.getItemId()).getNom());
						}
					}
				})
	        .register();
		
		/*mapperFactory.getConverterFactory()..registerConverter(
				new CustomConverter<MetaExpedientTascaValidacioEntity, MetaExpedientTascaValidacioDto>() {
					public MetaExpedientTascaValidacioDto convert(MetaExpedientTascaValidacioEntity source, Type<? extends MetaExpedientTascaValidacioDto> destinationClass) {
						MetaExpedientTascaValidacioDto target = new MetaExpedientTascaValidacioDto();
						target.setId(source.getId());
						target.setItemId(source.getItemId());
						target.setActiva(source.isActiva());
						target.setItemValidacio(source.getItemValidacio());
						target.setMetaExpedientTasca(metaExpedientTasca);
						if (ItemValidacioTascaEnum.DADA.equals(source.getItemValidacio())) {
							
						} else {
							
						}
						return target;
					}
				});		
		*/
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<PermisDto, PermisOrganGestorDto>() {
					public PermisOrganGestorDto convert(PermisDto source, Type<? extends PermisOrganGestorDto> destinationClass) {
						PermisOrganGestorDto target = new PermisOrganGestorDto();
						target.setId(source.getId());
						target.setPrincipalNom(source.getPrincipalNom());
						target.setPrincipalCodiNom(source.getPrincipalCodiNom());
						target.setPrincipalTipus(source.getPrincipalTipus());
						target.setRead(source.isRead());
						target.setWrite(source.isWrite());
						target.setCreate(source.isCreate());
						target.setDelete(source.isDelete());
						target.setProcedimentsComuns(source.isProcedimentsComuns());
						target.setAdministration(source.isAdministration());
						target.setAdministrationComuns(source.isAdministrationComuns());
						target.setDisseny(source.isDisseny());
						target.setOrganGestorId(source.getOrganGestorId());
						target.setOrganGestorNom(source.getOrganGestorNom());
						return target;
					}
				});

		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<ExpedientTascaEntity, ExpedientTascaDto>() {
					public ExpedientTascaDto convert(ExpedientTascaEntity source, Type<? extends ExpedientTascaDto> destinationClass) {
						organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(source.getExpedient().getId()));
						ExpedientTascaDto target = new ExpedientTascaDto();
						target.setId(source.getId());
						target.setExpedient((ExpedientDto) contingutHelper.toContingutDto(source.getExpedient(), false, false));
						target.setMetaExpedientTasca(convertir(source.getMetaTasca(), MetaExpedientTascaDto.class));
						
						target.setCreatedBy(convertir(source.getCreatedBy(), UsuariDto.class));
						target.setResponsableActual(convertir(source.getResponsableActual(), UsuariDto.class));
						target.setResponsables(convertirList(source.getResponsables(), UsuariDto.class));
						target.setObservadors(convertirList(source.getObservadors(), UsuariDto.class));
						target.setDelegat(convertir(source.getDelegat(), UsuariDto.class));
						
						target.setDataInici(source.getDataInici());
						target.setDataFi(source.getDataFi());
						target.setTitol(source.getTitol());
						target.setObservacions(source.getObservacions());
						target.setEstat(source.getEstat());
						target.setMotiuRebuig(source.getMotiuRebuig());
						target.setDataLimit(source.getDataLimit());
						target.setShouldNotifyAboutDeadline(tascaHelper.shouldNotifyAboutDeadline(source));
						target.setNumComentaris(source.getComentaris() == null ? 0L :source.getComentaris().size());
						target.setNumComentaris(source.getComentaris() == null ? 0L :source.getComentaris().size());
						target.setDuracio(source.getDuracio());
						target.setPrioritat(source.getPrioritat()!=null?source.getPrioritat():PrioritatEnumDto.B_NORMAL);
						Authentication auth = SecurityContextHolder.getContext().getAuthentication();
						
						boolean usuariActualReposnable = false;
						for (UsuariEntity usuari : source.getResponsables()) {
							if (usuari.getCodi().equals(auth.getName())) {
								usuariActualReposnable = true;
							}
						}
						target.setUsuariActualResponsable(usuariActualReposnable);
						
						boolean usuariActualObservador = false;
						for (UsuariEntity usuari : source.getObservadors()) {
							if (usuari.getCodi().equals(auth.getName())) {
								usuariActualObservador = true;
							}
						}
						target.setUsuariActualObservador(usuariActualObservador);
						
						target.setUsuariActualDelegat(source.getDelegat() != null && source.getDelegat().getCodi().equals(auth.getName()));
						return target;
					}
				});
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<MetaExpedientTascaEntity, MetaExpedientTascaDto>() {
					public MetaExpedientTascaDto convert(MetaExpedientTascaEntity source, Type<? extends MetaExpedientTascaDto> destinationClass) {
						MetaExpedientTascaDto target = new MetaExpedientTascaDto();
						target.setActiva(source.isActiva());
						target.setCodi(source.getCodi());
						target.setDataLimit(source.getDataLimit());
						target.setDescripcio(source.getDescripcio());
						target.setEstatIdCrearTasca(source.getEstatCrearTasca() != null ? source.getEstatCrearTasca().getId() : null);
						target.setEstatNomCrearTasca(source.getEstatCrearTasca() != null ? source.getEstatCrearTasca().getNom() : null);
						target.setEstatColorCrearTasca(source.getEstatCrearTasca() != null ? source.getEstatCrearTasca().getColor() : null);
						target.setEstatIdFinalitzarTasca(source.getEstatFinalitzarTasca() != null ? source.getEstatFinalitzarTasca().getId() : null);
						target.setEstatNomFinalitzarTasca(source.getEstatFinalitzarTasca() != null ? source.getEstatFinalitzarTasca().getNom() : null);
						target.setEstatColorFinalitzarTasca(source.getEstatFinalitzarTasca() != null ? source.getEstatFinalitzarTasca().getColor() : null);
						target.setId(source.getId());
						target.setNom(source.getNom());
						target.setResponsable(source.getResponsable());
						target.setDuracio(source.getDuracio());
						target.setPrioritat(source.getPrioritat()!=null?source.getPrioritat():PrioritatEnumDto.B_NORMAL);
						return target;
					}
				});
		
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<InteressatEntity, InteressatDto>() {
					public InteressatDto convert(InteressatEntity source, Type<? extends InteressatDto> destinationClass) {
						source = deproxyInteressatEntity(source);
						return getInteressatDto(source);
					}
				});
		
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<MetaDadaEntity, MetaDadaDto>() {
					public MetaDadaDto convert(MetaDadaEntity source, Type<? extends MetaDadaDto> destinationClass) {
						MetaDadaDto target = new MetaDadaDto();
						target.setId(source.getId());
						target.setCodi(source.getCodi());
						target.setNom(source.getNom());
						target.setTipus(source.getTipus());
						target.setDescripcio(source.getDescripcio());
						target.setMultiplicitat(source.getMultiplicitat());
						target.setReadOnly(source.isReadOnly());
						target.setOrdre(source.getOrdre());
						target.setActiva(source.isActiva());
						target.setNoAplica(source.isNoAplica());
						target.setEnviable(source.isEnviable());
						target.setMetadadaArxiu(source.getMetadadaArxiu());
						
						if (source.getTipus()==MetaDadaTipusEnumDto.BOOLEA) {
							target.setValorBoolea((Boolean) DadaEntity.getDadaValorPerRetornar(source, source.getValor()));
						} else if (source.getTipus()==MetaDadaTipusEnumDto.DATA) {
							target.setValorData((Date) DadaEntity.getDadaValorPerRetornar(source, source.getValor()));
						} else if (source.getTipus()==MetaDadaTipusEnumDto.FLOTANT) {
							target.setValorFlotant((Double) DadaEntity.getDadaValorPerRetornar(source, source.getValor()));
						} else if (source.getTipus()==MetaDadaTipusEnumDto.IMPORT) {
							target.setValorImport((BigDecimal)DadaEntity.getDadaValorPerRetornar(source, source.getValor()));
						} else if (source.getTipus()==MetaDadaTipusEnumDto.SENCER) {
							target.setValorSencer((Long) DadaEntity.getDadaValorPerRetornar(source, source.getValor()));
						}  else if (source.getTipus()==MetaDadaTipusEnumDto.TEXT) {
							target.setValorString((String) DadaEntity.getDadaValorPerRetornar(source, source.getValor()));
						}						
						
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
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<OrganGestorEntity, Long>() {
					@Override
					public Long convert(OrganGestorEntity source, Type<? extends Long> destinationType) {
						return source.getId();
					}
				});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<EntitatEntity, Long>() {
					@Override
					public Long convert(EntitatEntity source, Type<? extends Long> destinationType) {
						return source.getId();
					}
				});	
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<HistoricExpedientAggregation, HistoricExpedientDto>() {
					@Override
					public HistoricExpedientDto convert(HistoricExpedientAggregation source, Type<? extends HistoricExpedientDto> destinationType) {
						HistoricExpedientDto target = new HistoricExpedientDto();
						target.setData(source.getData());
						target.setNumExpedientsCreats(source.getNumExpedientsCreats());
						target.setNumExpedientsCreatsTotal(source.getNumExpedientsCreatsTotal());
						target.setNumExpedientsTancats(source.getNumExpedientsTancats());
						target.setNumExpedientsTancatsTotal(source.getNumExpedientsTancatsTotal());
						target.setNumDocsSignats(source.getNumDocsSignats());
						target.setNumDocsNotificats(source.getNumDocsNotificats());
						return target;
					}
				});	
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<HistoricUsuariAggregation, HistoricUsuariDto>() {
					@Override
					public HistoricUsuariDto convert(HistoricUsuariAggregation source, Type<? extends HistoricUsuariDto> destinationType) {
						HistoricUsuariDto target = new HistoricUsuariDto(null, null);
						target.setData(source.getData());
						target.setNumExpedientsCreats(source.getNumExpedientsCreats());
						target.setNumExpedientsCreatsTotal(source.getNumExpedientsCreatsTotal());
						target.setNumExpedientsTancats(source.getNumExpedientsTancats());
						target.setNumExpedientsTancatsTotal(source.getNumExpedientsTancatsTotal());
						target.setNumTasquesTramitades(source.getNumTasquesTramitades());
						target.setUsuariCodi(source.getUsuari().getCodi());
						
						return target;
					}
				});	
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<HistoricAggregation, HistoricInteressatDto>() {
					@Override
					public HistoricInteressatDto convert(HistoricAggregation source, Type<? extends HistoricInteressatDto> destinationType) {
						HistoricInteressatDto target = new HistoricInteressatDto(null, null);
						target.setData(source.getData());
						target.setNumExpedientsCreats(source.getNumExpedientsCreats());
						target.setNumExpedientsCreatsTotal(source.getNumExpedientsCreatsTotal());
						target.setNumExpedientsTancats(source.getNumExpedientsTancats());
						target.setNumExpedientsTancatsTotal(source.getNumExpedientsTancatsTotal());
						return target;
					}
				});	
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<ExpedientPeticioEntity, ExpedientPeticioDto>() {
					@Override
					public ExpedientPeticioDto convert(ExpedientPeticioEntity source, Type<? extends ExpedientPeticioDto> destinationType) {
						ExpedientPeticioDto target = new ExpedientPeticioDto();
						target.setId(source.getId());
						target.setRegistre(convertir(source.getRegistre(), RegistreDto.class));
						target.setMetaExpedientId(source.getMetaExpedient() != null ? source.getMetaExpedient().getId() : null);
						target.setMetaExpedientNom(source.getMetaExpedient() != null ? source.getMetaExpedient().getNom() : null);
						target.setEstat(source.getEstat());
						target.setIdentificador(source.getIdentificador());
						target.setExpedientId(source.getExpedient() != null ? source.getExpedient().getId() : null);
						target.setNotificaDistError(source.getNotificaDistError());
						target.setDataAlta(source.getDataAlta());
						target.setDataActualitzacio(source.getDataActualitzacio());
						if (target.getDataActualitzacio() == null && source.getLastModifiedDate() != null) {
							target.setDataActualitzacio(source.getLastModifiedDate().toDate());
						}
						target.setUsuariActualitzacio(source.getUsuariActualitzacio() != null ? source.getUsuariActualitzacio().getCodiAndNom() : "");
						if ("".equals(target.getUsuariActualitzacio()) && source.getLastModifiedBy() != null) {
							target.setUsuariActualitzacio(source.getLastModifiedBy().getCodiAndNom());
						}
						target.setObservacions(source.getObservacions());
						target.setGrupId(source.getGrup() != null ? source.getGrup().getId() : null);
						
						return target;
					}
				});
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<ExpedientPeticioEntity, ExpedientPeticioListDto>() {
					@Override
					public ExpedientPeticioListDto convert(ExpedientPeticioEntity source, Type<? extends ExpedientPeticioListDto> destinationType) {
						ExpedientPeticioListDto target = new ExpedientPeticioListDto();
						target.setId(source.getId());
						RegistreDto registre = new RegistreDto();
						if (source.getRegistre() != null) {
							registre.setIdentificador(source.getRegistre().getIdentificador());
							registre.setData(source.getRegistre().getData());
							registre.setExtracte(source.getRegistre().getExtracte());
							registre.setDestiCodi(source.getRegistre().getDestiCodi());
							registre.setDestiDescripcio(source.getRegistre().getDestiDescripcio());
							registre.setProcedimentCodi(source.getRegistre().getProcedimentCodi());
							registre.setOrigenRegistreNumero(source.getRegistre().getOrigenRegistreNumero());
							target.setAnotacioId(source.getRegistre().getId());
							
						}
						target.setRegistre(registre);
						target.setMetaExpedientId(source.getMetaExpedient() != null ? source.getMetaExpedient().getId() : null);
						target.setMetaExpedientNom(source.getMetaExpedient() != null ? source.getMetaExpedient().getNom() : null);
						target.setProcedimentCodi(source.getMetaExpedient() != null ? source.getMetaExpedient().getClassificacio() : null);
						target.setEstat(source.getEstat());
						target.setIdentificador(source.getIdentificador());
						target.setExpedientId(source.getExpedient() != null ? source.getExpedient().getId() : null);
						target.setPendentEnviarDistribucio(source.isPendentCanviEstatDistribucio());
						target.setDataAlta(source.getDataAlta());
						
						target.setConsultaWsError(source.isConsultaWsError());
						target.setConsultaWsErrorDesc(source.getConsultaWsErrorDesc());
						target.setConsultaWsErrorDate(source.getConsultaWsErrorDate());
						target.setPendentCanviEstatDistribucio(source.isPendentCanviEstatDistribucio());
						target.setReintentsCanviEstatDistribucio(source.getReintentsCanviEstatDistribucio());
						target.setDataActualitzacio(source.getDataActualitzacio());
						if (target.getDataActualitzacio() == null && source.getLastModifiedDate() != null) {
							target.setDataActualitzacio(source.getLastModifiedDate().toDate());
						}
						
						target.setGrupNom(source.getGrup() != null ? source.getGrup().getDescripcio() : null);
						
						ExpedientPeticioEstatPendentDistribucioEnumDto estatPendentEnviarDistribucio = null;
						switch (source.getEstat()) {
//						case CREAT:
//							estatPendentEnviarDistribucio = ExpedientPeticioEstatPendentDistribucioEnumDto.CONSULTA_ERROR;
//							break;
						case PENDENT:
							estatPendentEnviarDistribucio = ExpedientPeticioEstatPendentDistribucioEnumDto.PENDENT;
							break;
						case PROCESSAT_PENDENT:
						case PROCESSAT_NOTIFICAT:
							estatPendentEnviarDistribucio = ExpedientPeticioEstatPendentDistribucioEnumDto.ACCEPTAT;
							break;
						case REBUTJAT:
							estatPendentEnviarDistribucio = ExpedientPeticioEstatPendentDistribucioEnumDto.REBUTJAT;
							break;
						}
						target.setEstatPendentEnviarDistribucio(estatPendentEnviarDistribucio);
						
						String interessatsResum = "";
						if (source.getRegistre() != null && source.getRegistre().getInteressats() != null)
							for (RegistreInteressatEntity interessat : source.getRegistre().getInteressats()) {
								if (interessat.getTipus() == InteressatTipus.PERSONA_FISICA) {
									interessatsResum += interessat.getNom() == null ? "" : interessat.getNom() + " ";
									interessatsResum += interessat.getLlinatge1() == null ? "" : interessat.getLlinatge1() + " ";
									interessatsResum += interessat.getLlinatge2() == null ? "" : interessat.getLlinatge2() + " ";
									interessatsResum += "(" + interessat.getDocumentNumero() + ")" + "<br>";
								} else if (interessat.getTipus() == InteressatTipus.PERSONA_JURIDICA) {
									interessatsResum += interessat.getRaoSocial() + " ";
									interessatsResum += "(" + interessat.getDocumentNumero() + ")" + "<br>";
								} else if (interessat.getTipus() == InteressatTipus.ADMINISTRACIO) {
									interessatsResum += interessat.getRaoSocial() + " ";
									interessatsResum += "(" + interessat.getDocumentNumero() + ")" + "<br>";
								}
							}
						target.setInteressatsResum(interessatsResum);
						
						
						return target;
					}
				});
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<DocumentPortafirmesEntity, SeguimentDto>() {
					@Override
					public SeguimentDto convert(DocumentPortafirmesEntity source, Type<? extends SeguimentDto> destinationType) {
						SeguimentDto target = new SeguimentDto();
						target.setId(source.getId());
						target.setExpedientId(source.getExpedient().getId());
						target.setExpedientNom(source.getExpedient().getNom());
						target.setDocumentId(source.getDocument().getId());
						target.setDocumentNom(source.getDocument().getNom());
						target.setPortafirmesEstat(source.getEstat());
						target.setDataEnviament(source.getEnviatData());
						target.setDataFinalitzacio(source.getProcessatData());
						target.setConcepte(source.getAssumpte());
						return target;
					}
				});	
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<DocumentNotificacioEntity, SeguimentDto>() {
					@Override
					public SeguimentDto convert(DocumentNotificacioEntity source, Type<? extends SeguimentDto> destinationType) {
						SeguimentDto target = new SeguimentDto();
						target.setId(source.getId());
						target.setExpedientId(source.getExpedient().getId());
						target.setExpedientNom(source.getExpedient().getNom());
						target.setDocumentId(source.getDocument().getId());
						target.setDocumentNom(source.getDocument().getNom());
						target.setNotificacioEstat(source.getNotificacioEstat());
						target.setDataEnviament(source.getCreatedDate().toDate());
						target.setNotificacioIdentificador(source.getNotificacioIdentificador());
						target.setOrgan(source.getExpedient().getOrganGestor().getCodiINom());
						target.setProcediment(source.getExpedient().getMetaExpedient().getCodiSiaINom());
						target.setConcepte(source.getAssumpte());
						target.setDataFinalitzacio(source.getProcessatData());
						target.setError(source.isError());
						if (Utils.isNotEmpty(source.getDocumentEnviamentInteressats())) {
							String enviamentDatatEstat  = source.getDocumentEnviamentInteressats().iterator().next().getEnviamentDatatEstat();
							target.setEnviamentDatatEstat(enviamentDatatEstat);
						}
						
						
						
						InteressatEntity destinatari = !source.getDocumentEnviamentInteressats().isEmpty() ? HibernateHelper.deproxy(source.getDocumentEnviamentInteressats().iterator().next().getInteressat()) : null;
						String destinatariNom = "";
						if (destinatari != null) {
							if (destinatari instanceof  InteressatAdministracioEntity) {
								InteressatAdministracioEntity adm = (InteressatAdministracioEntity) destinatari;
								destinatariNom = adm.getOrganCodi() + " - " + adm.getOrganNom();
							} else if (destinatari instanceof  InteressatPersonaFisicaEntity) {
								InteressatPersonaFisicaEntity fis = (InteressatPersonaFisicaEntity) destinatari;
								destinatariNom = fis.getNom() + " " + fis.getLlinatge1() + " " + (fis.getLlinatge2() != null ? fis.getLlinatge2() : "");
							} else if (destinatari instanceof  InteressatPersonaJuridicaEntity) {
								InteressatPersonaJuridicaEntity jur = (InteressatPersonaJuridicaEntity) destinatari;
								destinatariNom = jur.getRaoSocial();
							} 
						}
						target.setDestinataris(destinatariNom);
						return target;
					}
				});	
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<ExpedientTascaEntity, SeguimentDto>() {
					@Override
					public SeguimentDto convert(ExpedientTascaEntity source, Type<? extends SeguimentDto> destinationType) {
						SeguimentDto target = new SeguimentDto();
						target.setId(source.getId());
						target.setExpedientId(source.getExpedient().getId());
						target.setExpedientNom(source.getExpedient().getNom());
						target.setTascaNom(source.getMetaTasca().getNom());
						target.setData(source.getDataInici());
						List<String> responsablesNom = new ArrayList<String>();
						for (UsuariEntity responsable: source.getResponsables()) {
							responsablesNom.add(responsable.getNom());
						}
						target.setResponsablesNom(StringUtils.join(responsablesNom, ","));
						if (source.getResponsableActual() != null)
							target.setResponsableActualNom(source.getResponsableActual().getNom());
						target.setTascaEstat(source.getEstat());
						return target;
					}
				});	
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<ExpedientEntity, SeguimentArxiuPendentsDto>() {
					@Override
					public SeguimentArxiuPendentsDto convert(ExpedientEntity source, Type<? extends SeguimentArxiuPendentsDto> destinationType) {
						SeguimentArxiuPendentsDto target = new SeguimentArxiuPendentsDto();
						target.setId(source.getId());
						target.setElementNom(source.getNom());
						target.setExpedientNumeroNom(source.getNom() + " (" + source.getNumero() + ")");
						target.setMetaExpedientCodiNom(source.getMetaExpedient() != null ? source.getMetaExpedient().getClassificacio() + " - " + source.getMetaExpedient().getNom() : null);
						target.setCreatedDate(source.getCreatedDate().toDate());
						target.setDataDarrerIntent(source.getArxiuIntentData());
						return target;
					}
				});	
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<DocumentEntity, SeguimentArxiuPendentsDto>() {
					@Override
					public SeguimentArxiuPendentsDto convert(DocumentEntity source, Type<? extends SeguimentArxiuPendentsDto> destinationType) {
						SeguimentArxiuPendentsDto target = new SeguimentArxiuPendentsDto();
						target.setId(source.getId());
						target.setExpedientId(source.getExpedient().getId());
						target.setElementNom(source.getNom());
						target.setExpedientNumeroNom(source.getExpedient().getNom() + " (" + source.getExpedient().getNumero() + ")");
						target.setMetaExpedientCodiNom(source.getExpedient().getMetaExpedient() != null ? source.getExpedient().getMetaExpedient().getClassificacio() + " - " + source.getExpedient().getMetaExpedient().getNom() : null);
						target.setCreatedDate(source.getCreatedDate().toDate());
						target.setDataDarrerIntent(source.getArxiuIntentData());
						target.setExpedientArxiuPropagat(source.getExpedient().getArxiuUuid() != null);
						target.setAnnex(source.getAnnexos() != null && !source.getAnnexos().isEmpty());
						return target;
					}
				});	
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<InteressatEntity, SeguimentArxiuPendentsDto>() {
					@Override
					public SeguimentArxiuPendentsDto convert(InteressatEntity source, Type<? extends SeguimentArxiuPendentsDto> destinationType) {
						SeguimentArxiuPendentsDto target = new SeguimentArxiuPendentsDto();
						target.setId(source.getId());
						target.setExpedientId(source.getExpedient().getId());
						if (source instanceof  InteressatAdministracioEntity) {
							target.setElementNom(((InteressatAdministracioEntity)source).getOrganNom());
						} else if (source instanceof  InteressatPersonaFisicaEntity) {
							InteressatPersonaFisicaEntity fis = (InteressatPersonaFisicaEntity)source;
							target.setElementNom(fis.getNom() + " " + fis.getLlinatge1() + " " + fis.getLlinatge2());
						} else if (source instanceof  InteressatPersonaJuridicaEntity) {
							target.setElementNom(((InteressatPersonaJuridicaEntity)source).getRaoSocial());
						} 
						target.setExpedientNumeroNom(source.getExpedient().getNom() + " (" + source.getExpedient().getNumero() + ")");
						target.setMetaExpedientCodiNom(source.getExpedient().getMetaExpedient() != null ? source.getExpedient().getMetaExpedient().getClassificacio() + " - " + source.getExpedient().getMetaExpedient().getNom() : null);
						target.setCreatedDate(source.getCreatedDate().toDate());
						target.setDataDarrerIntent(source.getArxiuIntentData());
						target.setExpedientArxiuPropagat(source.getExpedient().getArxiuUuid() != null);
						return target;
					}
				});

		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<ExecucioMassivaEntity, ExecucioMassivaDto>() {
					@Override
					public ExecucioMassivaDto convert(ExecucioMassivaEntity source, Type<? extends ExecucioMassivaDto> destinationType) {
						ExecucioMassivaDto target = new ExecucioMassivaDto();
						target.setId(source.getId());
						target.setTipus(source.getTipus());
						target.setDataInici(source.getDataInici());
						target.setDataFi(source.getDataFi());
						target.setMotiu(source.getMotiu());
						target.setPrioritat(source.getPrioritat());
						target.setDataCaducitat(source.getDataCaducitat());
						target.setPortafirmesResponsables(source.getPortafirmesResponsables() != null ? source.getPortafirmesResponsables().split(",") : null);
						target.setPortafirmesSequenciaTipus(source.getPortafirmesSequenciaTipus());
						target.setPortafirmesFluxId(source.getPortafirmesFluxId());
						target.setPortafirmesTransaccioId(source.getPortafirmesTransaccioId());
						target.setEnviarCorreu(source.getEnviarCorreu());
						target.setCreatedBy(convertir(source.getCreatedBy(), UsuariDto.class));
						return target;
					}
				});
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<ExpedientEntity, CodiValorDto>() {
					@Override
					public CodiValorDto convert(ExpedientEntity source, Type<? extends CodiValorDto> destinationClass) {
						CodiValorDto target = new CodiValorDto();
						target.setCodi(source.getId().toString());
						target.setValor(source.getNom());
						return target;
					}
				});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<OrganGestorEntity, OrganGestorDto>() {
					@Override
					public OrganGestorDto convert(OrganGestorEntity source, Type<? extends OrganGestorDto> destinationClass) {
						OrganGestorDto target = new OrganGestorDto();
						target.setId(source.getId());
						target.setCodi(source.getCodi());
						target.setNom(source.getNom());
						target.setEntitatId(source.getEntitat() != null ? source.getEntitat().getId().toString() : null);
						target.setEntitatNom(source.getEntitat() != null ? source.getEntitat().getNom() : null);
						target.setEntitatCodi(source.getEntitat() != null ? source.getEntitat().getCodi() : null);
						target.setPareId(source.getPare() != null ? source.getPare().getId() : null);
						target.setPareCodi(source.getPare() != null ? source.getPare().getCodi() : null);
						target.setPareNom(source.getPare() != null ? source.getPare().getNom() : null);
						target.setEstat(source.getEstat());
						target.setTipusTransicio(source.getTipusTransicio());
						target.setCif(source.getCif());
						target.setUtilitzarCifPinbal(source.isUtilitzarCifPinbal());
						try {
							if (target.getEstat()!=null)
								target.setEstatMessage(messageHelper.getMessage("OrganEstatEnumDto."+target.getEstat()));
						} catch (Exception ex) {}
						return target;
					}
				});
		
		
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<RegistreAnnexEntity, RegistreAnnexDto>() {
					@Override
					public RegistreAnnexDto convert(RegistreAnnexEntity source, Type<? extends RegistreAnnexDto> destinationClass) {
						RegistreAnnexDto target = new RegistreAnnexDto();

						target.setId(source.getId());
						target.setFirmaPerfil(source.getFirmaPerfil() != null ? source.getFirmaPerfil().toString() : null);
						target.setFirmaTipus(source.getFirmaTipus() != null ? source.getFirmaTipus().toString() : null);
						target.setNtiFechaCaptura(source.getNtiFechaCaptura());
						target.setNtiOrigen(source.getNtiOrigen() != null ? source.getNtiOrigen().toString() : null);
						target.setNtiTipoDocumental(Utils.convertEnum(source.getNtiTipoDocumental(), NtiTipoDocumentoEnumDto.class));
						
						target.setNtiEstadoElaboracion(Utils.toString(source.getNtiEstadoElaboracion()));
						
						target.setObservacions(source.getObservacions());
						
						target.setSicresTipoDocumento(Utils.convertEnum(source.getSicresTipoDocumento(), SicresTipoDocumentoEnumDto.class));
						target.setSicresValidezDocumento(Utils.convertEnum(source.getSicresValidezDocumento(), SicresValidezDocumentoEnumDto.class));
						target.setTamany(source.getTamany());
						target.setTipusMime(source.getTipusMime());
						target.setTitol(source.getTitol());
						target.setNom(source.getNom());
						target.setUuid(source.getUuid());
						target.setCreatedDate(source.getCreatedDate() != null ? source.getCreatedDate().toDate() : null);
						target.setEstat(source.getEstat());
						target.setError(source.getError());

						target.setRegistreNumero(source.getRegistre().getIdentificador());
						ExpedientEntity expedient = source.getRegistre().getExpedientPeticions().get(0).getExpedient();
						if (expedient != null) {
							target.setExpedientId(expedient.getId());
							target.setExpedientNumeroNom(expedient.getNumero() + " - " + expedient.getNom());
							target.setExpedientCreatedDate(expedient.getCreatedDate().toDate());
						}
						target.setDocumentId(source.getDocument() != null ? source.getDocument().getId() : null);
						target.setExpedientPeticioId(source.getRegistre().getExpedientPeticions().get(0).getId());
						target.setValidacioFirmaCorrecte(source.isValidacioFirmaCorrecte());
						target.setValidacioFirmaErrorMsg(source.getValidacioFirmaErrorMsg());
						target.setAnnexArxiuEstat(source.getAnnexArxiuEstat());
						
						return target;
					}
				});
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<ConfigEntity, OrganConfigDto>() {
					@Override
					public OrganConfigDto convert(ConfigEntity source, Type<? extends OrganConfigDto> destinationClass) {
						OrganConfigDto target = new OrganConfigDto();
						OrganGestorEntity organGestor = organGestorRepository.findByCodi(source.getOrganCodi());
						target.setOrganGestorId(organGestor.getId());
						target.setOrganGestorCodiNom(organGestor.getCodi() + " - " + organGestor.getNom() + " (" + organGestor.getEntitat().getCodi() + ")");
						
						target.setKey(source.getKey());
						target.setTypeCode(source.getTypeCode());
						target.setJbossProperty(source.isJbossProperty());
						
			            if ("PASSWORD".equals(source.getTypeCode())){
			            	target.setValue("*****");
			            } else if (source.isJbossProperty()) {
			                // Les propietats de Jboss es llegeixen del fitxer de properties i si no estan definides prenen el valor especificat a la base de dades.
			            	target.setValue(ConfigHelper.JBossPropertiesHelper.getProperties().getProperty(source.getKey(), source.getValue()));
			            } else {
			            	target.setValue(source.getValue());
			            }
						target.setConfigurableOrgansDescendents(source.isConfigurableOrgansDescendents());
	
						return target;
					}
				});
		
		
	      mapperFactory.classMap(MetaDocumentEntity.class, MetaDocumentDto.class)
	        .customize(new CustomMapper<MetaDocumentEntity,MetaDocumentDto>() {
	            @Override
	            public void mapAtoB(MetaDocumentEntity source, MetaDocumentDto target, MappingContext mappingContext) {
	            	
	            	TipusDocumentalEntity tipusDocumental = null;
					if (source.getMetaExpedient() != null) {
						tipusDocumental = tipusDocumentalRepository.findByCodiAndEntitat(source.getNtiTipoDocumental(), source.getMetaExpedient().getEntitat());
					}  else {
						tipusDocumental = tipusDocumentalRepository.findByCodi(source.getNtiTipoDocumental()).get(0);
					}
	            	
//					target.setNtiTipoDocumental(tipusDocumental.getCodiEspecific() != null ? tipusDocumental.getCodiEspecific() : tipusDocumental.getCodi());
	            	if (LocaleContextHolder.getLocale().toString().equals("ca") && Utils.isNotEmpty(tipusDocumental.getNomCatala())) {
	            		target.setNtiTipoDocumentalNom(tipusDocumental.getNomCatala());
					} else {
						target.setNtiTipoDocumentalNom(tipusDocumental.getNomEspanyol());
					}
	            }
	        })
	        .byDefault()
	        .register();
	      
	      mapperFactory.classMap(TipusDocumentalEntity.class, TipusDocumentalDto.class)
	        .customize(new CustomMapper<TipusDocumentalEntity,TipusDocumentalDto>() {
	            @Override
	            public void mapAtoB(TipusDocumentalEntity source, TipusDocumentalDto target, MappingContext mappingContext) {
	            	if (LocaleContextHolder.getLocale().toString().equals("ca") && Utils.isNotEmpty(source.getNomCatala())) {
	            		target.setNom(source.getNomCatala());
					} else {
						target.setNom(source.getNomEspanyol());
					}
	            }
	        })
	        .byDefault()
	        .register();	
	      
	      
	      mapperFactory.classMap(ConsultaPinbalEntity.class, SeguimentConsultaPinbalDto.class)
	        .customize(new CustomMapper<ConsultaPinbalEntity, SeguimentConsultaPinbalDto>() {
	            @Override
	            public void mapAtoB(ConsultaPinbalEntity source, SeguimentConsultaPinbalDto target, MappingContext mappingContext) {
	            
	            	target.setDocumentId(source.getDocument() != null ? source.getDocument().getId() : null);
	            	target.setDocumentTitol(source.getDocument() != null ? source.getDocument().getNom() : null);
	            	target.setExpedientId(source.getExpedient() != null ? source.getExpedient().getId() : null);
	            	target.setExpedientNumeroTitol(source.getExpedient() != null ? source.getExpedient().getNomINumero() : null);
	            	target.setProcedimentCodiNom(source.getMetaExpedient() != null ? source.getMetaExpedient().getCodiSiaINom() : null);
	            	target.setCreatedBy(source.getCreatedBy().getNom());
	            	target.setCreatedDate(source.getCreatedDate().toDate());
	            	target.setError(HtmlUtils.htmlEscape(source.getError()));
	            	PinbalServeiEntity pse = pinbalServeiRepository.findByCodi(source.getServei());
	            	if (pse!=null) {
	            		target.setServei(pse.getCodi() + " - " + pse.getNom());
	            	} else {
	            		target.setServei(source.getServei());
	            	}
	            }
	        })
	        .byDefault()
	        .register();
		
      
	      //if not excluded with the new version of orika 1.4.6 it gives: ma.glasnost.orika.MappingException: Encountered mapping of primitive to object (or vise-versa); sourceType=boolean, destinationType=ExpedientEntity
	      mapperFactory.classMap(DocumentEntity.class, DocumentDto.class) 
	      	.exclude("esborrat")
	      	.exclude("expedient")
	      	.exclude("document")
	      	.exclude("node")
	      	.exclude("carpeta")
	      	.exclude("escriptori")
	      	.exclude("registre")
	        .byDefault()
	        .register();	      
	      
	      mapperFactory.classMap(CarpetaEntity.class, CarpetaDto.class) 
	      	.exclude("esborrat")
	      	.exclude("expedient")
	      	.exclude("document")
	      	.exclude("node")
	      	.exclude("carpeta")
	      	.exclude("escriptori")
	      	.exclude("registre")
	        .byDefault()
	        .register();

	      mapperFactory.classMap(ExpedientEntity.class, ExpedientDto.class) 
	      	.exclude("esborrat")
	      	.exclude("expedient")
	      	.exclude("document")
	      	.exclude("node")
	      	.exclude("carpeta")
	      	.exclude("escriptori")
	      	.exclude("registre")
	      	.exclude("peticions")
	      	.exclude("tasques")
	        .byDefault()
	        .register();
	}

	private static InteressatEntity deproxyInteressatEntity(InteressatEntity source) {
		if (source == null) return null;

		if(source instanceof HibernateProxy) {
			HibernateProxy hibernateProxy = (HibernateProxy) source;
			LazyInitializer initializer = hibernateProxy.getHibernateLazyInitializer();
			source = (InteressatEntity)initializer.getImplementation();
		}
		return source;
	}

	private InteressatDto getInteressatDto(final InteressatEntity source) {
		InteressatDto target = createTargetDto(source);
		mapCommonFields(target, source);
		mapRepresentantFields(target, source);
		return target;
	}

	private InteressatDto getRepresentantDto(final InteressatEntity source) {
		InteressatDto target = createTargetDto(source);
		mapCommonFields(target, source);
		target.setEsRepresentant(true);
		return target;
	}

	private InteressatDto createTargetDto(final InteressatEntity source) {
		InteressatDto target = null;
		if (source instanceof InteressatAdministracioEntity) {
			target = new InteressatAdministracioDto();
			((InteressatAdministracioDto) target).setOrganCodi(((InteressatAdministracioEntity) source).getOrganCodi());
			((InteressatAdministracioDto) target).setOrganNom(((InteressatAdministracioEntity) source).getOrganNom());
			((InteressatAdministracioDto) target).setAmbOficinaSir(((InteressatAdministracioEntity) source).getAmbOficinaSir());
		} else if (source instanceof InteressatPersonaFisicaEntity) {
			target = new InteressatPersonaFisicaDto();
			((InteressatPersonaFisicaDto) target).setNom(((InteressatPersonaFisicaEntity) source).getNom());
			((InteressatPersonaFisicaDto) target).setLlinatge1(((InteressatPersonaFisicaEntity) source).getLlinatge1());
			((InteressatPersonaFisicaDto) target).setLlinatge2(((InteressatPersonaFisicaEntity) source).getLlinatge2());
		} else if (source instanceof InteressatPersonaJuridicaEntity) {
			target = new InteressatPersonaJuridicaDto();
			((InteressatPersonaJuridicaDto) target).setRaoSocial(((InteressatPersonaJuridicaEntity) source).getRaoSocial());
		}
		return target;
	}

	private void mapCommonFields(InteressatDto target, final InteressatEntity source) {
		target.setId(source.getId());
		target.setDocumentNum(source.getDocumentNum());
		target.setDocumentTipus(source.getDocumentTipus());
		target.setPais(source.getPais());
		target.setProvincia(source.getProvincia());
		target.setMunicipi(source.getMunicipi());
		target.setAdresa(source.getAdresa());
		target.setCodiPostal(source.getCodiPostal());
		target.setEmail(source.getEmail());
		target.setTelefon(source.getTelefon());
		target.setObservacions(source.getObservacions());
		target.setPreferenciaIdioma(source.getPreferenciaIdioma());
		target.setIdentificador(source.getIdentificador());
		target.setEntregaDeh(source.getEntregaDeh());
		target.setEntregaDehObligat(source.getEntregaDehObligat());
		target.setIncapacitat(source.getIncapacitat());
		target.setArxiuPropagat(source.isArxiuPropagat());
		target.setExpedientArxiuPropagat(source.getExpedient() != null && source.getExpedient().getArxiuUuid() != null);
	}

	private void mapRepresentantFields(InteressatDto target, final InteressatEntity source) {
		target.setEsRepresentant(source.isEsRepresentant());
		target.setRepresentantId(source.getRepresentantId());
		target.setRepresentantIdentificador(source.getRepresentantIdentificador());

		InteressatEntity representant = deproxyInteressatEntity(source.getRepresentant());
		target.setRepresentant(representant != null ? getRepresentantDto(representant) : null);
		target.setRepresentantArxiuPropagat(representant != null ? representant.isArxiuPropagat() : true);
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
