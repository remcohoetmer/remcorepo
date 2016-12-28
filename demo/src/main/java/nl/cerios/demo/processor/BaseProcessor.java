package nl.cerios.demo.common;

public abstract class BaseProcessor {
	protected LocationService_CF locationService_CF = new LocationService_CF();
	protected LocationService_Sync locationService_Sync = new LocationService_Sync();
	protected LocationService_Rx locationService_Rx = new LocationService_Rx();
	protected CustomerService customerService = new CustomerService();
	protected OrderService orderService= new OrderService();
	protected TransactionService transactionService= new TransactionService();
	protected PurchaseRequestController purchaseRequestController= PurchaseRequestController.getInstance();
	protected MailboxHandler mailboxHandler= new MailboxHandler();
	
	protected String composeLinkingFailedMessage(PurchaseRequest purchaseRequest) {
		return "Linking Transaction to Order failed" + purchaseRequest.toString();
	}

}
