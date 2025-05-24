package edu.utsa.cs3443.boxinggymapp.controller;

import edu.utsa.cs3443.boxinggymapp.api.GymRecordsApiDelegate;
import edu.utsa.cs3443.boxinggymapp.dto.GymRecordDTO;
import edu.utsa.cs3443.boxinggymapp.service.GymRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GymRecordController implements GymRecordsApiDelegate {

    private final GymRecordService gymRecordService;

    @Override
    public ResponseEntity<Void> saveRecord() {
        gymRecordService.saveRecord();
        return ResponseEntity.ok(null);
    }

    @Override
    public ResponseEntity<List<GymRecordDTO>> getRecordsForConnectedGymOwner(){
        return ResponseEntity.ok(gymRecordService.getRecordsForConnectedGymOwner());
    }
}
