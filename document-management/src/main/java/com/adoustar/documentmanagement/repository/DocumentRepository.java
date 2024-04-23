package com.adoustar.documentmanagement.repository;

import com.adoustar.documentmanagement.domain.dto.api.IDocument;
import com.adoustar.documentmanagement.entity.DocumentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.adoustar.documentmanagement.constant.Constant.*;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {
    @Query(countQuery = SELECT_COUNT_DOCUMENTS_QUERY,
            value = SELECT_DOCUMENTS_QUERY,
            nativeQuery = true)
    Page<IDocument> findDocuments(Pageable pageable);

    @Query(countQuery = SELECT_COUNT_DOCUMENTS_BY_NAME_QUERY,
            value = SELECT_DOCUMENTS_BY_NAME_QUERY,
            nativeQuery = true)
    Page<IDocument> findDocumentsByName(@Param("documentName") String documentName, PageRequest name);

    @Query(value = SELECT_DOCUMENT_QUERY,
            nativeQuery = true)
    Optional<IDocument> findDocumentByDocumentId(String documentId);

    Optional<DocumentEntity> findByDocumentId(String documentId);
}
