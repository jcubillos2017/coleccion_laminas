package cl.coleccion_laminas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.coleccion_laminas.domain.StickerEntry;

public interface StickerEntryRepository extends JpaRepository<StickerEntry, Long> {

  List<StickerEntry> findByAlbumId(Long albumId);

  Optional<StickerEntry> findByAlbumIdAndNumber(Long albumId, Integer number);
}
