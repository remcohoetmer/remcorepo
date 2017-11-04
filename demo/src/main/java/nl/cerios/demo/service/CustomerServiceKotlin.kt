package nl.cerios.demo.service

import java.util.logging.Logger


class CustomerServiceKotlin {

    @Throws(ValidationException::class)
    suspend fun retrieveCustomerData(customerId: Int?): CustomerData {
        return CustomerData(customerId)
    }

    fun validateCustomer(customerData: CustomerData, locationData: LocationConfig): CustomerValidation {
        LOG.info(Thread.currentThread().name)
        val validation = CustomerValidation()
        var status = Status.OK
        if (customerData.customerId !== locationData.locationId) {
            status = Status.NOT_OK
        }
        validation.status = status
        when (status) {
            Status.NOT_OK -> validation.setMessage("Customer validation failed")
            Status.OK -> validation.setMessage("Customer OK")
        }
        return validation
    }

    companion object {
        private val LOG = Logger.getLogger(CustomerService::class.java.name)
    }

}