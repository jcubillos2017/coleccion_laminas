package cl.coleccion_laminas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.coleccion_laminas.domain.Album;

public interface AlbumRepository extends JpaRepository<Album, Long> {

}
