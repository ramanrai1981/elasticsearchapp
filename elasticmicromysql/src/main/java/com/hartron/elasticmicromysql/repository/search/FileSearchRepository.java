package com.hartron.elasticmicromysql.repository.search;

import com.hartron.elasticmicromysql.domain.File;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the File entity.
 */
public interface FileSearchRepository extends ElasticsearchRepository<File, Long> {
}
