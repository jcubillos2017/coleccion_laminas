package cl.coleccion_laminas.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BulkAddRequest(
  @NotNull(message = "La lista de números es obligatoria")
  @Size(min = 1, message = "Debes enviar al menos 1 número de lámina")
  List<@NotNull(message = "No se permiten valores nulos en la lista") Integer> numbers
) {}
