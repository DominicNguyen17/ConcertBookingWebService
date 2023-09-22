package proj.concert.service.domain;

import javax.persistence.*;
@Entity
@Table(name = "USERS")
public class User {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;
    @Column(name = "USERNAME")
    private String username;
    @Column(name = "PASSWORD")
    private String password;
    @Version
    @Column(name = "VERSION")
    private long version;
    private String token;
    public User(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }
    public User(String username, String password) {
        this(null, username, password);
    }
    public User() { }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public long getVersion() { return version; }
    public void setVersion(long version) { this.version = version; }
    public String getToken(){return token; }
    public void setToken(String token) { this.token = token; }
}
