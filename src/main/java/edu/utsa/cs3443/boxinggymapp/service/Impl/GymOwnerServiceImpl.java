package edu.utsa.cs3443.boxinggymapp.service.Impl;

import edu.utsa.cs3443.boxinggymapp.dto.*;
import edu.utsa.cs3443.boxinggymapp.model.GymMember;
import edu.utsa.cs3443.boxinggymapp.model.GymMemberFile;
import edu.utsa.cs3443.boxinggymapp.model.GymOwner;
import edu.utsa.cs3443.boxinggymapp.model.PaymentPlan;
import edu.utsa.cs3443.boxinggymapp.repository.GymMemberFileRepository;
import edu.utsa.cs3443.boxinggymapp.repository.GymMemberRepository;
import edu.utsa.cs3443.boxinggymapp.repository.GymOwnerRepository;
import edu.utsa.cs3443.boxinggymapp.repository.PaymentPlanRepository;
import edu.utsa.cs3443.boxinggymapp.service.AuthService;
import edu.utsa.cs3443.boxinggymapp.service.GymMemberService;
import edu.utsa.cs3443.boxinggymapp.service.GymOwnerService;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GymOwnerServiceImpl implements GymOwnerService {
    private final AuthService authService;
    private final GymOwnerRepository gymOwnerRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final GymMemberRepository gymMemberRepository;
    private final GymMemberService gymMemberService;
    private final PaymentPlanRepository paymentPlanRepository;
    private final GymMemberFileRepository gymMemberFileRepository;

    @Override
    public AuthentificationTokenDTO gymOwnerLogin(GymOwnerLogInRequestDTO gymOwnerLogInRequestDTO) {
        GymOwner gymOwner = gymOwnerRepository.findByUsername(gymOwnerLogInRequestDTO.getUsername()).orElseThrow(() -> new RuntimeException("gym Owner not found"));
        if (!passwordEncoder.matches(gymOwnerLogInRequestDTO.getPassword(), gymOwner.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return authService.login(gymOwner.getUsername(),"gymOwner");
    }

    @Override
    public GymOwnerDTO gymOwnerSignup(GymOwnerSignUpRequestDTO gymOwnerSignUpRequestDTO){
        if (gymOwnerRepository.findByUsername(gymOwnerSignUpRequestDTO.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        GymOwner gymOwner = new GymOwner();
        gymOwner.setName(gymOwnerSignUpRequestDTO.getName());
        gymOwner.setGymAddress(gymOwnerSignUpRequestDTO.getGymAddress());
        gymOwner.setPhoneNumber(gymOwnerSignUpRequestDTO.getPhoneNumber());
        gymOwner.setUsername(gymOwnerSignUpRequestDTO.getUsername());
        gymOwner.setPassword(passwordEncoder.encode(gymOwnerSignUpRequestDTO.getPassword()));
        Set<PaymentPlan> paymentPlans = gymOwnerSignUpRequestDTO.getPaymentPlans().stream()
                .map(planDTO -> {
                    PaymentPlan paymentPlan = new PaymentPlan();
                    paymentPlan.setName(planDTO.getName());
                    paymentPlan.setPrice(planDTO.getPrice());
                    paymentPlan.setFrequencyInDays(planDTO.getFrequencyInDays());
                    paymentPlan.setGymOwner(gymOwner);
                    return paymentPlan;
                }).collect(Collectors.toSet());
        gymOwner.setPaymentPlans(paymentPlans);
        GymOwner savedGymOwner = gymOwnerRepository.save(gymOwner);

        return modelMapper.map(savedGymOwner,GymOwnerDTO.class);
    }

    @Override
    public GymOwnerDTO getCurrentUser(){
        return modelMapper.map(gymOwnerRepository.findByUsername(authService.getCurrentUser()),GymOwnerDTO.class);
    }

    @Override
    public AuthentificationTokenDTO verifyToken(String token){
        authService.getUsernameFromJWT(token);
        return new AuthentificationTokenDTO();
    }

    @Override
    public void verifyPassword(String password){
        String gymOwnerUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<GymOwner> gymOwnerOptional = gymOwnerRepository.findByUsername(gymOwnerUsername);
        if (gymOwnerOptional.isEmpty()){
            throw new RuntimeException("Gym Owner not found");
        }
        if (!passwordEncoder.matches(password,gymOwnerOptional.get().getPassword())){
            throw new RuntimeException("Wrong password");
        }
    }

    @Override
    public Resource getLogoCurrentGymOwner(){
        String gymOwnerUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<GymOwner> gymOwnerOptional = gymOwnerRepository.findByUsername(gymOwnerUsername);
        if (gymOwnerOptional.isEmpty()){
            throw new RuntimeException("Gym Owner not authorized");
        }
        return gymOwnerOptional.get().getLogo()==null ? null : new ByteArrayResource(gymOwnerOptional.get().getLogo());
    }

    @Override
    public void setLogoCurrentGymOwner(Integer id, MultipartFile logoFile){
        Optional<GymOwner> gymOwnerOptional = gymOwnerRepository.findById(id);
        if (gymOwnerOptional.isEmpty()){
            throw new RuntimeException("Gym Owner not authorized");
        }
        GymOwner gymOwner = gymOwnerOptional.get();
        if (logoFile != null && !logoFile.isEmpty()) {
            try {
                gymOwner.setLogo(logoFile.getBytes());
            } catch (IOException e) {
            }
        }
        gymOwnerRepository.save(gymOwner);
    }

    @Override
    public List<GymMemberDTO> getAllGymMembers(String query){
        String gymOwnerUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<GymOwner> gymOwnerOptional = gymOwnerRepository.findByUsername(gymOwnerUsername);
        if (gymOwnerOptional.isEmpty()){
            throw new RuntimeException("Gym Owner not authorized");
        }
        return gymMemberRepository.findByGymOwnerAndUsernameContainingIgnoreCase(gymOwnerOptional.get(), query)
                .stream().map(
                        gymMember -> modelMapper.map(gymMember, GymMemberDTO.class)
                ).toList();
    }

    @Override
    public List<PaymentPlanDTO> getAllPaymentPlans() {
        String gymOwnerUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<GymOwner> gymOwnerOptional = gymOwnerRepository.findByUsername(gymOwnerUsername);
        if (gymOwnerOptional.isEmpty()){
            throw new RuntimeException("Gym Owner not authorized");
        }
        return gymOwnerOptional.get().getPaymentPlans().stream()
                .map(plan -> modelMapper.map(plan, PaymentPlanDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<GymMemberDTO> getFilteredGymMembers(String name, Boolean paid, Double weightMin, Double weightMax, String paymentPlanName, LocalDate attendenceDate) {
        String gymOwnerUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<GymOwner> gymOwnerOptional = gymOwnerRepository.findByUsername(gymOwnerUsername);
        if (gymOwnerOptional.isEmpty()){
            throw new RuntimeException("Gym Owner not authorized");
        }
        LocalDateTime startOfDay = null;
        LocalDateTime endOfDay = null;
        if (attendenceDate != null) {
            startOfDay = attendenceDate.atStartOfDay();
            endOfDay = attendenceDate.atTime(23, 59, 59);
        }

        return gymMemberRepository.findMembersWithFilters(gymOwnerOptional.get().getId() ,name, paid, weightMin, weightMax, paymentPlanName, startOfDay, endOfDay)
                .stream()
                .map(gymMember -> {
                    GymMemberDTO gymMemberDTO = new GymMemberDTO();
                    gymMemberDTO.setId(gymMember.getId());
                    gymMemberDTO.setName(gymMember.getName());
                    gymMemberDTO.setUsername(gymMember.getUsername());
                    gymMemberDTO.setDateOfBirth(gymMember.getDateOfBirth());
                    gymMemberDTO.setPhoneNumber(gymMember.getPhoneNumber());
                    gymMemberDTO.setEmergencyPhoneNumber(gymMember.getEmergencyPhoneNumber());
                    gymMemberDTO.setWeight(gymMember.getWeight());
                    gymMemberDTO.setPin(gymMember.getPin());
                    gymMemberDTO.setBalance(gymMember.getBalance());

                    PaymentPlanDTO paymentPlanDTO = new PaymentPlanDTO();
                    paymentPlanDTO.setId(gymMember.getPaymentPlan().getId());
                    paymentPlanDTO.setName(gymMember.getPaymentPlan().getName());
                    paymentPlanDTO.setPrice(gymMember.getPaymentPlan().getPrice());
                    paymentPlanDTO.setFrequencyInDays(gymMember.getPaymentPlan().getFrequencyInDays());
                    gymMemberDTO.setPaymentPlan(paymentPlanDTO);

                    List<GymMemberFileDTO> gymMemberFileDTOS = gymMember.getPdfs().stream().map(pdf -> {
                        GymMemberFileDTO gymMemberFileDTO = new GymMemberFileDTO();
                        gymMemberFileDTO.setId(pdf.getId());
                        gymMemberFileDTO.setName(pdf.getName());
                        return gymMemberFileDTO;
                    }).toList();
                    gymMemberDTO.setPdfs(gymMemberFileDTOS);

                    if (gymMember.getPicture() != null){
                        gymMemberDTO.setPicture(Base64.getEncoder().encodeToString(gymMember.getPicture()));
                    }
                    return gymMemberDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteGymMember(Integer id) {
        if (!gymMemberRepository.existsById(id)) {
            throw new EntityNotFoundException("GymMember with id " + id + " not found");
        }
        gymMemberRepository.deleteById(id);
    }

    @Override
    public void updateGymMember(Integer id, GymMemberSignUpRequestDTO request) {
        GymMember gymMember = gymMemberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("GymMember with id " + id + " not found"));

        gymMember.setName(request.getName());
        gymMember.setDateOfBirth(request.getDateOfBirth());
        gymMember.setPhoneNumber(request.getPhoneNumber());
        gymMember.setEmergencyPhoneNumber(request.getEmergencyPhoneNumber());
        gymMember.setWeight(request.getWeight());
        gymMember.setPaymentPlan(paymentPlanRepository.findByName(request.getPaymentPlan().getName()));
        gymMember.setUsername(request.getUsername());
        if (!StringUtils.isEmpty(request.getPin())) {
            gymMember.setPin(passwordEncoder.encode(request.getPin()));
        }

        gymMemberRepository.save(gymMember);
    }

    @Override
    @Transactional
    public GymMemberFileDTO uploadFile(Integer id, MultipartFile file) {
        try {
            return this.modelMapper.map(gymMemberService.addPdf(id, file), GymMemberFileDTO.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    @Transactional
    public Resource getFile(Integer id) {
        try {
            GymMemberFile file = gymMemberFileRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Unfound file"));
            return file.getContent()==null ? null : new ByteArrayResource(file.getContent());
        } catch (Exception e){
            return null;
        }
    }

    @Override
    public void setPictureGymMember(Integer id, MultipartFile picture){
        Optional<GymMember> gymMemberOptional = gymMemberRepository.findById(id);
        if (gymMemberOptional.isEmpty()){
            throw new RuntimeException("Gym member not found");
        }
        GymMember gymMember = gymMemberOptional.get();
        if (picture != null && !picture.isEmpty()) {
            try {
                gymMember.setPicture(picture.getBytes());
            } catch (IOException e) {
            }
        }
        gymMemberRepository.save(gymMember);
    }

    @Override
    public void updateBalance(Integer id, Double balance){
        Optional<GymMember> gymMemberOptional = gymMemberRepository.findById(id);
        if (gymMemberOptional.isEmpty()){
            throw new RuntimeException("Gym member not found");
        }
        GymMember gymMember = gymMemberOptional.get();
        gymMember.setBalance(balance);
        gymMember.setPaid(balance >= 0);
        gymMemberRepository.save(gymMember);
    }
}
