package proj.concert.service.domain;

import proj.concert.common.types.Genre;

import javax.persistence.*;

@Entity
@Table(name="PERFORMERS")
public class Performer {
    @Id
    @GeneratedValue
    @Column(name="ID")
    private Long id;
    @Column(name="NAME")
    private String name;
    @Column(name="IMAGE_NAME")
    private String imageName;
    @Enumerated(EnumType.STRING)
    @Column(name="GENRE")
    private Genre genre;
    @Column(name="BLURB", length = 1000)
    private String blurb;

    public Performer(Long id, String name, String imageName, Genre genre, String blurb) {
        this.id = id;
        this.name = name;
        this.imageName = imageName;
        this.genre = genre;
        this.blurb = blurb;
    }

    public Performer(String name, String imageName, Genre genre, String blurb) {
        this(null, name, imageName, genre, blurb);
    }
    public Performer() { }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }
    public Genre getGenre() { return genre; }
    public void setGenre(Genre genre) { this.genre = genre; }
    public String getBlurb() { return blurb; }
    public void setBlurb(String blrb) { blurb = blrb; }

}
