package ftnjps.paymentgateway;

import java.io.File;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfiguration {

	@Value("${trust.store}")
	private String trustStore;
	@Value("${trust.store.password}")
	private String trustStorePassword;

	@Bean
	public RestTemplate restTemplate() throws Exception {
		SSLContext sslContext =
				new SSLContextBuilder()
					.loadTrustMaterial(new File(trustStore), trustStorePassword.toCharArray()).build();
		SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
				sslContext,
				new NoopHostnameVerifier());

		HttpClient httpClient = HttpClients.custom()
				.setSSLSocketFactory(socketFactory)
				.build();
		HttpComponentsClientHttpRequestFactory requestFactory =
				new HttpComponentsClientHttpRequestFactory(httpClient);
		return new RestTemplate(requestFactory);
	}

}
