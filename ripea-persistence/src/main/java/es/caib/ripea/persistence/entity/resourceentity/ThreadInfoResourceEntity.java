package es.caib.ripea.persistence.entity.resourceentity;

import java.lang.management.LockInfo;

import es.caib.ripea.persistence.base.entity.ResourceEntity;
import es.caib.ripea.service.intf.model.ThreadInfoResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ThreadInfoResourceEntity implements ResourceEntity<ThreadInfoResource, Long> {

	private Long         threadId;
    private String       threadName;    
    private long         blockedTime;
    private long         blockedCount;
    private long         waitedTime;
    private long         waitedCount;
    private LockInfo     lock;
    private String       lockName;
    private long         lockOwnerId;
    private String       lockOwnerName;
    private boolean      daemon;
    private boolean      inNative;
    private boolean      suspended;
    private Thread.State threadState;
    private int          priority;
	
	@Override
	public Long getId() {
		return this.threadId;
	}

	@Override
	public boolean isNew() {
		return getId()==null;
	}
}
