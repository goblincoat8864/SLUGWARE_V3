package edu.utsa.cs3443.boxinggymapp.repository;

import edu.utsa.cs3443.boxinggymapp.model.GymMemberFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GymMemberFileRepository extends JpaRepository<GymMemberFile, Integer> {
}
