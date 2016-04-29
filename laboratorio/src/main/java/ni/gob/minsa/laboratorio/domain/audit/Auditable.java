package ni.gob.minsa.laboratorio.domain.audit;

public interface Auditable {
	
	public boolean isFieldAuditable(String fieldname);

}
