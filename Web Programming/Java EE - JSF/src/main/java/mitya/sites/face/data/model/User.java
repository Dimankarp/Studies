package mitya.sites.face.data.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Entity
@Table(name = "users")
public final class User {

    private long id;

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    private String sessionId;

    @NotNull
    @Column(length = 60, unique = true)
    public String getSessionId() {
        return sessionId;
    }

    private void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    private List<ShotRecord> records;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    public List<ShotRecord> getRecords() {
        return records;
    }
    private void setRecords(List<ShotRecord> records){
        this.records = records;
    }

    public User() {

    }

    public User(String sessionId) {
        this.sessionId = sessionId;
    }
}
