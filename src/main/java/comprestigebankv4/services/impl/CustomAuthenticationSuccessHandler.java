package comprestigebankv4.services.impl;

import comprestigebankv4.dto.EmailDetails;
import comprestigebankv4.utils.AccountUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final EmailService emailService;

    @Autowired
    public CustomAuthenticationSuccessHandler(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // Get username from authentication
        String username = authentication.getName();

        // Generate OTP and save in session
        String otpCode = AccountUtils.generateCode();  // OTP generation
        HttpSession session = request.getSession();
        session.setAttribute("otp", otpCode);  // Save OTP in session
        session.setAttribute("otpTimestamp", System.currentTimeMillis());  // Save timestamp for OTP

        //Sending an email with an OTP code
        EmailDetails otpCodeMessage = EmailDetails.builder()
                .subject("OTP KOD!!")
                .recipient(username)
                .messageBody("Hesaba girişinizi təsdiqləmək üçün aşağıdakı birdəfəlik kodu (OTP) istifadə edin:" +
                        "\n\nKod: " + otpCode +
                        "\n\nBu kod 5 dəqiqə etibarlıdır." +
                        "\n\nKODU HEÇ KİMƏ VERMƏYİN! BANK İŞÇİLƏRİ BU KODU SİZDƏN HEÇ VAXT TƏLƏB ELƏMƏYACƏK!" +
                        "\n\nƏgər siz hesabınıza daxil olmağı tələb etməmisinizsə, lütfən, parolunuzu dəyişdirin və ya banka zəng edin.")
                .build();
        emailService.sendEmail(otpCodeMessage);

        // Redirecting the user to the OTP page
        response.sendRedirect("/OTP");
    }



}
