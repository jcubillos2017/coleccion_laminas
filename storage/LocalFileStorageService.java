package cl.coleccion_laminas.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalFileStorageService {

  private final Path rootDir;

  private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "webp");

  public LocalFileStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) {
    this.rootDir = Paths.get(uploadDir).toAbsolutePath().normalize();
  }

  /**
   * Guarda una imagen en: {uploadDir}/stickers/{filename}
   * Retorna un "public path" tipo: /files/stickers/{filename}
   */
  public String saveStickerPhoto(Long albumId, Long stickerId, MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new IllegalArgumentException("La foto es obligatoria y no puede venir vacía.");
    }

    String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
    String ext = getExtension(originalName);

    if (ext == null || !ALLOWED_EXT.contains(ext.toLowerCase())) {
      throw new IllegalArgumentException("Formato de imagen no permitido. Usa: jpg, jpeg, png, webp.");
    }

    try {
      Path stickersDir = rootDir.resolve("stickers").normalize();
      Files.createDirectories(stickersDir);

      String filename = "sticker-a" + albumId + "-s" + stickerId + "-" + UUID.randomUUID() + "." + ext.toLowerCase();
      Path target = stickersDir.resolve(filename).normalize();

      // Evita path traversal
      if (!target.startsWith(stickersDir)) {
        throw new IllegalArgumentException("Nombre de archivo inválido.");
      }

      Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

      // Ruta pública (la serviremos con WebConfig)
      return "/files/stickers/" + filename;

    } catch (IOException e) {
      throw new RuntimeException("No se pudo guardar la foto. " + e.getMessage(), e);
    }
  }

  private String getExtension(String filename) {
    int dot = filename.lastIndexOf('.');
    if (dot < 0 || dot == filename.length() - 1) return null;
    return filename.substring(dot + 1);
  }
}
