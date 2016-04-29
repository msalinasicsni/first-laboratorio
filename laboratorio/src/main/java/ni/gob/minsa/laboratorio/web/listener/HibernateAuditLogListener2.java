package ni.gob.minsa.laboratorio.web.listener;

import ni.gob.minsa.laboratorio.domain.audit.AuditTrail;
import ni.gob.minsa.laboratorio.domain.audit.Auditable;
import org.hibernate.HibernateException;
import org.hibernate.StatelessSession;
import org.hibernate.event.spi.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;


/** 
 * Audit Log Listener is used to log insert, update, delete, and load operations. Complete list of listeners/events can be obtained at <tt>org.hibernate.event.EventListeners</tt>. 
 *  
 * @see org.hibernate.event.spi.LoadEventListener
 * @author whoover 
 */  
public final class HibernateAuditLogListener2 implements
PostDeleteEventListener
 {
	
	
	private static final long serialVersionUID = 1L;  
    private static final Logger LOG = LoggerFactory.getLogger(HibernateAuditLogListener2.class);
    public static final String OPERATION_TYPE_DELETE = "DELETE";

    /** 
     * {@inheritDoc} 
     */  
    /*@Override
    public final void initialize(final Configuration cfg) {  
        //  
    }
    */

    /**
     * Log deletions made to the current model in the the Audit Trail. 
     *  
     * @param event 
     *            the post-deletion event
     */ 
	@Override
	public void onPostDelete(PostDeleteEvent event) {
        try {  
            final Serializable entityId = event.getId();
            final String entityName = event.getEntity().getClass().toString();  
            final Date transTime = new Date(); // new Date(event.getSource().getTimestamp());  
            
            // need to have a separate session for audit save  
            if (event.getEntity() instanceof Auditable){
            	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                final String actorId = authentication.getName();
	            StatelessSession session = event.getPersister().getFactory().openStatelessSession();  
	            session.beginTransaction();  
	  
	            if (LOG.isDebugEnabled()) {  
	                LOG.debug("{} for: {}, ID: {}, actor: {}, date: {}", new Object[] { entityName, entityId, actorId, transTime });  
	            }  
	            session.insert(new AuditTrail(entityId.toString(), entityName, OPERATION_TYPE_DELETE, null, null, OPERATION_TYPE_DELETE, actorId, transTime));  
	  
	            session.getTransaction().commit();  
	            session.close();  
            }
        } catch (HibernateException e) {  
            LOG.error("Unable to process audit log for DELETE operation", e);  
        }  
	}


 }
