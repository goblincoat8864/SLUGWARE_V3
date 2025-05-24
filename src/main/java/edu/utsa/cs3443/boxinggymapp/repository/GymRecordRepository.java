package edu.utsa.cs3443.boxinggymapp.repository;

import edu.utsa.cs3443.boxinggymapp.model.GymRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GymRecordRepository extends JpaRepository<GymRecord, Integer> {

    @Query("SELECT gr FROM GymRecord gr WHERE gr.gymOwner.username = :gymOwnerUsername")
    List<GymRecord> findByGymOwnerUsername(@Param("gymOwnerUsername") String gymOwnerUsername);

}
