package cl.coleccion_laminas.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cl.coleccion_laminas.dto.BulkAddRequest;
import cl.coleccion_laminas.dto.DuplicateStickerResponse;
import cl.coleccion_laminas.dto.StickerCreateRequest;
import cl.coleccion_laminas.dto.StickerResponse;
import cl.coleccion_laminas.dto.StickerUpdateRequest;
import cl.coleccion_laminas.service.StickerService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/albums/{albumId}/stickers")
public class StickerController {

    private final StickerService stickerService;

    public StickerController(StickerService stickerService) {
        this.stickerService = stickerService;
    }

    // =========================
    // CRUD LÁMINAS (Punto 2)
    // =========================

    @PostMapping
    public ResponseEntity<StickerResponse> createSticker(@PathVariable Long albumId,
            @Valid @RequestBody StickerCreateRequest req) {
        StickerResponse created = stickerService.createSticker(albumId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<StickerResponse>> listStickers(@PathVariable Long albumId) {
        return ResponseEntity.ok(stickerService.listStickers(albumId));
    }

    @GetMapping("/{stickerId}")
    public ResponseEntity<StickerResponse> getSticker(@PathVariable Long albumId,
            @PathVariable Long stickerId) {
        return ResponseEntity.ok(stickerService.getSticker(albumId, stickerId));
    }

    @PutMapping("/{stickerId}")
    public ResponseEntity<StickerResponse> updateSticker(@PathVariable Long albumId,
            @PathVariable Long stickerId,
            @Valid @RequestBody StickerCreateRequest req) {
        return ResponseEntity.ok(stickerService.updateSticker(albumId, stickerId, req));
    }

    @PatchMapping("/{stickerId}")
    public ResponseEntity<StickerResponse> patchSticker(@PathVariable Long albumId,
            @PathVariable Long stickerId,
            @Valid @RequestBody StickerUpdateRequest req) {
        return ResponseEntity.ok(stickerService.patchSticker(albumId, stickerId, req));
    }

    @DeleteMapping("/{stickerId}")
    public ResponseEntity<Void> deleteSticker(@PathVariable Long albumId,
            @PathVariable Long stickerId) {
        stickerService.deleteSticker(albumId, stickerId);
        return ResponseEntity.noContent().build();
    }

    // =========================
    // FOTO OPCIONAL (Punto 2)
    // =========================

    @PostMapping(value = "/{stickerId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StickerResponse> uploadPhoto(@PathVariable Long albumId,
            @PathVariable Long stickerId,
            @RequestPart("photo") MultipartFile photo) {
        return ResponseEntity.ok(stickerService.uploadStickerPhoto(albumId, stickerId, photo));
    }

    // =========================
    // FUNCIONALIDADES ESPECIALES (Punto 3)
    // =========================

    /**
     * Carga masiva de láminas.
     * Permite repetidas (si envías 10 tres veces, suma 3 al ownedCount).
     */
    @PostMapping("/bulk")
    public ResponseEntity<Void> bulkAdd(@PathVariable Long albumId, @Valid @RequestBody BulkAddRequest req) {
        stickerService.bulkAdd(albumId, req.numbers());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Devuelve las láminas faltantes (números que no existen o no tienen ownedCount
     * > 0).
     */
    @GetMapping("/missing")
    public ResponseEntity<Map<String, Object>> missing(@PathVariable Long albumId) {
        List<Integer> missing = stickerService.getMissing(albumId);
        return ResponseEntity.ok(Map.of(
                "albumId", albumId,
                "missingCount", missing.size(),
                "missingNumbers", missing));
    }

    /**
     * Devuelve las láminas repetidas y cuántas repetidas hay por cada número.
     * repeatedCount = ownedCount - 1
     */
    @GetMapping("/duplicates")
    public ResponseEntity<Map<String, Object>> duplicates(@PathVariable Long albumId) {
        List<DuplicateStickerResponse> dups = stickerService.getDuplicates(albumId);
        int totalRepeated = dups.stream().mapToInt(DuplicateStickerResponse::repeatedCount).sum();

        return ResponseEntity.ok(Map.of(
                "albumId", albumId,
                "duplicateItems", dups,
                "totalRepeatedCount", totalRepeated));
    }
}
