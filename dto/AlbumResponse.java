package cl.coleccion_laminas.dto;

import java.time.LocalDate;

public record AlbumResponse(
  Long id,
  String name,
  String coverImageUrl,
  LocalDate releaseDate,
  String stickerType,
  Integer totalStickers,
  String description
) {}
