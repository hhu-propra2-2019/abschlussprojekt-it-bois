package mops.gruppen2.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data

public class Gruppe {
	@Id
	Long id;
	String titel;
	String beschreibung;
	List<Teilnehmer> teilnehmersList;

	public Gruppe (String titel, String beschreibung){
		this.titel = titel;
		this.beschreibung = beschreibung;
	}
}
