package mitya.haha.repository;

import mitya.haha.model.ShotRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShotRecordRepository extends CrudRepository<ShotRecord, Long> {


}
