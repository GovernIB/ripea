package es.caib.ripea.service.intf.model;

import java.lang.management.LockInfo;

import es.caib.ripea.service.intf.base.annotation.ResourceArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseResource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        quickFilterFields = { "threadName" },
        descriptionField = "threadName",
        artifacts = {
            @ResourceArtifact(
                    type = ResourceArtifactType.ACTION,
                    code = ThreadInfoResource.ACTION_SYSTEM_INFO),
        })
public class ThreadInfoResource extends BaseResource<Long> {

	public static final String ACTION_SYSTEM_INFO	= "SYSTEM_INFO";
	
	private Long         threadId;
    private String       threadName;
    private String		 threadState;
    private String 		 tiempoCPU;
    private int          prioritat;
    private boolean      suspended;
    private LockInfo     lock;
    private String       waitedTime;
    private String       blockedTime;    

	@Override
	public Long getId() {
		return this.threadId;
	}

	@Override
	public void setId(Long id) {
		this.threadId = id;
	}
}
