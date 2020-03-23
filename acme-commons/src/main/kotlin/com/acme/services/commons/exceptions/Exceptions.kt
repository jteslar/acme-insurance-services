package com.acme.services.commons.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus


@ResponseStatus(code = HttpStatus.BAD_REQUEST)
class BadRequestException(message: String?): Exception(message)

@ResponseStatus(code = HttpStatus.NOT_FOUND)
class NotFoundException(message: String?): Exception(message)

@ResponseStatus(code = HttpStatus.CONFLICT)
class ConflictException(message: String?): Exception(message)