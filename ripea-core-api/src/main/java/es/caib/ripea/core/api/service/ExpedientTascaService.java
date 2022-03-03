package es.caib.ripea.core.api.service;

import es.caib.ripea.core.api.dto.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface ExpedientTascaService {

	public ExpedientTascaDto findOne(
			Long expedientPeticioId);

	public List<ExpedientTascaDto> findAmbExpedient(
			Long entitatId,
			Long expedientId);

	public List<MetaExpedientTascaDto> findAmbMetaExpedient(
			Long entitatId,
			Long metaExpedientId);

	public ExpedientTascaDto createTasca(
			Long entitatId,
			Long expedientId,
			ExpedientTascaDto expedientTasca);

	public MetaExpedientTascaDto findMetaExpedientTascaById(
			Long metaExpedientTascaId);

	public List<ExpedientTascaDto> findAmbAuthentication(
			Long entitatId, 
			PaginacioParamsDto paginacioParam);

	public long countTasquesPendents();


	public ContingutDto findTascaExpedient(
			Long entitatId,
			Long contingutId,
			Long tascaId,
			boolean ambFills,
			boolean ambVersions);


	public FitxerDto descarregar(
			Long entitatId,
			Long contingutId,
			Long tascaId,
			String versio);

	public DocumentDto createDocument(
			Long entitatId,
			Long pareId,
			Long tascaId,
			DocumentDto document,
			boolean comprovarMetaExpedient);

	public DocumentDto findDocumentById(
			Long entitatId,
			Long tascaId,
			Long documentId);

	public DocumentDto updateDocument(
			Long entitatId,
			Long tascaId,
			DocumentDto documentDto,
			boolean comprovarMetaExpedient);

	public ContingutDto deleteTascaReversible(
			Long entitatId,
			Long tascaId,
			Long contingutId) throws IOException;

	public void portafirmesEnviar(
			Long entitatId,
			Long documentId,
			String assumpte,
			PortafirmesPrioritatEnumDto prioritat,
			Date dataCaducitat,
			String[] portafirmesResponsables,
			MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSeqTipus,
			MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus,
			Long[] annexosIds,
			Long tascaId,
			String transaccioId);

	public DocumentPortafirmesDto portafirmesInfo(
			Long entitatId,
			Long tascaId,
			Long documentId);

	public void portafirmesCancelar(
			Long entitatId,
			Long tascaId,
			Long docuemntId);

	public FitxerDto convertirPdfPerFirmaClient(
			Long entitatId,
			Long tascaId,
			Long documentId);

	public String generarIdentificadorFirmaClient(
			Long entitatId,
			Long tascaId,
			Long id);

	public void processarFirmaClient(String identificador,
			String arxiuNom,
			byte[] arxiuContingut,
			Long tascaId);

	public void portafirmesReintentar(
			Long entitatId,
			Long id, 
			Long tascaId);

	public void enviarEmailCrearTasca(
			Long expedientTascaId);

	public ExpedientTascaDto canviarEstat(Long expedientTascaId,
			TascaEstatEnumDto tascaEstatEnumDto,
			String motiu);
	
	public ExpedientTascaDto updateResponsables(Long expedientTascaId, 
			String usuariCodi);

	public List<MetaExpedientTascaDto> findAmbEntitat(Long entitatId);

	public boolean publicarComentariPerExpedientTasca(
			Long entitatId,
			Long expedientTascaId,
			String text,
			String rolActual);

	public List<ExpedientTascaComentariDto> findComentarisPerTasca(Long entitatId, Long expedientTascaId);
}
