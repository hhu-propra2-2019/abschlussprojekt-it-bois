package mops.gruppen2.DTOs;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("gruppe")
@Data
public class GruppeDTO {
	@Id
	private Long gruppe_id;
	private String titel;
	private String beschreibung;
}
