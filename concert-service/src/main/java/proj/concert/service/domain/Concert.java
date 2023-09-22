package proj.concert.service.domain;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import proj.concert.common.jackson.LocalDateTimeDeserializer;
import proj.concert.common.jackson.LocalDateTimeSerializer;

@Entity
@Table(name="CONCERTS")
public class Concert{

    // TODO Implement this class.
    @Id
    @GeneratedValue
    @Column(name="ID")
    private Long id;
    @Column(name="TITLE")
    private String title;
    @Column(name="IMAGE_NAME")
    private String imageName;
    @Column(name="BLURB", length = 1000)
    private String blrb;
    @ElementCollection
    @CollectionTable(
            name = "CONCERT_DATES",
            joinColumns = @JoinColumn(name = "CONCERT_ID"))
    @Column(name = "DATE")
    private Set<LocalDateTime> dates = new HashSet<>();
    @ManyToMany
    @JoinTable(
            name = "CONCERT_PERFORMER",
            joinColumns=@JoinColumn(name="CONCERT_ID", referencedColumnName = "ID"),
            inverseJoinColumns=@JoinColumn(name="PERFORMER_ID", referencedColumnName = "ID"))
    private Set<Performer> performers = new HashSet<>();

    public Concert(Long id, String title, String imageName, String blurb) {
        this.id = id;
        this.title = title;
        this.imageName = imageName;
        blrb = blurb;
    }
    public Concert(String title, String imageName, String blurb) {
        this(null, title, imageName, blurb);
    }
    public Concert() { }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }
    public String getBlrb() { return blrb; }
    public void setBlrb(String blurb) { blrb = blurb; }
    @JsonSerialize(contentUsing = LocalDateTimeSerializer.class)
    @JsonDeserialize(contentUsing = LocalDateTimeDeserializer.class)
    public Set<LocalDateTime> getDates() {
        return dates;
    }
    public void setDates(Set<LocalDateTime> dates) { this.dates = dates; }
    public Set<Performer> getPerformers() { return performers; }
    public void setPerformers(Set<Performer> performers) { this.performers = performers; }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Concert)) return false;
        if (obj == this) return true;
        Concert rhs = (Concert) obj;
        return new EqualsBuilder().append(title, rhs.title).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).append(title).hashCode();
    }
}
