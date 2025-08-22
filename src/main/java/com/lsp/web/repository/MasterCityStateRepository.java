//package com.lsp.web.repository;
//
//import com.lsp.web.entity.Master_City_State;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
//@Repository
//public interface MasterCityStateRepository extends JpaRepository<Master_City_State, Long> {
////    Optional<Master_City_State> findByPincode(String pincode);
//	Optional<Master_City_State> findByPincode(Integer pincode);
//
//}

package com.lsp.web.repository;

import com.lsp.web.entity.Master_City_State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MasterCityStateRepository extends JpaRepository<Master_City_State, Long> {
//    Optional<Master_City_State> findByPincode(String pincode);
	Optional<Master_City_State> findByPincode(Integer pincode);
	
	@Query("SELECT DISTINCT m.city FROM Master_City_State m ORDER BY m.city ASC")
    List<String> findAllDistinctCities();

}

