package edu.utsa.cs3443.boxinggymapp.service.Impl;

import edu.utsa.cs3443.boxinggymapp.dto.*;
import edu.utsa.cs3443.boxinggymapp.model.GymMember;
import edu.utsa.cs3443.boxinggymapp.model.GymMemberFile;
import edu.utsa.cs3443.boxinggymapp.model.GymOwner;
import edu.utsa.cs3443.boxinggymapp.repository.GymMemberFileRepository;
import edu.utsa.cs3443.boxinggymapp.repository.GymMemberRepository;
import edu.utsa.cs3443.boxinggymapp.repository.GymOwnerRepository;
import edu.utsa.cs3443.boxinggymapp.repository.PaymentPlanRepository;
import edu.utsa.cs3443.boxinggymapp.service.AuthService;
import edu.utsa.cs3443.boxinggymapp.service.GymMemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GymMemberServiceImpl implements GymMemberService {
    private final AuthService authService;
    private final GymMemberRepository gymMemberRepository;
    private final GymOwnerRepository gymOwnerRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final PaymentPlanRepository paymentPlanRepository;
    private final GymMemberFileRepository gymMemberFileRepository;

    @Override
    public AuthentificationTokenDTO gymMemberLogin(GymMemberLogInRequestDTO gymMemberLogInRequestDTO) {
        GymMember user = gymMemberRepository.findByUsername(gymMemberLogInRequestDTO.getUsername()).orElseThrow(() -> new RuntimeException("gym Owner not found"));
        if (!passwordEncoder.matches(gymMemberLogInRequestDTO.getPin(), user.getPin())) {
            throw new RuntimeException("Invalid password");
        }

        return authService.login(user.getUsername(),"gymMember");
    }

    @Override
    public Void gymMemberSignup(GymMemberSignUpRequestDTO gymMemberSignUpRequestDTO){
        if (gymMemberRepository.findByUsername(gymMemberSignUpRequestDTO.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        String gymOwnerUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<GymOwner> gymOwnerOptional = gymOwnerRepository.findByUsername(gymOwnerUsername);
        if (gymOwnerOptional.isEmpty()){
            throw new RuntimeException("Gym Owner not authorized");
        }
        GymOwner gymOwner = gymOwnerOptional.get();

        GymMember gymMember = new GymMember();
        gymMember.setName(gymMemberSignUpRequestDTO.getName());
        gymMember.setUsername(gymMemberSignUpRequestDTO.getUsername());
        gymMember.setPin(passwordEncoder.encode(gymMemberSignUpRequestDTO.getPin()));
        gymMember.setGymOwner(gymOwner);
        gymMember.setPaymentPlan(paymentPlanRepository.findById(gymMemberSignUpRequestDTO.getPaymentPlan().getId()).orElseThrow());
        gymMember.setDateOfBirth(gymMemberSignUpRequestDTO.getDateOfBirth());
        gymMember.setPhoneNumber(gymMemberSignUpRequestDTO.getPhoneNumber());
        gymMember.setEmergencyPhoneNumber(gymMemberSignUpRequestDTO.getEmergencyPhoneNumber());
        gymMember.setWeight(gymMemberSignUpRequestDTO.getWeight());
        gymMember.setPaid(false);
        gymMember.setSignUpDate((new Date()).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate());
        gymMember.setBalance(-gymMember.getPaymentPlan().getPrice());
        gymMember.setNextPaymentDate(gymMember.getSignUpDate().plusDays(gymMember.getPaymentPlan().getFrequencyInDays()));
        gymMemberRepository.save(gymMember);
        return null;
    }

    public GymMemberDTO findById(Integer gymMemberId) {
        GymMember member = gymMemberRepository.findById(gymMemberId)
                .orElseThrow(() -> new RuntimeException("GymMember not found"));

        return modelMapper.map(member, GymMemberDTO.class);
    }

    public GymMemberFile addPdf(Integer gymMemberId, MultipartFile file) {
        GymMember member = gymMemberRepository.findById(gymMemberId)
                .orElseThrow(() -> new RuntimeException("GymMember not found"));

        if (!Objects.equals(file.getContentType(),"application/pdf")) {
            throw new IllegalArgumentException("File must be a PDF");
        }

        GymMemberFile gymMemberFile = new GymMemberFile();
        gymMemberFile.setName(file.getOriginalFilename());
        try {
            gymMemberFile.setContent(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        gymMemberFile.setGymMember(member);

        return gymMemberFileRepository.save(gymMemberFile);
    }

    public List<GymMemberFile> getPdfs(Integer gymMemberId) {
        GymMember member = gymMemberRepository.findById(gymMemberId)
                .orElseThrow(() -> new RuntimeException("GymMember not found"));

        return member.getPdfs();
    }

    @Transactional
    public void checkAndUpdatePayments() {
        LocalDate today = LocalDate.now();
        List<GymMember> members = gymMemberRepository.findAll();

        for (GymMember member : members) {
            if (member.getNextPaymentDate().isBefore(today)) {
                member.setBalance(member.getBalance()-member.getPaymentPlan().getPrice());
                member.setPaid(member.getBalance()>=0);
                member.setNextPaymentDate(member.getNextPaymentDate().plusDays(member.getPaymentPlan().getFrequencyInDays()));
            }
        }

        gymMemberRepository.saveAll(members);
    }

    @Override
    public GymMemberDTO getCurrentGymMember(){
        return modelMapper.map(gymMemberRepository.findByUsername(authService.getCurrentUser()), GymMemberDTO.class);
    }
}
