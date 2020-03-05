package mops.gruppen2.entities;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GruppeRepository extends CrudRepository<Gruppe,Long> {
}
