package com.JavaWebService.Demo.Utils.RestUtil;

import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.springframework.util.CollectionUtils;

import com.JavaWebService.Demo.BaseClass.BeanResultDto;
import com.alibaba.fastjson.JSON;

public class DIYJerseyClient<R, T> {
	private static final Logger logger = Logger.getLogger(DIYJerseyClient.class);
	public static final String GET = "GET";
    public static final String DELETE = "DELETE";
    public static final String PUT = "PUT";
    public static final String POST = "POST";
	private ClientConfig clientConfig;
	private Set<Class<?>> clientRegisters;
	private boolean isAsync = false;
	public void setClientRegisters(final Set<Class<?>> clientRegisters) {
		this.clientRegisters = clientRegisters;
	}
	/*
	 * @SuppressWarnings("deprecation") public void useApacheConnector(){
	 * PlainConnectionSocketFactory plainConnectionSocketFactory = ; final
	 * ClientConfig clientConfig = new ClientConfig(); // SchemeRegistry
	 * registry = new SchemeRegistry(); Map<String,
	 * PlainConnectionSocketFactory> map = new
	 * HashMap<String,PlainConnectionSocketFactory>();
	 * map.put("plainConnection", new PlainConnectionSocketFactory()); Registry
	 * registry = new Registry<PlainConnectionSocketFactory>(map);
	 * //schemePortResolver.resolve(); final ApacheConnectorProvider connector =
	 * new ApacheConnectorProvider(); clientConfig.connectorProvider(connector);
	 * clientConfig.property(ClientProperties.CONNECT_TIMEOUT, 1000);
	 * clientConfig.property(ClientProperties.READ_TIMEOUT, 2000);
	 * 
	 * client = ClientBuilder.newClient(clientConfig); }
	 */

	public  T rest(final String method, final String requestUrl, final Set<AtupRequestParam> headParams,
			final Set<AtupRequestParam> queryParams, final MediaType requestDataType, final R requestData,
			final Class<T> returnType) {
		if (clientConfig == null) {
			clientConfig = new ClientConfig();
		}
		final Client client = ClientBuilder.newClient(clientConfig);

		if (!CollectionUtils.isEmpty(clientRegisters)) {
			for (final Class<?> clazz : clientRegisters) {
				client.register(clazz);
			}
		}
		String urlStr = "http://localhost/Demo-Web/jersey" + requestUrl;
		WebTarget webTarget = client.target(urlStr);
		if (!CollectionUtils.isEmpty(queryParams)) {
			for (final AtupRequestParam atupRequestParam : queryParams) {
				webTarget = webTarget.queryParam(atupRequestParam.getKey(), atupRequestParam.getValue());
			}
		}
		final Invocation.Builder invocationBuilder = webTarget.request();
		if (!CollectionUtils.isEmpty(headParams)) {
			for (final AtupRequestParam atupRequestParam : headParams) {
				invocationBuilder.header(atupRequestParam.getKey(), atupRequestParam.getValue());
			}
		}
		Response response = null;
		Entity<R> entity;
		switch (method) {
		case GET:
			response = invocationBuilder.get();
			break;
		case DELETE:
			response = invocationBuilder.delete();
			break;
		case PUT:
			entity = Entity.entity(requestData, requestDataType);
			response = invocationBuilder.put(entity);
			break;
		case POST:
			if (isAsync) {
				final AsyncInvoker async = invocationBuilder.async();
				entity = Entity.entity(requestData, requestDataType);
				final Future<T> responseFuture = async.post(entity, returnType);
				try {
					return responseFuture.get(AtupVariable.ASYNC_LAUNCH_TEST_TIMEOUT + 2, TimeUnit.SECONDS);
				} catch (Exception e) {
					logger.error(e);
				}
				break;
			} else {
				entity = Entity.entity(requestData, requestDataType);
				response = invocationBuilder.post(entity);
				break;
			}
		default:
			response = invocationBuilder.get();
		}
		
		if (response != null) {
			return response.readEntity(returnType);
		} else {
			client.close();
			return null;
		}
	}
	
	public T rest(final String method,final String requestUrl,final Class<T> returnType){
		return rest(method, requestUrl, null, null, null, null, returnType);
	}
    public T rest(final String method, final String requestUrl, final Set<AtupRequestParam> headParams, final Set<AtupRequestParam> queryParams,
            final MediaType requestDataType, final Class<T> returnType) {
	  return rest(method, requestUrl, headParams, queryParams, requestDataType, null, returnType);
	}
    public T rest(final String method, final String requestUrl, final Set<AtupRequestParam> queryParams, final Class<T> returnType) {
	  return rest(method, requestUrl, null, queryParams, null, null, returnType);
	}
	public T rest(final String method, final String requestUrl, final MediaType requestDataType, final R requestData, final Class<T> returnType) {
	  return rest(method, requestUrl, null, null, requestDataType, requestData, returnType);
	}
	
	public BeanResultDto doGetBeanReult(final String requestUrl,final Set<AtupRequestParam> queryParams, final Class<T> returnType){
		BeanResultDto resultDto = (BeanResultDto)rest(DIYJerseyClient.GET, requestUrl, null, queryParams, null, null, (Class<T>) BeanResultDto.class);
		resultDto.setResult(JSON.parseObject(JSON.toJSONString(resultDto.getResult()), returnType));
		return resultDto;
	}
}
