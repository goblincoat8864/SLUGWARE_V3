package edu.utsa.cs3443.boxinggymapp.service;

import edu.utsa.cs3443.boxinggymapp.dto.*;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface GymOwnerService {
    AuthentificationTokenDTO gymOwnerLogin(GymOwnerLogInRequestDTO gymOwnerLogInRequestDTO);

    GymOwnerDTO gymOwnerSignup(GymOwnerSignUpRequestDTO gymOwnerSignUpRequestDTO);

    GymOwnerDTO getCurrentUser();

    AuthentificationTokenDTO verifyToken(String token);

    void verifyPassword(String password);

    Resource getLogoCurrentGymOwner();

    void setLogoCurrentGymOwner(Integer id, MultipartFile file);

    List<GymMemberDTO> getAllGymMembers(String query);

    List<PaymentPlanDTO> getAllPaymentPlans();

    List<GymMemberDTO> getFilteredGymMembers(String name,
                                              Boolean paid,
                                              Double weightMin,
                                              Double weightMax,
                                              String paymentPlanName,
                                             LocalDate attendenceDate);

    void deleteGymMember(Integer id);

    void updateGymMember(Integer id, GymMemberSignUpRequestDTO request);

    GymMemberFileDTO uploadFile(Integer id, MultipartFile file);

    Resource getFile(Integer id);

    void setPictureGymMember(Integer id, MultipartFile picture);

    void updateBalance(Integer id, Double balance);
}
