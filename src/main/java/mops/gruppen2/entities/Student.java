package mops.gruppen2.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

public class Student extends Teilnehmer{

    public Student(String vorname, String nachname) {
        super(vorname, nachname);
    }

}
