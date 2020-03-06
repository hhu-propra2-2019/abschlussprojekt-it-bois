package mops.gruppen2.entities;

import lombok.Data;

import java.util.List;

@Data
public class Teilnehmer {
	String id;
	String vorname;
	String nachname;
	String email;
	List<Gruppe> Gruppen;
}
