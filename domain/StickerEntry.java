package cl.coleccion_laminas.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
  name = "sticker_entries",
  uniqueConstraints = @UniqueConstraint(name = "uk_album_number", columnNames = {"album_id", "number"})
)
public class StickerEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "album_id", nullable = false)
  private Album album;

  @Column(nullable = false)
  private Integer number;

  @Column(length = 120)
  private String name;

  private LocalDate releaseDate;

  @Column(length = 60)
  private String type;

  // Foto opcional (ruta/URL)
  @Column(length = 500)
  private String photoUrl;

  @Column(nullable = false)
  private Integer ownedCount = 0;

  // Getters/Setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public Album getAlbum() { return album; }
  public void setAlbum(Album album) { this.album = album; }

  public Integer getNumber() { return number; }
  public void setNumber(Integer number) { this.number = number; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public LocalDate getReleaseDate() { return releaseDate; }
  public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }

  public String getType() { return type; }
  public void setType(String type) { this.type = type; }

  public String getPhotoUrl() { return photoUrl; }
  public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

  public Integer getOwnedCount() { return ownedCount; }
  public void setOwnedCount(Integer ownedCount) { this.ownedCount = ownedCount; }
}
