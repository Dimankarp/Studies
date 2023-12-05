package mitya.haha.service;

import mitya.haha.model.*;
import mitya.haha.repository.ShotRecordRepository;
import org.springframework.stereotype.Service;

@Service
public class ShotProcessingService {

    private Target target;
    private ShotRecordRepository repository;

    public ShotProcessingService(ShotRecordRepository repo){
        TargetBuilder builder = new TargetBuilder();
        /*
         Examples of chain Target building:
         this.target = builder.setRadiusInterval(10, -1).setXInterval(10, -10).buildTarget();
         */

        this.target = builder.buildTarget();
        this.repository = repo;
    }

    public boolean checkIfShotValid(ShotParams params){
        return target.areCoordsValid(params);
    }
    public void registerShot(ShotParams params, User user){
        ShotRecord record = target.processShot(params, user);
        repository.save(record);
    }


}
