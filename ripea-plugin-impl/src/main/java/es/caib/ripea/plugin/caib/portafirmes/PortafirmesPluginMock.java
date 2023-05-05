/**
 * 
 */
package es.caib.ripea.plugin.caib.portafirmes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import es.caib.ripea.plugin.RipeaAbstractPluginProperties;
import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.portafirmes.PortafirmesBlockInfo;
import es.caib.ripea.plugin.portafirmes.PortafirmesCarrec;
import es.caib.ripea.plugin.portafirmes.PortafirmesDocument;
import es.caib.ripea.plugin.portafirmes.PortafirmesDocumentTipus;
import es.caib.ripea.plugin.portafirmes.PortafirmesFluxBloc;
import es.caib.ripea.plugin.portafirmes.PortafirmesFluxInfo;
import es.caib.ripea.plugin.portafirmes.PortafirmesFluxResposta;
import es.caib.ripea.plugin.portafirmes.PortafirmesIniciFluxResposta;
import es.caib.ripea.plugin.portafirmes.PortafirmesPlugin;
import es.caib.ripea.plugin.portafirmes.PortafirmesPrioritatEnum;

/**
 * Implementació del plugin de portafirmes per fer proves sense accés
 * a cap sistema extern.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PortafirmesPluginMock extends RipeaAbstractPluginProperties implements PortafirmesPlugin {

	
	public PortafirmesPluginMock() {
		super();
	}
	public PortafirmesPluginMock(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}
	
	@Override
	public String upload(
			PortafirmesDocument document,
			String documentTipus,
			String motiu,
			String remitent,
			PortafirmesPrioritatEnum prioritat,
			Date dataCaducitat,
			List<PortafirmesFluxBloc> flux,
			String plantillaFluxId,
			List<PortafirmesDocument> annexos,
			boolean signarAnnexos,
			String transaccioId) throws SistemaExternException {
		//throw new SistemaExternException("Això no acaba d'anar be");
		return new Long(System.currentTimeMillis()).toString();
	}

	@Override
	public PortafirmesDocument download(
			String id) throws SistemaExternException {
		//throw new SistemaExternException("Això no acaba d'anar be");
		PortafirmesDocument pdoc = new PortafirmesDocument();
		pdoc.setArxiuNom("arxiu.pdf");
		pdoc.setArxiuContingut(new byte[0]);
		pdoc.setFirmat(true);
		return pdoc;
	}

	@Override
	public void delete(
			String id) throws SistemaExternException {
		throw new SistemaExternException("Això no acaba d'anar be");
	}

	@Override
	public List<PortafirmesDocumentTipus> findDocumentTipus() throws SistemaExternException {
		List<PortafirmesDocumentTipus> resposta = new ArrayList<PortafirmesDocumentTipus>();
		PortafirmesDocumentTipus tipusDocument = new PortafirmesDocumentTipus();
		for(int i = 0; i < 5; i++) {
			tipusDocument = new PortafirmesDocumentTipus();
			tipusDocument.setId(new Long(i));
			tipusDocument.setCodi("T" + i);
			tipusDocument.setNom("Tipus " + i);
			resposta.add(tipusDocument);				
		}		
		return resposta;
	}

	@Override
	public boolean isCustodiaAutomatica() {
		return false;
	}

	@Override
	public PortafirmesIniciFluxResposta iniciarFluxDeFirma(
			String idioma,
			boolean isPlantilla,
			String nom,
			String descripcio,
			boolean descripcioVisible,
			String urlReturn) throws SistemaExternException {
		PortafirmesIniciFluxResposta transaccioResponse = null;
		try {
//			String idTransaccio = getTransaction(
//					idioma, 
//					isPlantilla, 
//					nom, 
//					descripcio, 
//					descripcioVisible);
//			
//			urlRedireccio = startTransaction(
//					idTransaccio, 
//					urlReturn + idTransaccio);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return transaccioResponse;
	}
	
	@Override
	public PortafirmesFluxResposta recuperarFluxDeFirmaByIdTransaccio(String idTransaccio) {
		PortafirmesFluxResposta urlRedireccio = null;
		try {
//			FlowTemplateSimpleGetFlowResultResponse result = getFluxDeFirmaClient().getFlowTemplateResult(idTransaccio);
//			
//			result.getFlowInfo().getIntermediateServerFlowTemplateId();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// getFluxDeFirmaClient().closeTransaction(idTransaccio);
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return urlRedireccio;
	}

	@Override
	public void tancarTransaccioFlux(String idTransaccio) throws SistemaExternException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public PortafirmesFluxInfo recuperarFluxDeFirmaByIdPlantilla(String idTransaccio, String idioma)
			throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String recuperarUrlViewEditPlantilla(String idPlantilla, String idioma, String urlReturn, boolean edicio) throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PortafirmesFluxResposta> recuperarPlantillesDisponibles(String idioma) throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean esborrarPlantillaFirma(String idioma, String plantillaFluxId) throws SistemaExternException {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public List<PortafirmesBlockInfo> recuperarBlocksFirmes(String idPlantilla, String idTransaccio,
			boolean portafirmesFluxAsync, Long portafirmesId, String idioma) throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PortafirmesCarrec> recuperarCarrecs() throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PortafirmesCarrec recuperarCarrec(String carrecId) throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PortafirmesFluxResposta> recuperarPlantillesPerFiltre(String idioma,
			String descripcio) throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String recuperarUrlViewEstatFluxDeFirmes(long portafirmesId, String idioma) throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}
}
