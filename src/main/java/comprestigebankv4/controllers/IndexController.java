package comprestigebankv4.controllers;

import comprestigebankv4.dto.*;
import comprestigebankv4.services.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "User Account Management API")
@RequestMapping("/api/user")
public class IndexController {
    @Autowired
    UserService userService;
    @Operation(
            summary = "Create New user Account"
    )
    @ApiResponse(
            responseCode = "201",
            description="Http status 201 CREATED"
    )
    @PostMapping
    public BankResponse createAccount(@RequestBody UserRequest userRequest) {
        return userService.createAccount(userRequest);
    }
}
