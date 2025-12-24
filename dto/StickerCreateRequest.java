package cl.coleccion_laminas.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StickerCreateRequest(
  @NotNull @Min(1) Integer number,
  @Size(max = 120) String name,
  LocalDate releaseDate,
  @Size(max = 60) String type,
  @Min(0) Integer ownedCount
) {}
