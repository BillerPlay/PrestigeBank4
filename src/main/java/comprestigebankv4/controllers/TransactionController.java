package comprestigebankv4.controllers;

import comprestigebankv4.dto.EmailDetails;
import comprestigebankv4.model.Account;
import comprestigebankv4.services.impl.EmailServiceImpl;
import comprestigebankv4.services.impl.UserServiceImpl;
import comprestigebankv4.utils.AccountUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;

@Controller
public class TransactionController {
    @Autowired
    EmailServiceImpl emailService;
    @Autowired
    private UserServiceImpl userService;
    @GetMapping("/transactions")
    public String transactions(Model model, HttpServletRequest request) {
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

            model.addAttribute("transactions", userService.getTransactionsHistory(account));
            model.addAttribute("account", account);
            return "transactions";
        }
    }
    @GetMapping("/deposit")
    public String depositSite(Model model, HttpServletRequest request) {
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
            return "deposit";
        }
    }
    @GetMapping("/withdraw")
    public String withdrawSite(Model model, HttpServletRequest request) {
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
            return "withdraw";
        }
    }
    @GetMapping("/send")
    public String sendSite(Model model, HttpServletRequest request) {
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
            return "redirect:/OTP";  //Redirect if OTP is not verified
        }
        else if (account == null){
            return "redirect:/login";
        }
        else {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            userService.loadUserByUsername(username);
            model.addAttribute("account", account);
            return "send";
        }
    }
    @GetMapping("/transfer")
    public String transfer(Model model, HttpServletRequest request) {
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
            return "transfer";
        }
    }
    @PostMapping("/deposit")
    public String deposit(@RequestParam BigDecimal amount, Model model) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = userService.findAccountByUsername(username);
        model.addAttribute("account", account);
        try{
            userService.deposit(account, amount);
            model.addAttribute("successMessage",  "Mədaxil uğurlu oldu!" );
        } catch (RuntimeException e){
            model.addAttribute("errorMessage", "Mədaxil uğursuz oldu: " + e.getMessage());
        }
        return "deposit";
    }

    @PostMapping("withdraw")
    public String withdraw(@RequestParam BigDecimal amount, Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = userService.findAccountByUsername(username);
        model.addAttribute("account", account);
        try{
            userService.withdraw(account, amount);
            model.addAttribute("successMessage",  "Çıxarılma uğurlu oldu!" );
        } catch (RuntimeException e){
            model.addAttribute("errorMessage", "Çıxarılma uğursuz oldu: " + e.getMessage());
        }

        return "withdraw";
    }



    @PostMapping("send")
    public String transfer(@RequestParam String toUsername, @RequestParam BigDecimal amount, Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account fromAccount = userService.findAccountByUsername(username);
        model.addAttribute("account", fromAccount);
        try {
            userService.transferAmount(fromAccount, toUsername, amount);
            model.addAttribute("successMessage", AccountUtils.maskCardNumber(toUsername) + " nömrəsinə köçürmə uğurlu oldu!" );
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Köçürmə uğursuz oldu: " + e.getMessage());
        }

        // We return to the send page, where the messages will be displayed.
        return "send";
    }

}
