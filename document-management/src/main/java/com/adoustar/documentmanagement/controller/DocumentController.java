package com.adoustar.documentmanagement.controller;

import com.adoustar.documentmanagement.domain.Response;
import com.adoustar.documentmanagement.domain.dto.User;
import com.adoustar.documentmanagement.domain.dtoRequest.UpdateDocRequest;
import com.adoustar.documentmanagement.service.DocumentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static com.adoustar.documentmanagement.utils.RequestUtil.getResponse;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('document:create') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> saveDocuments(@AuthenticationPrincipal User user, @RequestParam("files") List<MultipartFile> documents, HttpServletRequest request) {
        var newDocuments = documentService.saveDocuments(user.getUserId(), documents);
        return ResponseEntity.created(URI.create("")).body(getResponse(request, Map.of("documents", newDocuments), "Document(s) uploaded.", CREATED));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('document:read') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> getDocuments(@AuthenticationPrincipal User user, HttpServletRequest request,
                                                @RequestParam(value = "page", defaultValue = "0") int page,
                                                @RequestParam(value = "size", defaultValue = "5") int size) {
        var documents = documentService.getDocuments(page, size);
        return ResponseEntity.ok(getResponse(request, Map.of("documents", documents), "Document(s) retrieved successfully", HttpStatus.OK));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('document:read') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> searchDocuments(@AuthenticationPrincipal User user, HttpServletRequest request,
                                                @RequestParam(value = "page", defaultValue = "0") int page,
                                                @RequestParam(value = "size", defaultValue = "5") int size,
                                                    @RequestParam(value = "name", defaultValue = "") String name) {
        var documents = documentService.getDocuments(page, size, name);
        return ResponseEntity.ok(getResponse(request, Map.of("documents", documents), "Document(s) retrieved successfully", HttpStatus.OK));
    }

    @GetMapping("/{documentId}")
    @PreAuthorize("hasAuthority('document:read') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> getDocument(@AuthenticationPrincipal User user, @PathVariable("documentId") String documentId, HttpServletRequest request) {
        var document = documentService.getDocumentByDocumentId(documentId);
        return ResponseEntity.ok(getResponse(request, Map.of("document", document), "Document retrieved successfully", HttpStatus.OK));
    }

    @PatchMapping("/update")
    @PreAuthorize("hasAuthority('document:update') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> updateDocument(@AuthenticationPrincipal User user, @RequestBody UpdateDocRequest document, HttpServletRequest request) {
        var updatedDocument = documentService.updateDocument(document.getDocumentId(), document.getName(), document.getDescription());
        return ResponseEntity.ok(getResponse(request, Map.of("document", updatedDocument), "Document updated", HttpStatus.OK));
    }

    @GetMapping("/download/{documentName}")
    @PreAuthorize("hasAuthority('document:read') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Resource> downloadDocument(@AuthenticationPrincipal User user, @PathVariable("documentName") String documentName, HttpServletRequest request) throws IOException {
        var resource = documentService.getResource(documentName);
        var httpHeaders = new HttpHeaders();
        httpHeaders.add("File-Name", documentName);
        // Specifying that we need to download - content-disposition and attachment are required
        httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment;File-Name=%s", resource.getFilename()));
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(Files.probeContentType(resource.getFile().toPath())))
                .headers(httpHeaders).body(resource);
    }
}