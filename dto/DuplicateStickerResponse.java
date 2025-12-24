package cl.coleccion_laminas.dto;

/**
 * repetidoContar = propiedadRecuento - 1
 */
public record DuplicateStickerResponse(
  Integer number,
  Integer repeatedCount
) {}
