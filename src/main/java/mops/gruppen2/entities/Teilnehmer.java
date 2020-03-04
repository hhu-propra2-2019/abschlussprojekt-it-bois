package mops.gruppen2.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
public class Teilnehmer {
	@Id
	Long id;
	String vorname;
	String nachname;
	String email;
	List<Gruppe> Gruppen;
}
