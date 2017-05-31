package com.hartron.elasticmicromysql.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.hartron.elasticmicromysql.service.FileService;
import com.hartron.elasticmicromysql.web.rest.util.HeaderUtil;
import com.hartron.elasticmicromysql.web.rest.util.PaginationUtil;
import com.hartron.elasticmicromysql.service.dto.FileDTO;
import io.swagger.annotations.ApiParam;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing File.
 */
@RestController
@RequestMapping("/api")
public class FileResource {

    private final Logger log = LoggerFactory.getLogger(FileResource.class);

    private static final String ENTITY_NAME = "file";
        
    private final FileService fileService;

    public FileResource(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * POST  /files : Create a new file.
     *
     * @param fileDTO the fileDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new fileDTO, or with status 400 (Bad Request) if the file has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/files")
    @Timed
    public ResponseEntity<FileDTO> createFile(@Valid @RequestBody FileDTO fileDTO) throws URISyntaxException {
        log.debug("REST request to save File : {}", fileDTO);
        if (fileDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new file cannot already have an ID")).body(null);
        }
        FileDTO result = fileService.save(fileDTO);
        return ResponseEntity.created(new URI("/api/files/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /files : Updates an existing file.
     *
     * @param fileDTO the fileDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated fileDTO,
     * or with status 400 (Bad Request) if the fileDTO is not valid,
     * or with status 500 (Internal Server Error) if the fileDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/files")
    @Timed
    public ResponseEntity<FileDTO> updateFile(@Valid @RequestBody FileDTO fileDTO) throws URISyntaxException {
        log.debug("REST request to update File : {}", fileDTO);
        if (fileDTO.getId() == null) {
            return createFile(fileDTO);
        }
        FileDTO result = fileService.save(fileDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, fileDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /files : get all the files.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of files in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/files")
    @Timed
    public ResponseEntity<List<FileDTO>> getAllFiles(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Files");
        Page<FileDTO> page = fileService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/files");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /files/:id : get the "id" file.
     *
     * @param id the id of the fileDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the fileDTO, or with status 404 (Not Found)
     */
    @GetMapping("/files/{id}")
    @Timed
    public ResponseEntity<FileDTO> getFile(@PathVariable Long id) {
        log.debug("REST request to get File : {}", id);
        FileDTO fileDTO = fileService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(fileDTO));
    }

    /**
     * DELETE  /files/:id : delete the "id" file.
     *
     * @param id the id of the fileDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/files/{id}")
    @Timed
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        log.debug("REST request to delete File : {}", id);
        fileService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/files?query=:query : search for the file corresponding
     * to the query.
     *
     * @param query the query of the file search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/files")
    @Timed
    public ResponseEntity<List<FileDTO>> searchFiles(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Files for query {}", query);
        Page<FileDTO> page = fileService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/files");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
