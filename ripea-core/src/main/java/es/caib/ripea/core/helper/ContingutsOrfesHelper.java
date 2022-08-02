/**
 * 
 */
package es.caib.ripea.core.helper;

import es.caib.ripea.core.repository.AlertaRepository;
import es.caib.ripea.core.repository.ContingutComentariRepository;
import es.caib.ripea.core.repository.ContingutLogRepository;
import es.caib.ripea.core.repository.ContingutMovimentRepository;
import es.caib.ripea.core.repository.ContingutRepository;
import es.caib.ripea.core.repository.DadaRepository;
import es.caib.ripea.core.repository.ExecucioMassivaContingutRepository;
import es.caib.ripea.core.repository.NodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Utilitat per a eliminar continguts orfes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class ContingutsOrfesHelper {

	@Autowired
	private ContingutRepository contingutRepository;
	@Autowired
	private NodeRepository nodeRepository;
	@Autowired
	private DadaRepository dadaRepository;
	@Autowired
	private AlertaRepository alertaRepository;
	@Autowired
	private ExecucioMassivaContingutRepository execucioMassivaContingutRepository;
	@Autowired
	private ContingutMovimentRepository contingutMovimentRepository;
	@Autowired
	private ContingutLogRepository contingutLogRepository;
	@Autowired
	private ContingutComentariRepository contingutComentariRepository;

	// Mètodes per evitar errors al tenir continguts orfes en base de dades
	// ////////////////////////////////////////////////////////////////////

	public void deleteContingutsOrfes() {

		// Nodes orfes
		dadaRepository.deleteDadesFromNodesOrfes();
		nodeRepository.deleteNodesOrfes();

		// Continguts orfes
		List<Long> contingutsOrfes = contingutRepository.getIdContingutsOrfes();
		for (Long contingutOrfe: contingutsOrfes)
			deleteContingutOrfe(contingutOrfe);

	}

	private void deleteContingutOrfe(Long contingutId) {
		List<Long> contingutsFills = contingutRepository.findIdFills(contingutId);
		for (Long contingutFill: contingutsFills) {
			//deleteContingutOrfe(contingutFill);
			contingutRepository.removePare(contingutFill);
		}
		alertaRepository.deleteAlertesFromContingutsOrfes(contingutId);
		execucioMassivaContingutRepository.deleteExecucioMassivaFromContingutsOrfes(contingutId);
		contingutMovimentRepository.deleteMovimentsFromContingutsOrfes(contingutId);
//		contingutComentariRepository.deleteComentarisFromContingutsOrfes(contingutId);
		contingutLogRepository.deleteLogsFromContingutsOrfes(contingutId);
		contingutRepository.deleteContingutsOrfes(contingutId);
	}

//	@Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = HibernateSystemException.class)
//	public List<ContingutEntity> getContingutsPendentsArxiu(int arxiuMaxReintentsExpedients, int arxiuMaxReintentsDocuments) {
//		List<ContingutEntity> contingutsPendents = null;
//		try {
//			contingutsPendents = contingutRepository.findContingutsPendentsArxiu(arxiuMaxReintentsExpedients, arxiuMaxReintentsDocuments);
//		} catch (HibernateSystemException he) {
//			log.error("Error obtenint continguts pendents. Hi ha continguts orfes", he);
//		}
//
//		if (contingutsPendents == null) {
//			try {
//				deleteContingutsOrfes();
//				contingutsPendents = contingutRepository.findContingutsPendentsArxiu(arxiuMaxReintentsExpedients, arxiuMaxReintentsDocuments);
//			} catch (Exception e) {
//				log.error("No s'han pogut eliminar els continguts orfes, o encara han quedat dades orfes", e);
//			}
//		}
//
//		return contingutsPendents;
//	}
//
//	public List<ContingutEntity> getContingutsByPareAndNom(ContingutEntity pare, String nom) {
//		List<ContingutEntity> continguts = null;
//
//		try {
//			continguts = contingutRepository.findByPareAndNomOrderByEsborratAsc(pare, nom);
//		} catch (HibernateSystemException he) {}
//
//		if (continguts == null) {
//			deleteContingutsOrfes();
//			continguts = contingutRepository.findByPareAndNomOrderByEsborratAsc(pare, nom);
//		}
//
//		return continguts;
//	}
//
//	public ContingutEntity getContingutsByPareAndNomAndEsborrat(ContingutEntity pare, String nom, int esborrat) {
//		ContingutEntity contingut = null;
//
//		try {
//			contingut = contingutRepository.findByPareAndNomAndEsborrat(pare, nom, esborrat);
//		} catch (HibernateSystemException he) {}
//
//		if (contingut == null) {
//			deleteContingutsOrfes();
//			contingut = contingutRepository.findByPareAndNomAndEsborrat(pare, nom, esborrat);
//		}
//
//		return contingut;
//	}
//
//	public List<ContingutEntity> getContingutsByPareAndNom(String nom, ContingutTipusEnumDto tipus, ContingutEntity pare, EntitatEntity entitat, int esborrat) {
//		List<ContingutEntity> continguts = null;
//
//		try {
//			continguts = contingutRepository.findByNomAndTipusAndPareAndEntitatAndEsborrat(nom, tipus, pare, entitat, esborrat);
//		} catch (HibernateSystemException he) {}
//
//		if (continguts == null) {
//			deleteContingutsOrfes();
//			continguts = contingutRepository.findByNomAndTipusAndPareAndEntitatAndEsborrat(nom, tipus, pare, entitat, esborrat);
//		}
//
//		return continguts;
//	}
//
//	public List<ContingutEntity> getContingutsByPareAndEsborratAndOrdenat(ContingutEntity pare, int esborrat, Sort sort) {
//		List<ContingutEntity> continguts = null;
//
//		try {
//			continguts = contingutRepository.findByPareAndEsborratAndOrdenat(pare, esborrat, sort);
//		} catch (HibernateSystemException he) {}
//
//		if (continguts == null) {
//			deleteContingutsOrfes();
//			continguts = contingutRepository.findByPareAndEsborratAndOrdenat(pare, esborrat, sort);
//		}
//
//		return continguts;
//	}
//
//	public List<ContingutEntity> getContingutsByPareAndEsborratSenseOrdenar(ContingutEntity pare, int esborrat, Sort sort) {
//		List<ContingutEntity> continguts = null;
//
//		try {
//			continguts = contingutRepository.findByPareAndEsborratSenseOrdre(pare, esborrat, sort);
//		} catch (HibernateSystemException he) {}
//
//		if (continguts == null) {
//			deleteContingutsOrfes();
//			continguts = contingutRepository.findByPareAndEsborratSenseOrdre(pare, esborrat, sort);
//		}
//
//		return continguts;
//	}


//	@PostConstruct
//	@Transactional
//	@Scheduled(cron = "0 0 */2 * * ?")
//	public void postConstruct() {
//		try {
//			deleteContingutsOrfes();
//		} catch (Exception ex) {
//			log.error("No s'han pogut eliminar els continguts orfes.", ex);
//		}
//	}

}
