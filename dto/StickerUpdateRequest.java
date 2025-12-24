package cl.coleccion_laminas.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * DTO para PATCH (update parcial):
 * - Los campos pueden venir null => no se actualizan.
 */
public record StickerUpdateRequest(
  @Min(value = 1, message = "El número debe ser >= 1")
  Integer number,

  @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
  String name,

  LocalDate releaseDate,

  @Size(max = 60, message = "El tipo no puede superar 60 caracteres")
  String type,

  @Min(value = 0, message = "ownedCount no puede ser negativo")
  Integer ownedCount
) {}
