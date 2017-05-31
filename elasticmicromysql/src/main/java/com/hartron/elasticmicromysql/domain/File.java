package com.hartron.elasticmicromysql.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

import com.hartron.elasticmicromysql.domain.enumeration.Status;

/**
 * A File.
 */
@Entity
@Table(name = "file")
@Document(indexName = "file")
public class File implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "file_no", nullable = false)
    private String fileNo;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "tag")
    private String tag;

    @Column(name = "upload_date")
    private ZonedDateTime uploadDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @NotNull
    @Column(name = "priority", nullable = false)
    private Boolean priority;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileNo() {
        return fileNo;
    }

    public File fileNo(String fileNo) {
        this.fileNo = fileNo;
        return this;
    }

    public void setFileNo(String fileNo) {
        this.fileNo = fileNo;
    }

    public String getTitle() {
        return title;
    }

    public File title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTag() {
        return tag;
    }

    public File tag(String tag) {
        this.tag = tag;
        return this;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public ZonedDateTime getUploadDate() {
        return uploadDate;
    }

    public File uploadDate(ZonedDateTime uploadDate) {
        this.uploadDate = uploadDate;
        return this;
    }

    public void setUploadDate(ZonedDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Status getStatus() {
        return status;
    }

    public File status(Status status) {
        this.status = status;
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Boolean isPriority() {
        return priority;
    }

    public File priority(Boolean priority) {
        this.priority = priority;
        return this;
    }

    public void setPriority(Boolean priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        File file = (File) o;
        if (file.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, file.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "File{" +
            "id=" + id +
            ", fileNo='" + fileNo + "'" +
            ", title='" + title + "'" +
            ", tag='" + tag + "'" +
            ", uploadDate='" + uploadDate + "'" +
            ", status='" + status + "'" +
            ", priority='" + priority + "'" +
            '}';
    }
}
