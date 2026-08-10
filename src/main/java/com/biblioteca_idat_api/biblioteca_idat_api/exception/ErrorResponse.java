package com.biblioteca_idat_api.biblioteca_idat_api.exception;

import java.time.LocalDateTime;

public record ErrorResponse(int status, String mensaje, LocalDateTime timestamp) {}