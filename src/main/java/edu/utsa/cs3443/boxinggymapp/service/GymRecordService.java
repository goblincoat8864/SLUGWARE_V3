package edu.utsa.cs3443.boxinggymapp.service;

import edu.utsa.cs3443.boxinggymapp.dto.GymRecordDTO;

import java.util.List;

public interface GymRecordService {

    void saveRecord();

    List<GymRecordDTO> getRecordsForConnectedGymOwner();
}
