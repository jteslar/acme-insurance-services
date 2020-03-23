package com.acme.services.contracts.validation

import com.acme.services.commons.exceptions.BadRequestException
import com.acme.services.commons.utils.toDate
import com.acme.services.commons.utils.toNumber
import com.acme.services.contracts.InsuranceContract
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class InsuranceContractValidator {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(InsuranceContractValidator::class.java)
    }

    fun validate(line: String): InsuranceContract {
        try {
            var contractProperties = line.split(";")
            return InsuranceContract(
                deliveryDate = contractProperties[0].toDate(),
                productName = contractProperties[1],
                declaredValue = contractProperties[2].toNumber()
            )
        } catch (e: Exception) {
            logger.error("Error parsing insurance request [$line]", e)
            throw BadRequestException("Error parsing insurance request [$line]")
        }
    }
}