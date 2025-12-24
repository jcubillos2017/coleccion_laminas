package cl.coleccion_laminas.domain;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "albums")
public class Album {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 120)
  private String name;

  // Imagen/portada del álbum (URL o path)
  @Column(length = 500)
  private String coverImageUrl;

  // Fecha de lanzamiento del álbum
  private LocalDate releaseDate;

  // Tipo de láminas (ej: "figuritas", "cartas", "cromos")
  @Column(length = 60)
  private String stickerType;

  @Column(nullable = false)
  private Integer totalStickers;

  @Column(length = 500)
  private String description;

  @Column(nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @Column(nullable = false)
  private OffsetDateTime updatedAt;

  @PrePersist
  public void prePersist() {
    OffsetDateTime now = OffsetDateTime.now();
    this.createdAt = now;
    this.updatedAt = now;
  }

  @PreUpdate
  public void preUpdate() {
    this.updatedAt = OffsetDateTime.now();
  }

  // Getters/Setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getCoverImageUrl() { return coverImageUrl; }
  public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

  public LocalDate getReleaseDate() { return releaseDate; }
  public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }

  public String getStickerType() { return stickerType; }
  public void setStickerType(String stickerType) { this.stickerType = stickerType; }

  public Integer getTotalStickers() { return totalStickers; }
  public void setTotalStickers(Integer totalStickers) { this.totalStickers = totalStickers; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public OffsetDateTime getCreatedAt() { return createdAt; }
  public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
