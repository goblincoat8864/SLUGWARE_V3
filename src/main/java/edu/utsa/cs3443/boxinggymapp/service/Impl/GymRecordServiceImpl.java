package edu.utsa.cs3443.boxinggymapp.service.Impl;

import edu.utsa.cs3443.boxinggymapp.dto.GymRecordDTO;
import edu.utsa.cs3443.boxinggymapp.model.GymMember;
import edu.utsa.cs3443.boxinggymapp.model.GymRecord;
import edu.utsa.cs3443.boxinggymapp.repository.GymMemberRepository;
import edu.utsa.cs3443.boxinggymapp.repository.GymRecordRepository;
import edu.utsa.cs3443.boxinggymapp.service.GymRecordService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GymRecordServiceImpl implements GymRecordService {

    private final GymRecordRepository gymRecordRepository;
    private final GymMemberRepository gymMemberRepository;
    private final ModelMapper modelMapper;

    @Override
    public void saveRecord(){
        String gymMemberUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<GymMember> gymMemberOptional = gymMemberRepository.findByUsername(gymMemberUsername);
        if (gymMemberOptional.isEmpty()){
            throw new RuntimeException("Gym Member not authorized");
        }
        GymMember gymMember = gymMemberOptional.get();
        GymRecord gymRecord = new GymRecord();
        gymRecord.setGymMember(gymMember);
        gymRecord.setGymOwner(gymMember.getGymOwner());
        gymRecord.setRecordedAt(LocalDateTime.now());
        gymRecordRepository.save(gymRecord);
    }

    @Override
    public List<GymRecordDTO> getRecordsForConnectedGymOwner() {
        String gymOwnerUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        List<GymRecord> gymRecords = gymRecordRepository.findByGymOwnerUsername(gymOwnerUsername);
        return gymRecords.stream().map(record -> {
                    GymRecordDTO gymRecordDTO = modelMapper.map(record, GymRecordDTO.class);
                    gymRecordDTO.setDateTime(record.getRecordedAt().atZone(ZoneId.systemDefault()).toOffsetDateTime());
                    return gymRecordDTO;
                }
        ).toList();
    }
}
