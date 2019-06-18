package nl.cerios.demo.processor

import kotlinx.coroutines.runBlocking
import nl.cerios.demo.http.HttpRequestData
import nl.cerios.demo.service.ValidationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll

class PurchaseRequestProcessorTestKotlin : PurchaseRequestProcessorTestBaseKotlin() {
    @BeforeAll
    @Throws(Exception::class)
    fun setUp() {
        addPurchaseRequest(10, 10, 10)
    }

    // @Test
    suspend fun testHandle() {
        addPurchaseRequest(10, 10, 10)
        val requestData = HttpRequestData()
        requestData.purchaseRequestId = 10

        val purchaseResponse = PurchaseRequestProcessorKotlin().process(requestData)
        println(purchaseResponse)
        assertEquals(Integer.valueOf(90), purchaseResponse.purchaseRequest.orderId)

    }

    suspend fun testHandle2() {
        addPurchaseRequest(13, 13, 15)
        val requestData = HttpRequestData()
        requestData.purchaseRequestId = 13
        try {

            PurchaseRequestProcessorKotlin().process(requestData)

            throw Exception("test failed")
        } catch (cv: ValidationException) {
            assertEquals("Customer validation failed", cv.message)
        }

    }

}

fun main(args: Array<String>) = runBlocking<Unit>
{
    val test = PurchaseRequestProcessorTestKotlin()
    test.testHandle()
    test.testHandle2()


}