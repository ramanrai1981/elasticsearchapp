package com.hartron.elasticmicromysql.service.impl;

import com.hartron.elasticmicromysql.service.FileService;
import com.hartron.elasticmicromysql.domain.File;
import com.hartron.elasticmicromysql.repository.FileRepository;
import com.hartron.elasticmicromysql.repository.search.FileSearchRepository;
import com.hartron.elasticmicromysql.service.dto.FileDTO;
import com.hartron.elasticmicromysql.service.mapper.FileMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing File.
 */
@Service
@Transactional
public class FileServiceImpl implements FileService{

    private final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);
    
    private final FileRepository fileRepository;

    private final FileMapper fileMapper;

    private final FileSearchRepository fileSearchRepository;

    public FileServiceImpl(FileRepository fileRepository, FileMapper fileMapper, FileSearchRepository fileSearchRepository) {
        this.fileRepository = fileRepository;
        this.fileMapper = fileMapper;
        this.fileSearchRepository = fileSearchRepository;
    }

    /**
     * Save a file.
     *
     * @param fileDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public FileDTO save(FileDTO fileDTO) {
        log.debug("Request to save File : {}", fileDTO);
        File file = fileMapper.fileDTOToFile(fileDTO);
        file = fileRepository.save(file);
        FileDTO result = fileMapper.fileToFileDTO(file);
        fileSearchRepository.save(file);
        return result;
    }

    /**
     *  Get all the files.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<FileDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Files");
        Page<File> result = fileRepository.findAll(pageable);
        return result.map(file -> fileMapper.fileToFileDTO(file));
    }

    /**
     *  Get one file by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public FileDTO findOne(Long id) {
        log.debug("Request to get File : {}", id);
        File file = fileRepository.findOne(id);
        FileDTO fileDTO = fileMapper.fileToFileDTO(file);
        return fileDTO;
    }

    /**
     *  Delete the  file by id.
     *
     *  @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete File : {}", id);
        fileRepository.delete(id);
        fileSearchRepository.delete(id);
    }

    /**
     * Search for the file corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<FileDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Files for query {}", query);
        Page<File> result = fileSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(file -> fileMapper.fileToFileDTO(file));
    }
}
