package com.lsp.web.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lsp.web.entity.Callback;

public interface CallbackRepository extends JpaRepository<Callback, Long> {
	List<Callback> findByuID(String uid);
	
	@Query("select c from Callback c where c.uID = ?1 ORDER BY c.createTime DESC LIMIT 1")
	Optional<Callback> findByuIdLatest(String uid);
	
//	@Query(value = "SELECT * FROM callback " +
//            "WHERE content->'context'->>'bpp_id' = :bppId " +
//            "AND content->'context'->>'transaction_id' = :transactionId " +
//            "ORDER BY create_time DESC LIMIT 1",
//    nativeQuery = true)
//Optional<Callback> findByTransactionIdAndBppId(@Param("transactionId") String transactionId,
//                                  @Param("bppId") String bppId);
	
	@Query(value = "SELECT * FROM t_callback " +
            "WHERE callback_content IS NOT NULL " +
            "AND callback_content <> '' " +
            "AND JSON_VALID(callback_content) " +
            "AND JSON_UNQUOTE(JSON_EXTRACT(callback_content, '$.context.bpp_id')) = :bppId " +
            "AND JSON_UNQUOTE(JSON_EXTRACT(callback_content, '$.context.transaction_id')) = :transactionId " +
            "ORDER BY createtime DESC LIMIT 1",
    nativeQuery = true)
Optional<Callback> findByTransactionIdAndBppId(@Param("transactionId") String transactionId,
                                  @Param("bppId") String bppId);
	
	@Query(value = "SELECT * FROM t_callback " +
            "WHERE callback_content IS NOT NULL " +
            "AND callback_content <> '' " +
            "AND JSON_VALID(callback_content) " +
            "AND JSON_UNQUOTE(JSON_EXTRACT(callback_content, '$.context.bpp_id')) = :bppId " +
            "AND JSON_UNQUOTE(JSON_EXTRACT(callback_content, '$.context.transaction_id')) = :transactionId " +
            "AND JSON_UNQUOTE(JSON_EXTRACT(callback_content, '$.context.action')) <> :skipAction " +
            "ORDER BY createtime DESC LIMIT 1",
    nativeQuery = true)
Optional<Callback> findLatestExcludingAction(@Param("transactionId") String transactionId,
                                             @Param("bppId") String bppId,
                                             @Param("skipAction") String skipAction);


}
