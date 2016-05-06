package ni.gob.minsa.laboratorio.domain.audit;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "lab_audit_trail", schema = "laboratorio")
public class AuditTrail {
	
	private int id;
	private String entityId;
	private String entityName;
	private String entityProperty;
	private String entityPropertyOldValue;
	private String entityPropertyNewValue;
	private String operationType;
	private String username;
	private Date operationDate;
	
	
	
	public AuditTrail(String entityId, String entityName,
			String entityProperty, String entityPropertyOldValue,
			String entityPropertyNewValue, String operationType,
			String username, Date operationDate) {
		super();
		this.entityId = entityId;
		this.entityName = entityName;
		this.entityProperty = entityProperty;
		this.entityPropertyOldValue = entityPropertyOldValue;
		this.entityPropertyNewValue = entityPropertyNewValue;
		this.operationType = operationType;
		this.username = username;
		this.operationDate = operationDate;
	}

    public AuditTrail() {
    }

    @Id
	@GenericGenerator(name="idautoinc3" , strategy="increment")
	@GeneratedValue(generator="idautoinc3")
	@Column(name="id")
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	@Column(name = "ENTITY_ID", nullable = true, length =100)
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	@Column(name = "ENTITY_NAME", nullable = true, length =150)
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	@Column(name = "ENTITY_PROPERTY", nullable = true, length =150)
	public String getEntityProperty() {
		return entityProperty;
	}
	public void setEntityProperty(String entityProperty) {
		this.entityProperty = entityProperty;
	}
	@Column(name = "ENTITY_PROPERTY_OLD_VALUE", nullable = true, length =1000)
	public String getEntityPropertyOldValue() {
		return entityPropertyOldValue;
	}
	public void setEntityPropertyOldValue(String entityPropertyOldValue) {
		this.entityPropertyOldValue = entityPropertyOldValue;
	}
	@Column(name = "ENTITY_PROPERTY_NEW_VALUE", nullable = true, length =1000)
	public String getEntityPropertyNewValue() {
		return entityPropertyNewValue;
	}
	public void setEntityPropertyNewValue(String entityPropertyNewValue) {
		this.entityPropertyNewValue = entityPropertyNewValue;
	}
	@Column(name = "OPERATION_TYPE", nullable = true, length =50)
	public String getOperationType() {
		return operationType;
	}
	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}
	@Column(name = "USERNAME", nullable = true, length =50)
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "OPERATION_DATE", nullable = true)
    @Temporal(TemporalType.DATE)
    public Date getOperationDate() {
		return operationDate;
	}
	public void setOperationDate(Date operationDate) {
		this.operationDate = operationDate;
	}

}
