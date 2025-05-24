package edu.utsa.cs3443.boxinggymapp.service;

import edu.utsa.cs3443.boxinggymapp.dto.AuthentificationTokenDTO;
import edu.utsa.cs3443.boxinggymapp.dto.GymMemberDTO;
import edu.utsa.cs3443.boxinggymapp.dto.GymMemberLogInRequestDTO;
import edu.utsa.cs3443.boxinggymapp.dto.GymMemberSignUpRequestDTO;
import edu.utsa.cs3443.boxinggymapp.model.GymMemberFile;
import org.springframework.web.multipart.MultipartFile;

public interface GymMemberService {
    AuthentificationTokenDTO gymMemberLogin(GymMemberLogInRequestDTO gymMemberLogInRequestDTO);

    Void gymMemberSignup(GymMemberSignUpRequestDTO gymMemberSignUpRequestDTO);

    GymMemberFile addPdf(Integer gymMemberId, MultipartFile file);

    void checkAndUpdatePayments();

    GymMemberDTO getCurrentGymMember();

    GymMemberDTO findById(Integer gymMemberId);
}
