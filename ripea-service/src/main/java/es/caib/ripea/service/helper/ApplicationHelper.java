package es.caib.ripea.service.helper;

import es.caib.ripea.core.persistence.repository.ProcessosInicialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ApplicationHelper {
	
	
    @Autowired
    private ProcessosInicialsRepository processosInicialsRepository;
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void setProcessAsProcessed(Long id){
		
		processosInicialsRepository.updateInit(id, false);
	}
	 

}
