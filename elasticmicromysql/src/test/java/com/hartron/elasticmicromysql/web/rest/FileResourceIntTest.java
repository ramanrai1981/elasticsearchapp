package com.hartron.elasticmicromysql.web.rest;

import com.hartron.elasticmicromysql.ElasticmicromysqlApp;

import com.hartron.elasticmicromysql.domain.File;
import com.hartron.elasticmicromysql.repository.FileRepository;
import com.hartron.elasticmicromysql.service.FileService;
import com.hartron.elasticmicromysql.repository.search.FileSearchRepository;
import com.hartron.elasticmicromysql.service.dto.FileDTO;
import com.hartron.elasticmicromysql.service.mapper.FileMapper;
import com.hartron.elasticmicromysql.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;

import static com.hartron.elasticmicromysql.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.hartron.elasticmicromysql.domain.enumeration.Status;
/**
 * Test class for the FileResource REST controller.
 *
 * @see FileResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ElasticmicromysqlApp.class)
public class FileResourceIntTest {

    private static final String DEFAULT_FILE_NO = "AAAAAAAAAA";
    private static final String UPDATED_FILE_NO = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_TAG = "AAAAAAAAAA";
    private static final String UPDATED_TAG = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_UPLOAD_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPLOAD_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Status DEFAULT_STATUS = Status.Active;
    private static final Status UPDATED_STATUS = Status.InActive;

    private static final Boolean DEFAULT_PRIORITY = false;
    private static final Boolean UPDATED_PRIORITY = true;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileSearchRepository fileSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restFileMockMvc;

    private File file;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        FileResource fileResource = new FileResource(fileService);
        this.restFileMockMvc = MockMvcBuilders.standaloneSetup(fileResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static File createEntity(EntityManager em) {
        File file = new File()
                .fileNo(DEFAULT_FILE_NO)
                .title(DEFAULT_TITLE)
                .tag(DEFAULT_TAG)
                .uploadDate(DEFAULT_UPLOAD_DATE)
                .status(DEFAULT_STATUS)
                .priority(DEFAULT_PRIORITY);
        return file;
    }

    @Before
    public void initTest() {
        fileSearchRepository.deleteAll();
        file = createEntity(em);
    }

    @Test
    @Transactional
    public void createFile() throws Exception {
        int databaseSizeBeforeCreate = fileRepository.findAll().size();

        // Create the File
        FileDTO fileDTO = fileMapper.fileToFileDTO(file);

        restFileMockMvc.perform(post("/api/files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fileDTO)))
            .andExpect(status().isCreated());

        // Validate the File in the database
        List<File> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeCreate + 1);
        File testFile = fileList.get(fileList.size() - 1);
        assertThat(testFile.getFileNo()).isEqualTo(DEFAULT_FILE_NO);
        assertThat(testFile.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testFile.getTag()).isEqualTo(DEFAULT_TAG);
        assertThat(testFile.getUploadDate()).isEqualTo(DEFAULT_UPLOAD_DATE);
        assertThat(testFile.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testFile.isPriority()).isEqualTo(DEFAULT_PRIORITY);

        // Validate the File in Elasticsearch
        File fileEs = fileSearchRepository.findOne(testFile.getId());
        assertThat(fileEs).isEqualToComparingFieldByField(testFile);
    }

    @Test
    @Transactional
    public void createFileWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = fileRepository.findAll().size();

        // Create the File with an existing ID
        File existingFile = new File();
        existingFile.setId(1L);
        FileDTO existingFileDTO = fileMapper.fileToFileDTO(existingFile);

        // An entity with an existing ID cannot be created, so this API call must fail
        restFileMockMvc.perform(post("/api/files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingFileDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<File> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkFileNoIsRequired() throws Exception {
        int databaseSizeBeforeTest = fileRepository.findAll().size();
        // set the field null
        file.setFileNo(null);

        // Create the File, which fails.
        FileDTO fileDTO = fileMapper.fileToFileDTO(file);

        restFileMockMvc.perform(post("/api/files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fileDTO)))
            .andExpect(status().isBadRequest());

        List<File> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = fileRepository.findAll().size();
        // set the field null
        file.setTitle(null);

        // Create the File, which fails.
        FileDTO fileDTO = fileMapper.fileToFileDTO(file);

        restFileMockMvc.perform(post("/api/files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fileDTO)))
            .andExpect(status().isBadRequest());

        List<File> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = fileRepository.findAll().size();
        // set the field null
        file.setStatus(null);

        // Create the File, which fails.
        FileDTO fileDTO = fileMapper.fileToFileDTO(file);

        restFileMockMvc.perform(post("/api/files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fileDTO)))
            .andExpect(status().isBadRequest());

        List<File> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPriorityIsRequired() throws Exception {
        int databaseSizeBeforeTest = fileRepository.findAll().size();
        // set the field null
        file.setPriority(null);

        // Create the File, which fails.
        FileDTO fileDTO = fileMapper.fileToFileDTO(file);

        restFileMockMvc.perform(post("/api/files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fileDTO)))
            .andExpect(status().isBadRequest());

        List<File> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllFiles() throws Exception {
        // Initialize the database
        fileRepository.saveAndFlush(file);

        // Get all the fileList
        restFileMockMvc.perform(get("/api/files?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(file.getId().intValue())))
            .andExpect(jsonPath("$.[*].fileNo").value(hasItem(DEFAULT_FILE_NO.toString())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].tag").value(hasItem(DEFAULT_TAG.toString())))
            .andExpect(jsonPath("$.[*].uploadDate").value(hasItem(sameInstant(DEFAULT_UPLOAD_DATE))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY.booleanValue())));
    }

    @Test
    @Transactional
    public void getFile() throws Exception {
        // Initialize the database
        fileRepository.saveAndFlush(file);

        // Get the file
        restFileMockMvc.perform(get("/api/files/{id}", file.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(file.getId().intValue()))
            .andExpect(jsonPath("$.fileNo").value(DEFAULT_FILE_NO.toString()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.tag").value(DEFAULT_TAG.toString()))
            .andExpect(jsonPath("$.uploadDate").value(sameInstant(DEFAULT_UPLOAD_DATE)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.priority").value(DEFAULT_PRIORITY.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingFile() throws Exception {
        // Get the file
        restFileMockMvc.perform(get("/api/files/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFile() throws Exception {
        // Initialize the database
        fileRepository.saveAndFlush(file);
        fileSearchRepository.save(file);
        int databaseSizeBeforeUpdate = fileRepository.findAll().size();

        // Update the file
        File updatedFile = fileRepository.findOne(file.getId());
        updatedFile
                .fileNo(UPDATED_FILE_NO)
                .title(UPDATED_TITLE)
                .tag(UPDATED_TAG)
                .uploadDate(UPDATED_UPLOAD_DATE)
                .status(UPDATED_STATUS)
                .priority(UPDATED_PRIORITY);
        FileDTO fileDTO = fileMapper.fileToFileDTO(updatedFile);

        restFileMockMvc.perform(put("/api/files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fileDTO)))
            .andExpect(status().isOk());

        // Validate the File in the database
        List<File> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeUpdate);
        File testFile = fileList.get(fileList.size() - 1);
        assertThat(testFile.getFileNo()).isEqualTo(UPDATED_FILE_NO);
        assertThat(testFile.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testFile.getTag()).isEqualTo(UPDATED_TAG);
        assertThat(testFile.getUploadDate()).isEqualTo(UPDATED_UPLOAD_DATE);
        assertThat(testFile.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testFile.isPriority()).isEqualTo(UPDATED_PRIORITY);

        // Validate the File in Elasticsearch
        File fileEs = fileSearchRepository.findOne(testFile.getId());
        assertThat(fileEs).isEqualToComparingFieldByField(testFile);
    }

    @Test
    @Transactional
    public void updateNonExistingFile() throws Exception {
        int databaseSizeBeforeUpdate = fileRepository.findAll().size();

        // Create the File
        FileDTO fileDTO = fileMapper.fileToFileDTO(file);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restFileMockMvc.perform(put("/api/files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fileDTO)))
            .andExpect(status().isCreated());

        // Validate the File in the database
        List<File> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteFile() throws Exception {
        // Initialize the database
        fileRepository.saveAndFlush(file);
        fileSearchRepository.save(file);
        int databaseSizeBeforeDelete = fileRepository.findAll().size();

        // Get the file
        restFileMockMvc.perform(delete("/api/files/{id}", file.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean fileExistsInEs = fileSearchRepository.exists(file.getId());
        assertThat(fileExistsInEs).isFalse();

        // Validate the database is empty
        List<File> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchFile() throws Exception {
        // Initialize the database
        fileRepository.saveAndFlush(file);
        fileSearchRepository.save(file);

        // Search the file
        restFileMockMvc.perform(get("/api/_search/files?query=id:" + file.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(file.getId().intValue())))
            .andExpect(jsonPath("$.[*].fileNo").value(hasItem(DEFAULT_FILE_NO.toString())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].tag").value(hasItem(DEFAULT_TAG.toString())))
            .andExpect(jsonPath("$.[*].uploadDate").value(hasItem(sameInstant(DEFAULT_UPLOAD_DATE))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY.booleanValue())));
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(File.class);
    }
}
