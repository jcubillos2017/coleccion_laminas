package cl.coleccion_laminas.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import cl.coleccion_laminas.domain.Album;
import cl.coleccion_laminas.domain.StickerEntry;
import cl.coleccion_laminas.dto.DuplicateStickerResponse;
import cl.coleccion_laminas.dto.StickerCreateRequest;
import cl.coleccion_laminas.dto.StickerResponse;
import cl.coleccion_laminas.dto.StickerUpdateRequest;
import cl.coleccion_laminas.exception.NotFoundException;
import cl.coleccion_laminas.repository.StickerEntryRepository;
import cl.coleccion_laminas.storage.LocalFileStorageService;

@Service
public class StickerService {

    private final StickerEntryRepository stickerRepo;
    private final AlbumService albumService;
    private final LocalFileStorageService fileStorage;

    public StickerService(StickerEntryRepository stickerRepo,
            AlbumService albumService,
            LocalFileStorageService fileStorage) {
        this.stickerRepo = stickerRepo;
        this.albumService = albumService;
        this.fileStorage = fileStorage;
    }

    // =========================
    // CRUD DE LÁMINAS (Punto 2)
    // =========================

    @Transactional
    public StickerResponse createSticker(Long albumId, StickerCreateRequest req) {
        Album album = albumService.findById(albumId);
        validateStickerNumberInRange(album, req.number());

        // Evita duplicar número de lámina en el mismo álbum
        if (stickerRepo.findByAlbumIdAndNumber(albumId, req.number()).isPresent()) {
            throw new IllegalArgumentException(
                    "Ya existe la lámina número " + req.number() + " en el álbum " + albumId);
        }

        StickerEntry e = new StickerEntry();
        e.setAlbum(album);
        e.setNumber(req.number());
        e.setName(req.name());
        e.setReleaseDate(req.releaseDate());
        e.setType(req.type());
        e.setOwnedCount(req.ownedCount() == null ? 0 : Math.max(req.ownedCount(), 0));

        StickerEntry saved = stickerRepo.save(e);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<StickerResponse> listStickers(Long albumId) {
        albumService.findById(albumId); // valida existencia
        return stickerRepo.findByAlbumId(albumId).stream()
                .sorted(Comparator.comparingInt(StickerEntry::getNumber))
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StickerResponse getSticker(Long albumId, Long stickerId) {
        StickerEntry e = findStickerInAlbum(albumId, stickerId);
        return toResponse(e);
    }

    @Transactional
    public StickerResponse patchSticker(Long albumId, Long stickerId, StickerUpdateRequest req) {
        Album album = albumService.findById(albumId);
        StickerEntry e = findStickerInAlbum(albumId, stickerId);

        // number (si viene)
        if (req.number() != null && !req.number().equals(e.getNumber())) {
            validateStickerNumberInRange(album, req.number());

            stickerRepo.findByAlbumIdAndNumber(albumId, req.number()).ifPresent(existing -> {
                if (!existing.getId().equals(stickerId)) {
                    throw new IllegalArgumentException(
                            "Ya existe la lámina número " + req.number() + " en el álbum " + albumId);
                }
            });

            e.setNumber(req.number());
        }

        // name (si viene)
        if (req.name() != null) {
            e.setName(req.name());
        }

        // releaseDate (si viene)
        if (req.releaseDate() != null) {
            e.setReleaseDate(req.releaseDate());
        }

        // type (si viene)
        if (req.type() != null) {
            e.setType(req.type());
        }

        // ownedCount (si viene)
        if (req.ownedCount() != null) {
            e.setOwnedCount(Math.max(req.ownedCount(), 0));
        }

        StickerEntry saved = stickerRepo.save(e);
        return toResponse(saved);
    }

    @Transactional
    public StickerResponse updateSticker(Long albumId, Long stickerId, StickerCreateRequest req) {
        Album album = albumService.findById(albumId);
        StickerEntry e = findStickerInAlbum(albumId, stickerId);

        // Si cambia el número, validar rango y colisión
        if (!req.number().equals(e.getNumber())) {
            validateStickerNumberInRange(album, req.number());
            stickerRepo.findByAlbumIdAndNumber(albumId, req.number()).ifPresent(existing -> {
                if (!existing.getId().equals(stickerId)) {
                    throw new IllegalArgumentException(
                            "Ya existe la lámina número " + req.number() + " en el álbum " + albumId);
                }
            });
            e.setNumber(req.number());
        }

        e.setName(req.name());
        e.setReleaseDate(req.releaseDate());
        e.setType(req.type());
        if (req.ownedCount() != null) {
            e.setOwnedCount(Math.max(req.ownedCount(), 0));
        }

        StickerEntry saved = stickerRepo.save(e);
        return toResponse(saved);
    }

    @Transactional
    public void deleteSticker(Long albumId, Long stickerId) {
        StickerEntry e = findStickerInAlbum(albumId, stickerId);
        stickerRepo.delete(e);
    }

    // =========================
    // FOTO OPCIONAL (Punto 2)
    // =========================

    @Transactional
    public StickerResponse uploadStickerPhoto(Long albumId, Long stickerId, MultipartFile photo) {
        StickerEntry e = findStickerInAlbum(albumId, stickerId);

        String publicPath = fileStorage.saveStickerPhoto(albumId, stickerId, photo);
        e.setPhotoUrl(publicPath);

        StickerEntry saved = stickerRepo.save(e);
        return toResponse(saved);
    }

    // =========================
    // FUNCIONALIDADES ESPECIALES (Punto 3)
    // =========================

    @Transactional
    public void bulkAdd(Long albumId, List<Integer> numbers) {
        Album album = albumService.findById(albumId);
        int total = album.getTotalStickers();

        for (Integer n : numbers) {
            if (n == null || n < 1 || n > total) {
                throw new IllegalArgumentException("Número fuera de rango (1.." + total + "): " + n);
            }

            StickerEntry entry = stickerRepo.findByAlbumIdAndNumber(albumId, n)
                    .orElseGet(() -> {
                        StickerEntry x = new StickerEntry();
                        x.setAlbum(album);
                        x.setNumber(n);
                        x.setOwnedCount(0);
                        return x;
                    });

            entry.setOwnedCount(entry.getOwnedCount() + 1);
            stickerRepo.save(entry);
        }
    }

    @Transactional(readOnly = true)
    public List<Integer> getMissing(Long albumId) {
        Album album = albumService.findById(albumId);
        int total = album.getTotalStickers();

        Set<Integer> owned = stickerRepo.findByAlbumId(albumId).stream()
                .filter(e -> e.getOwnedCount() != null && e.getOwnedCount() > 0)
                .map(StickerEntry::getNumber)
                .collect(Collectors.toSet());

        List<Integer> missing = new ArrayList<>();
        for (int i = 1; i <= total; i++) {
            if (!owned.contains(i))
                missing.add(i);
        }
        return missing;
    }

    @Transactional(readOnly = true)
    public List<DuplicateStickerResponse> getDuplicates(Long albumId) {
        albumService.findById(albumId); // valida existencia

        return stickerRepo.findByAlbumId(albumId).stream()
                .filter(e -> e.getOwnedCount() != null && e.getOwnedCount() > 1)
                .map(e -> new DuplicateStickerResponse(e.getNumber(), e.getOwnedCount() - 1))
                .sorted(Comparator.comparingInt(DuplicateStickerResponse::number))
                .toList();
    }

    // =========================
    // Helpers
    // =========================

    private StickerEntry findStickerInAlbum(Long albumId, Long stickerId) {
        StickerEntry e = stickerRepo.findById(stickerId)
                .orElseThrow(() -> new NotFoundException("Lámina no existe: " + stickerId));

        if (e.getAlbum() == null || e.getAlbum().getId() == null || !e.getAlbum().getId().equals(albumId)) {
            throw new NotFoundException("La lámina " + stickerId + " no pertenece al álbum " + albumId);
        }
        return e;
    }

    private void validateStickerNumberInRange(Album album, Integer number) {
        if (number == null)
            throw new IllegalArgumentException("El número de lámina es obligatorio.");
        int total = album.getTotalStickers();
        if (number < 1 || number > total) {
            throw new IllegalArgumentException("Número fuera de rango (1.." + total + "): " + number);
        }
    }

    private StickerResponse toResponse(StickerEntry e) {
        return new StickerResponse(
                e.getId(),
                e.getAlbum().getId(),
                e.getNumber(),
                e.getName(),
                e.getReleaseDate(),
                e.getType(),
                e.getPhotoUrl(),
                e.getOwnedCount());
    }
}
