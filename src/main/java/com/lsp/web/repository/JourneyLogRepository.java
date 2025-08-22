//package com.lsp.web.repository;
//
//import com.lsp.web.entity.JourneyLog;
//
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public interface JourneyLogRepository extends JpaRepository<JourneyLog, Integer> {
//
//    // Add any custom finders if you need them later:
//    // List<JourneyLog> findByApplyRecordId(Long applyRecordId);
//     Optional<JourneyLog> findByUId(String uid);
//     
//     List<JourneyLog> findFirstByUId(String uId);
//     
//     @Query("SELECT j FROM JourneyLog j WHERE j.UId = :UId AND j.stage = :stage ORDER BY j.createTime DESC")
//     List<JourneyLog> findByUIdAndStage(@Param("UId") String UId, @Param("stage") Integer stage);
//
//     List<JourneyLog> findByUIdOrderByCreateTimeDesc(String uId);
//
//}
//

package com.lsp.web.repository;

import com.lsp.web.entity.JourneyLog;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JourneyLogRepository extends JpaRepository<JourneyLog, Integer> {

    // Add any custom finders if you need them later:
    // List<JourneyLog> findByApplyRecordId(Long applyRecordId);
     Optional<JourneyLog> findByUId(String uid);
     
     List<JourneyLog> findFirstByUId(String uId);
     
     @Query("SELECT j FROM JourneyLog j WHERE j.UId = :UId AND j.stage = :stage ORDER BY j.createTime DESC")
     List<JourneyLog> findByUIdAndStage(@Param("UId") String UId, @Param("stage") Integer stage);

     List<JourneyLog> findByUIdOrderByCreateTimeDesc(String uId);

}


