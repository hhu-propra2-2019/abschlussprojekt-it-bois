package mops.gruppen2.domain;

import lombok.Data;

import java.util.List;

@Data
public class User {
	String id;
	String vorname;
	String nachname;
	String email;
	List<Group> gruppen;
}
