package com.hartron.elasticmicromysql.service.mapper;

import com.hartron.elasticmicromysql.domain.*;
import com.hartron.elasticmicromysql.service.dto.FileDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity File and its DTO FileDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface FileMapper {

    FileDTO fileToFileDTO(File file);

    List<FileDTO> filesToFileDTOs(List<File> files);

    File fileDTOToFile(FileDTO fileDTO);

    List<File> fileDTOsToFiles(List<FileDTO> fileDTOs);
}
