package nl.cerios.demo.processor

import nl.cerios.demo.http.HttpRequestData
import nl.cerios.demo.service.*

open class PurchaseRequestProcessorKotlin {
    private fun composeLinkingFailedMessage(purchaseResponse: PurchaseResponse): String {
        return "Linking Transaction to Order failed: " + purchaseResponse.toString()
    }

    @Throws(ValidationException::class)
    suspend fun process(requestData: HttpRequestData): PurchaseResponse {
        val purchaseRequest = PurchaseRequestController.getInstance().retrievePurchaseRequest_Sync(requestData.purchaseRequestId)
        val customerData = CustomerServiceKotlin().retrieveCustomerData(purchaseRequest.customerId)
        val locationData = LocationService_Sync().getLocationConfig(purchaseRequest.locationId)

        val customerValidation = CustomerServiceKotlin().validateCustomer(customerData, locationData)
        if (customerValidation.status != Status.OK) {
            throw ValidationException(customerValidation.message)
        }
        val transactionValidation = TransactionService().validate_Sync(purchaseRequest, customerData)
        if (transactionValidation.status != Status.OK) {
            throw ValidationException(transactionValidation.message)
        }

        val orderData = OrderService().executeOrder_Sync(purchaseRequest)
        val purchaseResponse = PurchaseRequestController.getInstance().update_Sync(purchaseRequest, orderData)

        val status = TransactionService().linkOrderToTransaction_Sync(purchaseRequest)
        if (status != Status.OK) {
            MailboxHandler().sendMessage_Sync(composeLinkingFailedMessage(purchaseResponse))
        }
        return purchaseResponse
    }
}


