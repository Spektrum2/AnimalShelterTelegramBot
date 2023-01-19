package com.example.animalsheltertelegrambot.service;

import com.example.animalsheltertelegrambot.exception.PhotoNotFoundException;
import com.example.animalsheltertelegrambot.model.PhotoOfAnimal;
import com.example.animalsheltertelegrambot.repository.PhotoRepository;
import com.example.animalsheltertelegrambot.repository.ReportRepository;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Document;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.response.GetFileResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Сервис для обработки фотографии
 */
@Service
public class PhotoOfAnimalService {
    private final Logger logger = LoggerFactory.getLogger(PhotoOfAnimal.class);
    @Value("${path.to.photo.folder}")
    private String photoDir;
    private final TelegramBot telegramBot;
    private final PhotoRepository photoRepository;
    private final ReportRepository reportRepository;

    public PhotoOfAnimalService(TelegramBot telegramBot, PhotoRepository photoRepository, ReportRepository reportRepository) {
        this.telegramBot = telegramBot;
        this.photoRepository = photoRepository;
        this.reportRepository = reportRepository;
    }

    /**
     * Метод для загрузки фотографии из телеграмма(с сжатием) в БД
     *
     * @param photoSizes фотография
     * @return возвращает фография
     */
    public PhotoOfAnimal uploadPhoto(PhotoSize[] photoSizes) {
        logger.info("Was invoked method for upload photo from photoSize");
        PhotoSize photoSize = photoSizes[2];
        try {
            GetFileResponse getFileResponse = telegramBot.execute(new GetFile(photoSize.fileId()));
            byte[] data = telegramBot.getFileContent(getFileResponse.file());
            String filePath = getFileResponse.file().filePath();
            String extension = filePath.substring(filePath.lastIndexOf('.'));
            PhotoOfAnimal photoOfAnimal = createPhoto(photoSize.fileSize(), data);
            Path path = Paths.get(photoDir).resolve(photoOfAnimal.getId() + extension);
            Files.createDirectories(path.getParent());
            Files.deleteIfExists(path);
            Files.write(path, data);
            photoOfAnimal.setMediaType(Files.probeContentType(path));
            photoOfAnimal.setFilePath(path.toString());
            return photoRepository.save(photoOfAnimal);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Метод для загрузки фотографии из телеграмма(полный размер) в БД
     *
     * @param document фотография
     * @return возвращает фотографию
     */
    public PhotoOfAnimal uploadPhoto(Document document) {
        logger.info("Was invoked method for upload photo from document");
        try {
            GetFileResponse getFileResponse = telegramBot.execute(new GetFile(document.fileId()));
            byte[] data = telegramBot.getFileContent(getFileResponse.file());
            String filePath = getFileResponse.file().filePath();
            String extension = filePath.substring(filePath.lastIndexOf('.'));
            PhotoOfAnimal photoOfAnimal = createPhoto(document.fileSize(), data);
            Path path = Paths.get(photoDir).resolve(photoOfAnimal.getId() + extension);
            Files.createDirectories(path.getParent());
            Files.deleteIfExists(path);
            Files.write(path, data);
            photoOfAnimal.setMediaType(Files.probeContentType(path));
            photoOfAnimal.setFilePath(path.toString());
            return photoRepository.save(photoOfAnimal);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Метод для создания фотографии
     *
     * @param size      размер фотографии
     * @param data      информация о файле
     * @return возвращает фотографию
     */
    public PhotoOfAnimal createPhoto(long size, byte[] data) {
        logger.info("Was invoked method for create photo");
        PhotoOfAnimal photoOfAnimal = new PhotoOfAnimal();
        photoOfAnimal.setData(data);
        photoOfAnimal.setFileSize(size);
        return photoRepository.save(photoOfAnimal);
    }

    /**
     * Метод для просмотра фотографии из БД
     *
     * @param id id фотографии
     * @return возвращает pair
     */
    public Pair<String, byte[]> readAvatarFromDb(long id) {
        logger.info("Was invoked method for read photo from db");
        PhotoOfAnimal photoOfAnimal = photoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is not photo with id = {}", id);
                    return new PhotoNotFoundException(id);
                });
        return Pair.of(photoOfAnimal.getMediaType(), photoOfAnimal.getData());
    }
}
