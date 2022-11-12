package ru.jd6team7.cooperatproject1.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;

/*
Сущность "Документ" хранит информацию о документах, которые оформляет/предоставляет "Опекун" (договор попечения,
скан-копии документов удостоверяющих личность, справки из мед. учреждения и т.п.).
Наименование документа, Путь к файлу документа на ресурсе, Дата подачи, Дата удаления
*/
@Entity
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long docID;
    String docName;
    String docFilePath;
    LocalDateTime createDate;
    LocalDateTime deleteDate;

    public Long getId() {
        return id;
    }

    public Long getDocID() {
        return docID;
    }

    public void setDocID(Long docID) {
        this.docID = docID;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDocFilePath() {
        return docFilePath;
    }

    public void setDocFilePath(String docFilePath) {
        this.docFilePath = docFilePath;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(LocalDateTime deleteDate) {
        this.deleteDate = deleteDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document)) return false;
        Document document = (Document) o;
        return Objects.equals(getDocID(), document.getDocID()) && Objects.equals(getDocName(), document.getDocName()) && Objects.equals(getCreateDate(), document.getCreateDate()) && Objects.equals(getDeleteDate(), document.getDeleteDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDocID(), getDocName(), getCreateDate(), getDeleteDate());
    }
}
