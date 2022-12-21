package com.example.animalsheltertelegrambot.model;

import jakarta.persistence.*;

/**
 * Класс для фотографии животного
 */
@Entity
public class PhotoOfAnimal {
    /**
     * Поле id фотографии
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    /**
     * Поле путь к файлу
     */
    private String filePath;
    /**
     * Поле размер файла
     */
    private long fileSize;
    /**
     * Поле тип файла
     */
    private String mediaType;
    /**
     * Поле информация о файле
     */
    private byte[] data;
    /**
     * Поле для связи с таблицей Report
     */
    @OneToOne
    private Report report;

    /**
     * Метод получения значения поля {@link PhotoOfAnimal#id}
     *
     * @return - возвращает id пользователя
     */
    public long getId() {
        return id;
    }

    /**
     * Метод изменения значения поля {@link PhotoOfAnimal#id}
     *
     * @param id - id фотографии
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Метод получения значения поля {@link PhotoOfAnimal#filePath}
     *
     * @return - возвращает путь к файлу
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Метод изменения значения поля {@link PhotoOfAnimal#filePath}
     *
     * @param filePath - путь к файлу
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Метод получения значения поля {@link PhotoOfAnimal#fileSize}
     *
     * @return - возвращает размер файла
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * Метод изменения значения поля {@link PhotoOfAnimal#fileSize}
     *
     * @param fileSize - размер файла
     */
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * Метод получения значения поля {@link PhotoOfAnimal#mediaType}
     *
     * @return - возвращает тип файла
     */
    public String getMediaType() {
        return mediaType;
    }

    /**
     * Метод изменения значения поля {@link PhotoOfAnimal#mediaType}
     *
     * @param mediaType - тип файла
     */
    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    /**
     * Метод получения значения поля {@link PhotoOfAnimal#data}
     *
     * @return - возвращает информацию о файле
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Метод изменения значения поля {@link PhotoOfAnimal#data}
     *
     * @param data - информация о файле
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     *  Метод получения значения поля {@link PhotoOfAnimal#report}
     *
     * @return - возврощает отчет
     */
    public Report getReport() {
        return report;
    }

    /**
     * Метод изменения значения поля {@link PhotoOfAnimal#report}
     *
     * @param report - отчет
     */
    public void setReport(Report report) {
        this.report = report;
    }
}

