package mops.gruppen2.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data

public class Gruppe {
	@Id
	Long id;
	private String titel;
	private String beschreibung;
	List<Teilnehmer> teilnehmersList;

}
