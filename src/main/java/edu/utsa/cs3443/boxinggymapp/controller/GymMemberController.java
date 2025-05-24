package edu.utsa.cs3443.boxinggymapp.controller;

import edu.utsa.cs3443.boxinggymapp.api.GymMembersApiDelegate;
import edu.utsa.cs3443.boxinggymapp.dto.AuthentificationTokenDTO;
import edu.utsa.cs3443.boxinggymapp.dto.GymMemberDTO;
import edu.utsa.cs3443.boxinggymapp.dto.GymMemberLogInRequestDTO;
import edu.utsa.cs3443.boxinggymapp.dto.GymMemberSignUpRequestDTO;
import edu.utsa.cs3443.boxinggymapp.service.GymMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GymMemberController implements GymMembersApiDelegate {
    private final GymMemberService gymMemberService;

    @Override
    public ResponseEntity<AuthentificationTokenDTO> gymMemberLogin(GymMemberLogInRequestDTO gymMemberLogInRequestDTO) {
        return ResponseEntity.ok(gymMemberService.gymMemberLogin(gymMemberLogInRequestDTO));
    }

    @Override
    public ResponseEntity<Void> gymMemberSignup(GymMemberSignUpRequestDTO gymMemberSignUpRequestDTO) {
        return ResponseEntity.ok(gymMemberService.gymMemberSignup(gymMemberSignUpRequestDTO));
    }

    @Override
    public ResponseEntity<GymMemberDTO> getCurrentGymMember(){
        return ResponseEntity.ok(gymMemberService.getCurrentGymMember());
    }

    @Override
    public ResponseEntity<GymMemberDTO> getGymMemberById(Integer id){
        return ResponseEntity.ok(gymMemberService.findById(id));
    }
}
