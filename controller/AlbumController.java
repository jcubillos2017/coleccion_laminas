package cl.coleccion_laminas.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.coleccion_laminas.domain.Album;
import cl.coleccion_laminas.dto.AlbumCreateRequest;
import cl.coleccion_laminas.dto.AlbumResponse;
import cl.coleccion_laminas.service.AlbumService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/albums")
public class AlbumController {

  private final AlbumService albumService;

  public AlbumController(AlbumService albumService) {
    this.albumService = albumService;
  }

  // CREATE
  @PostMapping
  public ResponseEntity<AlbumResponse> create(@Valid @RequestBody AlbumCreateRequest req) {
    Album a = albumService.create(req);
    return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(a));
  }

  // READ ALL
  @GetMapping
  public ResponseEntity<List<AlbumResponse>> list() {
    List<AlbumResponse> albums = albumService.findAll().stream()
      .map(this::toResponse)
      .toList();
    return ResponseEntity.ok(albums);
  }

  // READ ONE
  @GetMapping("/{id}")
  public ResponseEntity<AlbumResponse> get(@PathVariable Long id) {
    Album a = albumService.findById(id);
    return ResponseEntity.ok(toResponse(a));
  }

  // UPDATE
  @PutMapping("/{id}")
  public ResponseEntity<AlbumResponse> update(@PathVariable Long id, @Valid @RequestBody AlbumCreateRequest req) {
    Album a = albumService.update(id, req);
    return ResponseEntity.ok(toResponse(a));
  }

  // DELETE
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    albumService.delete(id);
    return ResponseEntity.noContent().build();
  }

  private AlbumResponse toResponse(Album a) {
    return new AlbumResponse(
      a.getId(),
      a.getName(),
      a.getCoverImageUrl(),
      a.getReleaseDate(),
      a.getStickerType(),
      a.getTotalStickers(),
      a.getDescription()
    );
  }
}
