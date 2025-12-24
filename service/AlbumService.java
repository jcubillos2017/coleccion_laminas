package cl.coleccion_laminas.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.coleccion_laminas.domain.Album;
import cl.coleccion_laminas.dto.AlbumCreateRequest;
import cl.coleccion_laminas.exception.NotFoundException;
import cl.coleccion_laminas.repository.AlbumRepository;

@Service
@Transactional
public class AlbumService {

  private final AlbumRepository albumRepository;

  public AlbumService(AlbumRepository albumRepository) {
    this.albumRepository = albumRepository;
  }

  public Album create(AlbumCreateRequest req) {
    Album a = new Album();
    a.setName(req.name());
    a.setCoverImageUrl(req.coverImageUrl());
    a.setReleaseDate(req.releaseDate());
    a.setStickerType(req.stickerType());
    a.setTotalStickers(req.totalStickers());
    a.setDescription(req.description());
    return albumRepository.save(a);
  }

  @Transactional(readOnly = true)
  public List<Album> findAll() {
    return albumRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Album findById(Long id) {
    return albumRepository.findById(id)
      .orElseThrow(() -> new NotFoundException("Album no existe: " + id));
  }

  public Album update(Long id, AlbumCreateRequest req) {
    Album a = findById(id);

    a.setName(req.name());
    a.setCoverImageUrl(req.coverImageUrl());
    a.setReleaseDate(req.releaseDate());
    a.setStickerType(req.stickerType());
    a.setTotalStickers(req.totalStickers());
    a.setDescription(req.description());

    return albumRepository.save(a);
  }

  public void delete(Long id) {
    Album a = findById(id);
    albumRepository.delete(a);
  }
}
