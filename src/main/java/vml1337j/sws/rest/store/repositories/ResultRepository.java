package vml1337j.sws.rest.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vml1337j.sws.rest.store.entities.ResultEntity;

public interface ResultRepository extends JpaRepository<ResultEntity, ResultEntity.RacerEventPK> {
}
