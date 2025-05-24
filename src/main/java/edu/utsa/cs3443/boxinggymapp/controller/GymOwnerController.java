package edu.utsa.cs3443.boxinggymapp.controller;

import edu.utsa.cs3443.boxinggymapp.api.GymOwnersApiDelegate;
import edu.utsa.cs3443.boxinggymapp.dto.*;
import edu.utsa.cs3443.boxinggymapp.service.GymOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class GymOwnerController implements GymOwnersApiDelegate {
    private final GymOwnerService gymOwnerService;

    @Override
    public ResponseEntity<AuthentificationTokenDTO> gymOwnerLogin(GymOwnerLogInRequestDTO gymOwnerLogInRequestDTO) {
        return ResponseEntity.ok(gymOwnerService.gymOwnerLogin(gymOwnerLogInRequestDTO));
    }

    @Override
    public ResponseEntity<GymOwnerDTO> gymOwnerSignup(GymOwnerSignUpRequestDTO gymOwnerSignUpRequestDTO) {
        return ResponseEntity.ok(gymOwnerService.gymOwnerSignup(gymOwnerSignUpRequestDTO));
    }

    @Override
    public ResponseEntity<Resource> getLogoCurrentGymOwner(){
        return ResponseEntity.ok(gymOwnerService.getLogoCurrentGymOwner());
    }

    @Override
    public ResponseEntity<GymOwnerDTO> getCurrentGymOwner(){
        return ResponseEntity.ok(gymOwnerService.getCurrentUser());
    }

    @Override
    public ResponseEntity<AuthentificationTokenDTO> verifyToken(String token){
        String reformulatedToken = token.substring(1,token.length()-1);
        return ResponseEntity.ok(gymOwnerService.verifyToken(reformulatedToken));
    }

    @Override
    public ResponseEntity<Void> verifyPassword(String password){
        String reformulatedPassword = password.substring(1,password.length()-1);
        gymOwnerService.verifyPassword(reformulatedPassword);
        return ResponseEntity.ok().build();
    }
    @Override
    public ResponseEntity<Void> setLogoCurrentGymOwner(Integer id, MultipartFile file){
        gymOwnerService.setLogoCurrentGymOwner(id, file);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<GymMemberDTO>> getAllGymMembers(String query){
        return ResponseEntity.ok(gymOwnerService.getAllGymMembers(query));
    }

    @Override
    public ResponseEntity<List<PaymentPlanDTO>> getAllPaymentPlans(){
        return ResponseEntity.ok(gymOwnerService.getAllPaymentPlans());
    }

    @Override
    public ResponseEntity<List<GymMemberDTO>> getFilteredGymMembers(String name,
                                                                    Boolean paid,
                                                                    Double weightMin,
                                                                    Double weightMax,
                                                                    String paymentPlanName,
                                                                    LocalDate attendenceDate){
        return ResponseEntity.ok(gymOwnerService.getFilteredGymMembers(name, paid, weightMin, weightMax, paymentPlanName, attendenceDate));
    }

    @Override
    public ResponseEntity<Void> deleteGymMember(Integer id) {
        gymOwnerService.deleteGymMember(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> updateGymMember(Integer id, GymMemberSignUpRequestDTO gymMemberSignUpRequestDTO) {
        gymOwnerService.updateGymMember(id, gymMemberSignUpRequestDTO);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<GymMemberFileDTO> uploadFile(Integer id, MultipartFile file) {
        return ResponseEntity.ok(gymOwnerService.uploadFile(id, file));
    }

    @Override
    public ResponseEntity<Resource> getFile(Integer id) {
        return ResponseEntity.ok(gymOwnerService.getFile(id));
    }

    @Override
    public ResponseEntity<Void> setPictureGymMember(Integer id, MultipartFile picture) {
        gymOwnerService.setPictureGymMember(id,picture);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> updateBalance(Integer id, Double balance){
        gymOwnerService.updateBalance(id,balance);
        return ResponseEntity.ok().build();
    }
}
