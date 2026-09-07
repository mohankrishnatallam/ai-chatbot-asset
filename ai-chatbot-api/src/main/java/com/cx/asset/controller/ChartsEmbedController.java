package com.cx.asset.controller;

import com.cx.asset.dto.ChartsEmbedTokenResponse;
import com.cx.asset.service.ChartsEmbedTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static com.cx.asset.controller.ApiHeaders.USER_ID_HEADER;

@RestController
@RequestMapping("/charts")
@Tag(name = "Charts", description = "Authenticated MongoDB Atlas Charts embed tokens")
public class ChartsEmbedController {

    private final ChartsEmbedTokenService chartsEmbedTokenService;

    public ChartsEmbedController(ChartsEmbedTokenService chartsEmbedTokenService) {
        this.chartsEmbedTokenService = chartsEmbedTokenService;
    }

    @GetMapping("/embed-token")
    @Operation(summary = "Mint a 60-minute Atlas Charts embed JWT for the logged-in user")
    public ChartsEmbedTokenResponse embedToken(
            @RequestHeader(value = USER_ID_HEADER, required = true) String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, USER_ID_HEADER + " header is required.");
        }

        return new ChartsEmbedTokenResponse(chartsEmbedTokenService.createToken(userId.trim()));
    }
}
