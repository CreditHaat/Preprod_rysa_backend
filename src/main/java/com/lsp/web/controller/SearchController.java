//package com.lsp.web.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.lsp.web.ONDCService.SearchService;
//
//@RestController
//public class SearchController {
//
//	@Autowired
//	SearchService searchService;
//
//	@PostMapping("/search")
//	public ResponseEntity<ApiResponse> search(@RequestBody SearchRequest request,
//			HttpServletRequest httpRequest) {
//		log.info("Search request received for transaction: {}", request.getContext().getTransactionId());
//		try {
//			SearchResponse response = searchService.processSearch(request, httpRequest);
//			return ResponseEntity.ok(ApiResponse.success(response));
//		} catch (Exception e) {
//			log.error("Search failed: {}", e.getMessage(), e);
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//					.body(ApiResponse.error("SEARCH_FAILED", e.getMessage()));
//		}
//	}
//
//	@PostMapping("/on_search")
//	public ResponseEntity<Void> onSearch(@Valid @RequestBody SearchResponse response, HttpServletRequest httpRequest) {
//		log.info("On_search callback received for transaction: {}", response.getContext().getTransactionId());
//		try {
//			searchService.handleSearchCallback(response, httpRequest);
//			return ResponseEntity.ok().build();
//		} catch (Exception e) {
//			log.error("On_search callback failed: {}", e.getMessage(), e);
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//		}
//	}
//
//}
