package com.pdvapi.auth;

public record LoginResponse(String token, String userName, String email) {
}