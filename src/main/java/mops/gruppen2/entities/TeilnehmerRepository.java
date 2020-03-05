package mops.gruppen2.entities;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeilnehmerRepository extends CrudRepository<Teilnehmer,Long> {
}
