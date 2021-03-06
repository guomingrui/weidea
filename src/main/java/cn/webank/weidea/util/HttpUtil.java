package cn.webank.weidea.util;

import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;

import cn.webank.weidea.dao.exception.CheckException;

public class HttpUtil {

	/**
	 * 发送post请求，参数用map接收
	 * 
	 * @param url
	 *            地址
	 * @param map
	 *            参数
	 * @return 返回值
	 */
	public static String postMap(String url, String json) {
		String result = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost post = new HttpPost(url);
		CloseableHttpResponse response = null;
		try {
			post.setEntity(new ByteArrayEntity(json.getBytes("UTF-8")));
			response = httpClient.execute(post);
			if (response != null && response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				result = entityToString(entity);
			}
			return result;
		} catch (Exception e) {
			throw new CheckException("http post失败", e);
		} finally {
			try {
				httpClient.close();
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	private static String entityToString(HttpEntity entity) throws IOException {
		String result = null;
		if (entity != null) {
			long lenth = entity.getContentLength();
			if (lenth != -1 && lenth < 2048) {
				result = EntityUtils.toString(entity, "UTF-8");
			} else {
				InputStreamReader reader1 = new InputStreamReader(entity.getContent(), "UTF-8");
				CharArrayBuffer buffer = new CharArrayBuffer(2048);
				char[] tmp = new char[4096];
				int l;
				while ((l = reader1.read(tmp)) != -1) {
					buffer.append(tmp, 0, l);
				}
				result = buffer.toString();
			}
		}
		return result;
	}
}