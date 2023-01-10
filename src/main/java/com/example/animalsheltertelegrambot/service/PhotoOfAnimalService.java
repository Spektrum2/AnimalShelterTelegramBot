package com.example.animalsheltertelegrambot.service;

import com.example.animalsheltertelegrambot.model.PhotoOfAnimal;
import com.example.animalsheltertelegrambot.model.Report;
import com.example.animalsheltertelegrambot.repository.PhotoRepository;
import com.example.animalsheltertelegrambot.repository.ReportRepository;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Document;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.response.GetFileResponse;
import org.springframework.beans.factory.annotation.Value;
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

    public long uploadPhoto(PhotoSize photoSize) {
        try {
            GetFileResponse getFileResponse = telegramBot.execute(new GetFile(photoSize.fileId()));
            byte[] data = telegramBot.getFileContent(getFileResponse.file());
            String filePath = getFileResponse.file().filePath();
            String mediaType = filePath.substring(filePath.lastIndexOf('.') + 1);
            String extension = filePath.substring(filePath.lastIndexOf('.'));
            PhotoOfAnimal photoOfAnimal = createPhoto(photoSize.fileSize(), mediaType, data);
            Path path = Paths.get(photoDir).resolve(photoOfAnimal.getId() + extension);
            Files.createDirectories(path.getParent());
            Files.deleteIfExists(path);
            Files.write(path, data);
            photoOfAnimal.setFilePath(path.toString());
            return createReport(photoRepository.save(photoOfAnimal)).getId();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long uploadPhoto(Document document) {
        try {
            GetFileResponse getFileResponse = telegramBot.execute(new GetFile(document.fileId()));
            byte[] data = telegramBot.getFileContent(getFileResponse.file());
            String filePath = getFileResponse.file().filePath();
            //String mediaType = filePath.substring(filePath.lastIndexOf('.') + 1);
            String extension = filePath.substring(filePath.lastIndexOf('.'));
            PhotoOfAnimal photoOfAnimal = createPhoto(document.fileSize(), document.mimeType(), data);
            Path path = Paths.get(photoDir).resolve(photoOfAnimal.getId() + extension);
            Files.createDirectories(path.getParent());
            Files.deleteIfExists(path);
            Files.write(path, data);
            photoOfAnimal.setFilePath(path.toString());
            return createReport(photoRepository.save(photoOfAnimal)).getId();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }



    public PhotoOfAnimal createPhoto(long size, String mediaType, byte[] data) {
        PhotoOfAnimal photoOfAnimal = new PhotoOfAnimal();
        photoOfAnimal.setData(data);
        photoOfAnimal.setFileSize(size);
        photoOfAnimal.setMediaType(mediaType);
        return photoRepository.save(photoOfAnimal);
    }

    public Report createReport(PhotoOfAnimal photoOfAnimal) {
        Report report = new Report();
        report.setPhotoOfAnimal(photoOfAnimal);
        return reportRepository.save(report);
    }
}
