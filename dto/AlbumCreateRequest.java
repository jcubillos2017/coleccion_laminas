package cl.coleccion_laminas.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AlbumCreateRequest(
  @NotBlank @Size(max = 120) String name,

  @Size(max = 500) String coverImageUrl,

  LocalDate releaseDate,

  @Size(max = 60) String stickerType,

  @NotNull @Min(1) @Max(20000) Integer totalStickers,

  @Size(max = 500) String description
) {}
