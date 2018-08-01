/**
 * 
 */
package es.caib.ripea.plugin.caib.portafirmes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.portafirmes.PortafirmesDocument;
import es.caib.ripea.plugin.portafirmes.PortafirmesDocumentTipus;
import es.caib.ripea.plugin.portafirmes.PortafirmesFluxBloc;
import es.caib.ripea.plugin.portafirmes.PortafirmesPlugin;
import es.caib.ripea.plugin.portafirmes.PortafirmesPrioritatEnum;

/**
 * Implementació del plugin de portafirmes per fer proves sense accés
 * a cap sistema extern.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PortafirmesPluginMock implements PortafirmesPlugin {

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
			boolean signarAnnexos) throws SistemaExternException {
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

}
