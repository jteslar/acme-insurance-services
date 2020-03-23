package com.acme.services.fees.validation

import com.acme.services.commons.exceptions.BadRequestException
import com.acme.services.commons.utils.toNumber
import com.acme.services.commons.utils.toPercentage
import com.acme.services.commons.utils.toUnlimitedNumber
import com.acme.services.fees.InsuranceFee
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class InsuranceFeeValidator {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(InsuranceFeeValidator::class.java)
    }

    fun validate(line: String): InsuranceFee {
        try {
            var feeProperties = line.split(";")
            return InsuranceFee(
                productCategory = feeProperties[0],
                limitLow = feeProperties[1].toNumber(),
                limitTop = feeProperties[2].toUnlimitedNumber(),
                feePercentage = feeProperties[3].toPercentage()
            )
        } catch (e: Exception) {
            logger.error("Error parsing fee configuration [$line]", e)
            throw BadRequestException("Error parsing fee configuration [$line]")
        }
    }
}