package com.hartron.elasticmicromysql.service.dto;


import java.time.ZonedDateTime;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import com.hartron.elasticmicromysql.domain.enumeration.Status;

/**
 * A DTO for the File entity.
 */
public class FileDTO implements Serializable {

    private Long id;

    @NotNull
    private String fileNo;

    @NotNull
    private String title;

    private String tag;

    private ZonedDateTime uploadDate;

    @NotNull
    private Status status;

    @NotNull
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

    public void setFileNo(String fileNo) {
        this.fileNo = fileNo;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
    public ZonedDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(ZonedDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    public Boolean getPriority() {
        return priority;
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

        FileDTO fileDTO = (FileDTO) o;

        if ( ! Objects.equals(id, fileDTO.id)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "FileDTO{" +
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
