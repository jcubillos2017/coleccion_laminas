package cl.coleccion_laminas.dto;

import java.time.LocalDate;

public record StickerResponse(
  Long id,
  Long albumId,
  Integer number,
  String name,
  LocalDate releaseDate,
  String type,
  String photoUrl,
  Integer ownedCount
) {}
