package com.example.restservice;

import java.math.BigDecimal;
import java.util.Properties;

import io.github.vantiv.sdk.CnpOnline;
import io.github.vantiv.sdk.generate.Authentication;
import io.github.vantiv.sdk.generate.CardTokenType;
import io.github.vantiv.sdk.generate.CnpOnlineRequest;
import io.github.vantiv.sdk.generate.Contact;
import io.github.vantiv.sdk.generate.Credit;
import io.github.vantiv.sdk.generate.CreditResponse;
import io.github.vantiv.sdk.generate.OrderSourceType;
import io.github.vantiv.sdk.generate.RegisterTokenRequestType;
import io.github.vantiv.sdk.generate.RegisterTokenResponse;
import io.github.vantiv.sdk.generate.Sale;
import io.github.vantiv.sdk.generate.SaleResponse;
import io.github.vantiv.sdk.generate.VoidResponse;

public class TestToken {

	public static void main(String[] args) throws Exception {
	
		//TOken Request
		// TODO Auto-generated method stub
		RegisterTokenRequestType r = new RegisterTokenRequestType();
		String order="888996";
		r.setOrderId(order);
		r.setPaypageRegistrationId("3141642976632383100");
		r.setReportGroup("Default Report Group");
		r.setId(order);
		RegisterTokenResponse c = getCnpConnection().registerToken(r);
		System.out.println("sdf" + c.getCnpToken());

		
		//Sale Request
		Sale payment = new Sale();
		payment.setId(order);
		payment.setCnpTxnId(BigDecimal.valueOf(4).longValue());
		payment.setOrderId(order);
		payment.setReportGroup("Default Report Group");
		payment.setAmount(BigDecimal.valueOf(10).longValue());
		payment.setOrderSource(OrderSourceType.ANDROIDPAY); // The order source is a constant value

		payment.setToken(getCardInformation(c));
		
		Contact ct =new  Contact();
		ct.setZip("42701");
		payment.setBillToAddress(ct);
		
		CnpOnlineRequest onlineRequest =new CnpOnlineRequest();
		Authentication authentication = new Authentication();
		authentication.setUser("ELECTRAN");
		authentication.setPassword("K8yR46Hv");
		onlineRequest.setAuthentication(authentication);
		onlineRequest.setMerchantId("10102918");
		
		System.out.println("sale start");
		SaleResponse s=getCnpConnection().sale(payment,onlineRequest);
		System.out.println(s.getAuthCode());
		System.out.println("sale end ");
		
		System.out.println("Refund start");
		Credit refund=new Credit();
		refund.setCnpTxnId(new BigDecimal("84081102784698557").longValue());
		refund.setCustomerId(s.getCustomerId());
		refund.setOrderId("17751973");
		refund.setReportGroup(s.getReportGroup());
		refund.setAmount(new BigDecimal(10).longValue());
		refund.setId("326411");
CreditResponse creditRes=		getCnpConnection().credit(refund, onlineRequest);
System.out.println(creditRes.getResponse());
System.out.println(creditRes.getMessage());
System.out.println("Refund end");
		
		System.out.println("void start");
		io.github.vantiv.sdk.generate.Void v=new io.github.vantiv.sdk.generate.Void();
		v.setCnpTxnId(s.getCnpTxnId());
		v.setCustomerId(s.getCustomerId());
		v.setId(s.getId());
		v.setReportGroup(s.getReportGroup());
	VoidResponse vres=	getCnpConnection().dovoid(v, onlineRequest);
	System.out.println(vres.getResponse());
	System.out.println(vres.getMessage());
	System.out.println("void end");
		
	}
	

	private static CardTokenType getCardInformation(RegisterTokenResponse c) {
		CardTokenType ct=new CardTokenType();
		ct.setCnpToken(c.getCnpToken());
		ct.setExpDate("1225");
		return ct;
	}

	private static CnpOnline getCnpConnection() throws Exception {
		Properties props = getLitleProperties();
		CnpOnline online = new CnpOnline(props);
		return online;
	}

	private static final Properties getLitleProperties() throws Exception {
		Properties props = new Properties();
		props.setProperty(GooglePayConfigurationEnum.merchantId.name(), "10102918");
		props.setProperty(GooglePayConfigurationEnum.reportGroup.name(), "Default Report Group");
		props.setProperty(GooglePayConfigurationEnum.url.name(),
				"https://online.vantivprelive.com/vap/communicator/online");
		props.setProperty(GooglePayConfigurationEnum.username.name(), "ELECTRAN");
		props.setProperty(GooglePayConfigurationEnum.password.name(), "K8yR46Hv");
		props.setProperty(GooglePayConfigurationEnum.batchHost.name(), "batch.vantivprelive.com");
		props.setProperty(GooglePayConfigurationEnum.batchPort.name(), "22");
		props.setProperty(GooglePayConfigurationEnum.batchUseSSL.name(), "true");
		props.setProperty(GooglePayConfigurationEnum.batchTcpTimeout.name(), "60000");
		props.setProperty(GooglePayConfigurationEnum.maxTransactionsPerBatch.name(), "100000");
		props.setProperty(GooglePayConfigurationEnum.maxAllowedTransactionsPerFile.name(), "500000");
		/*
		 * props.setProperty(GooglePayConfigurationEnum.proxyHost.name(),
		 * "162.217.162.146");
		 * props.setProperty(GooglePayConfigurationEnum.proxyPort.toString(), "3128");
		 */
		props.setProperty(GooglePayConfigurationEnum.httpConnPoolSize.name(), "100");
		props.setProperty(GooglePayConfigurationEnum.httpkeepalive.name(), "true");
		props.setProperty(GooglePayConfigurationEnum.timeout.name(), "60000");
		props.setProperty(GooglePayConfigurationEnum.readTimeout.name(), "60000");
		props.setProperty(GooglePayConfigurationEnum.printxml.name(), "true");
		return props;
	}
}

enum GooglePayConfigurationEnum {

	// Begin Worldpay Connection configuration - Cnp SDK config
	merchantId("LITLE_CONFIG_MERCHANT_ID"), reportGroup("LITLE_CONFIG_REPORT_GROUP"), url("LITLE_CONFIG_URL"),
	username("LITLE_CONFIG_USERNAME"), password("LITLE_CONFIG_PASSWORD"),

	batchHost("LITLE_CONFIG_BATCH_HOST"), batchPort("LITLE_CONFIG_BATCH_PORT"),
	batchRequestFolder("LITLE_CONFIG_BATCH_REQUEST_FOLDER"), batchResponseFolder("LITLE_CONFIG_BATCH_RESPONSE_FOLDER"),
	batchUseSSL("LITLE_CONFIG_BATCH_USE_SSL"), batchTcpTimeout("LITLE_CONFIG_BATCH_TCP_TIMEOUT"),
	maxTransactionsPerBatch("LITLE_CONFIG_MAX_TRANSACTIONS_PER_BATCH"),
	maxAllowedTransactionsPerFile("LITLE_CONFIG_MAX_ALLOWED_TRANSACTIONS_PER_FILE"),

	proxyHost("LITLE_CONFIG_PROXY_HOST"), proxyPort("LITLE_CONFIG_PROXY_PORT"),

	sftpUsername("LITLE_CONFIG_SFTP_USERNAME"), sftpPassword("LITLE_CONFIG_SFTP_PASSWORD"),
	sftpTimeout("LITLE_CONFIG_SFTP_TIMEOUT"),

	httpConnPoolSize("LITLE_HTTP_CONN_POOL_SIZE"), httpkeepalive("LITLE_HTTP_KEEP_ALIVE"),
	timeout("LITLE_CONFIG_TIMEOUT"), readTimeout("LITLE_CONFIG_READ_TIMEOUT"),

	printxml("LITLE_CONFIG_PRINTXML"),
	// End Worldpay Connection configuration - Cnp SDK config

	implEnvironment("LITLE_CONFIG_IMPL_ENVIRONMENT"), // This specifies which env support is enabled (sandbox, prelive,
														// postlive and production)

	// Google Pay Sale/Refund Success
	paymentSuccess("000");

	private String code;

	private GooglePayConfigurationEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
