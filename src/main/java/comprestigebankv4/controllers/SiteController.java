package comprestigebankv4.controllers;

import comprestigebankv4.dto.AccountUpdateRequest;
import comprestigebankv4.dto.BankResponse;
import comprestigebankv4.dto.EmailDetails;
import comprestigebankv4.dto.UserRequest;
import comprestigebankv4.model.Account;
import comprestigebankv4.repositories.AccountRepository;
import comprestigebankv4.services.impl.EmailServiceImpl;
import comprestigebankv4.services.impl.UserServiceImpl;
import comprestigebankv4.utils.AccountUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
public class SiteController {

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    EmailServiceImpl emailService;

    @GetMapping("/index")
    public String index(Model model) {
        return "index";
    }
    @GetMapping("/login")
    public String login() {
        // Getting the authentication object
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // If the user is authenticated (not "anonymousUser"), redirect to dashboard
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/dashboard";
        }

        // If not authenticated, display login page
        return "login";
    }

    @GetMapping("/OTP")
    public String otpPage() {
        return "OTP";  // Displaying the page for entering OTP
    }

    @PostMapping("/OTP")
    public String verifyOtp(@RequestParam String code, HttpSession session, Model model, Authentication authentication) {
        String sessionCode = (String) session.getAttribute("otp");
        Long timestamp = (Long) session.getAttribute("otpTimestamp");

        if (sessionCode == null || timestamp == null) {
            model.addAttribute("error", "OTP Kod göndərilməyib");
            return "/login";
        }
        if (System.currentTimeMillis() - timestamp < 300000) {  // Checking OTP expiration time
            if (sessionCode.equals(code)) {
                String username = authentication.getName();
                EmailDetails loginAlert = EmailDetails.builder()
                        .subject("SİZ DAXİI OLMUSUNUZ!")
                        .recipient(username)
                        .messageBody("Siz öz hesabınıza " + LocalDateTime.now() + "-də daxil olmusunuz." +
                                "\nƏgər siz deyildinizsə, lütfən, parolunuzu dəyişdirin və ya banka zəng edin.")
                        .build();
                emailService.sendEmail(loginAlert);
                Account account = userService.findAccountByUsername(username);
                userService.loadUserByUsername(username);
                account.setOtpVerified(true);  // Set otpVerified to true
                accountRepository.save(account);
                model.addAttribute("account", account);
                // Успешная верификация
                return "redirect:/dashboard";
            } else {
                model.addAttribute("error", "OTP kod düzgün daxil edilməyib!");
                return "/login";
            }
        } else {
            model.addAttribute("error", "OTP kodun vaxtı keçib!");
            return "/login";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        Account account = userService.findAccountByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        if (!account.isOtpVerified()) {
            String otpCode = AccountUtils.generateCode();  // OTP generation
            HttpSession session = request.getSession();
            session.setAttribute("otp", otpCode);  // Save OTP in session
            session.setAttribute("otpTimestamp", System.currentTimeMillis());
            EmailDetails otpCodeMessage = EmailDetails.builder()
                    .subject("OTP KOD!!")
                    .recipient(account.getUsername())
                    .messageBody("Hesaba girişinizi təsdiqləmək üçün aşağıdakı birdəfəlik kodu (OTP) istifadə edin:" +
                            "\n\nKod: " + otpCode +
                            "\n\nBu kod 5 dəqiqə etibarlıdır." +
                            "\n\nKODU HEÇ KİMƏ VERMƏYİN! BANK İŞÇİLƏRİ BU KODU SİZDƏN HEÇ VAXT TƏLƏB ELƏMƏYACƏK!" +
                            "\n\nƏgər siz hesabınıza daxil olmağı tələb etməmisinizsə, lütfən, parolunuzu dəyişdirin və ya banka zəng edin.")
                    .build();
            emailService.sendEmail(otpCodeMessage);
            return "redirect:/OTP";  // Redirect if OTP is not verified
        }
        else if (account == null){
            return "redirect:/login";
        }
        else{
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            userService.loadUserByUsername(username);
            model.addAttribute("account", account);
            return "dashboard";
        }
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userRequest", new UserRequest());  // Creating an object for registration
        return "registration";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserRequest userRequest, Model model) {
        BankResponse response = userService.createAccount(userRequest);

        if ("Account creation success".equals(response.getResponseCode())) {
            model.addAttribute("responseMessage", "Hesab uğurla yaradıldı! Ətraflı məlumat üçün e-poçtunuzu yoxlayın.");
        } else {
            model.addAttribute("responseMessage", response.getResponseMessage());
        }

        return "registration";
    }
    @GetMapping("/setting")
    public String showSettingsForm(Model model, HttpServletRequest request) {
        Account account = userService.findAccountByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        if (!account.isOtpVerified()) {
            String otpCode = AccountUtils.generateCode();  // OTP generation
            HttpSession session = request.getSession();
            session.setAttribute("otp", otpCode);  // Save OTP in session
            session.setAttribute("otpTimestamp", System.currentTimeMillis());
            EmailDetails otpCodeMessage = EmailDetails.builder()
                    .subject("OTP KOD!!")
                    .recipient(account.getUsername())
                    .messageBody("Hesaba girişinizi təsdiqləmək üçün aşağıdakı birdəfəlik kodu (OTP) istifadə edin:" +
                            "\n\nKod: " + otpCode +
                            "\n\nBu kod 5 dəqiqə etibarlıdır." +
                            "\n\nKODU HEÇ KİMƏ VERMƏYİN! BANK İŞÇİLƏRİ BU KODU SİZDƏN HEÇ VAXT TƏLƏB ELƏMƏYACƏK!" +
                            "\n\nƏgər siz hesabınıza daxil olmağı tələb etməmisinizsə, lütfən, parolunuzu dəyişdirin və ya banka zəng edin.")
                    .build();
            emailService.sendEmail(otpCodeMessage);
            return "redirect:/OTP";  // Redirect if OTP is not verified
        }
        else if (account == null){
            return "redirect:/login";
        }
        else {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            userService.loadUserByUsername(username);
            model.addAttribute("account", account);
            model.addAttribute("accountUpdateRequest", new AccountUpdateRequest());  // Creating an object for registration
            return "settings";
        }
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        Account account = userService.findAccountByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        account.setOtpVerified(false);
        accountRepository.save(account);
        session.invalidate();
        return "redirect:/login";
    }
    @PostMapping("/setting")
    public String updateAccount(@ModelAttribute("userRequest") @Valid AccountUpdateRequest accountUpdateRequest, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("responseMessage", "Form has errors. Please fix the issues and try again.");
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Account account = userService.findAccountByUsername(username);
            userService.loadUserByUsername(username);
            model.addAttribute("account", account);
            model.addAttribute("accountUpdateRequest", new AccountUpdateRequest());
            return "settings";
        }

        try {
            userService.changeDetails(accountUpdateRequest);
            model.addAttribute("responseMessage", "Hesab təfərrüatları uğurla yeniləndi.");
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Account account = userService.findAccountByUsername(username);
            userService.loadUserByUsername(username);
            model.addAttribute("account", account);
            model.addAttribute("accountUpdateRequest", new AccountUpdateRequest());
            account.setOtpVerified(false);
            accountRepository.save(account);
            return "redirect:/logout";
        } catch (Exception e) {
            model.addAttribute("responseMessage", "Error updating account: " + e.getMessage());
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Account account = userService.findAccountByUsername(username);
            userService.loadUserByUsername(username);
            model.addAttribute("account", account);
            model.addAttribute("accountUpdateRequest", new AccountUpdateRequest());
            return "settings";
        }
    }
}

