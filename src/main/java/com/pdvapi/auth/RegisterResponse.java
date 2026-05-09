package com.pdvapi.auth;

public record RegisterResponse(String token, String userName, String email) {
}
