package mitya.sites.face.beans;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpSession;
import mitya.sites.face.data.model.ShotRecord;
import mitya.sites.face.model.Target;
import mitya.sites.face.model.TargetBuilder;
import mitya.sites.face.data.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Named("shotProcesser")
@ApplicationScoped
public class ShotProcesser implements Serializable {
    @Inject
    private Shot processingShot;
    @Inject
    private ShotRecorder shortRecorder;

    private EntityManagerFactory managerFactory;

    private final Target target;

    public ShotProcesser() {
        TargetBuilder builder = new TargetBuilder();
         /*
         Change built target here if needed
          */
        this.target = builder.buildTarget();
        this.managerFactory = Persistence.createEntityManagerFactory("org.hibernate.shot.jpa");
    }
    public String process() {
        String currSessionId = getSessionId();

        EntityManager currManager = managerFactory.createEntityManager();
        currManager.getTransaction().begin();

        CriteriaBuilder cb = currManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        cq.select(root).where(cb.equal(root.get("sessionId"), (currSessionId)));
        List<User> users = currManager.createQuery(cq).getResultList();

        User currUser;
        if (!users.isEmpty()) {
            currUser = users.get(0);
        } else {
            currUser = new User(currSessionId);
            currManager.persist(currUser);
        }

        ShotRecord record = target.processShot(processingShot, currUser);
        currManager.persist(record);
        currManager.flush();
        currManager.refresh(currUser);
        shortRecorder.setLastShotRecord(record);
        shortRecorder.setRecordedShots(new ArrayList<ShotRecord>(currUser.getRecords()));

        currManager.getTransaction().commit();
        currManager.close();

        return "success";
    }

    private String getSessionId(){
        FacesContext ctx = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) ctx.getExternalContext().getSession(true);
        return session.getId();
    }
}

