package mitya.haha.service;

import mitya.haha.model.*;
import mitya.haha.repository.ShotRecordRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class ShotProcessingService {

    private Target target;
    private ShotRecordRepository repository;

    public ShotProcessingService(ShotRecordRepository repo) {
        TargetBuilder builder = new TargetBuilder();
        /*
         Examples of chain Target building:
         this.target = builder.setRadiusInterval(10, -1).setXInterval(10, -10).buildTarget();
         */

        this.target = builder.buildTarget();
        this.repository = repo;
    }

    public boolean checkIfShotValid(ShotParams params) {
        return target.areCoordsValid(params);
    }

    public ShotRecord registerShot(ShotParams params, User user) {
        ShotRecord record = target.processShot(params, user);
        repository.save(record);
        return record;
    }

    public ShotRecordDTO convertToDTO(ShotRecord record) {
        ShotRecordDTO dto = new ShotRecordDTO();
        dto.setX(record.getX());
        dto.setY(record.getY());
        dto.setHit(record.isHit());
        dto.setTimeStamp(record.getTimeStamp());
        return dto;
    }

    public List<ShotRecordDTO> convertToDTO(Collection<ShotRecord> records) {
        return records.stream().map(this::convertToDTO).toList();
    }


}
