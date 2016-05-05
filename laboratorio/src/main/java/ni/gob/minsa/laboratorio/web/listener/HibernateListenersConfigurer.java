package ni.gob.minsa.laboratorio.web.listener;

import org.hibernate.SessionFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Created by FIRSTICT on 4/25/2016.
 * V1.0
 */
@Component
public class HibernateListenersConfigurer {

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;
    @Autowired
    private HibernateAuditLogListener hibernateAuditLogListener;

    @PostConstruct
    public void registerListeners() {
        EventListenerRegistry registry = ((SessionFactoryImpl) sessionFactory).getServiceRegistry().getService(EventListenerRegistry.class);
        registry.getEventListenerGroup(EventType.POST_UPDATE).appendListener(hibernateAuditLogListener);
        registry.getEventListenerGroup(EventType.POST_DELETE).appendListener(hibernateAuditLogListener);

    }
}
