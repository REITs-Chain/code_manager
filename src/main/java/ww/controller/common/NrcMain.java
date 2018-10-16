package ww.controller.common;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import ru.paradoxs.bitcoin.client.ValidatedAddressInfo;
import ru.paradoxs.bitcoin.client.exceptions.BitcoinClientException;

import ww.bitcoin.WwHttpSession;


public class NrcMain {

	private WwHttpSession session = null;
	private String error="";
	
	private static String  host="192.168.1.168"; //换成你们的NRC节点IP
	private static String  userName="wuqi0113";//换成你们用户名
	private static String  password="diKjwPK51IFEpXB-nplEYngf0QNAMjPuVkoY4Xr6-5o=";//换成你们密码
	private static int  port=18211;//换成你们端口
	
	
	public  HashMap<String, Object> rpcTest(){
		System.err.println("begin call RPC:");
		NrcMain nm=new NrcMain();
		HashMap<String, Object> mm=new HashMap<String, Object>();
		nm.initRPC(host,userName,password,port);
		
		
		/**
		 * success
		 */
		//getInfo
		//JSONObject res=nm.getInfo();	
		//JSONObject res=nm.getBlockChainInfo();
		//JSONObject res = nm.getMempoolInfo();
		//System.err.println("response:"+res.toString());
		BigDecimal balance = nm.getBalance();
		
		
		
		/**
		 * error
		 */
		//JSONObject res=nm.getBlockCount();
		//JSONObject res2=nm.getChainTips();
		//System.err.println("response:"+res2.toString());
		mm.put("address2", balance.toString());
		//mm.put("isValid", isValid);
		return mm;
	}
	
	
	public ValidatedAddressInfo validateAddress(String address) {
		try {
			  JSONArray parameters = new JSONArray().element(address).element(1);
		      JSONObject request = createRequest("validateaddress", parameters);
		      JSONObject response = this.session.sendAndReceive(request);
		      JSONObject result = (JSONObject)response.get("result");
		      
		      ValidatedAddressInfo info = new ValidatedAddressInfo();
		      info.setIsValid(result.getBoolean("isvalid"));
		      if (info.getIsValid())
		      {
		        info.setIsMine(result.getBoolean("ismine"));
		        info.setAddress(result.getString("address"));
		      }
		      return info;
		} catch (Exception e) {
			throw new BitcoinClientException("Exception when getting the server info", e);
		}
	}
	
	
	  private static BigDecimal getBigDecimal(JSONObject jsonObject, String key)
			    throws Exception
			  {
			    String string = jsonObject.getString(key);
			    BigDecimal bigDecimal = new BigDecimal(string);
			    return bigDecimal;
			  }
	 public BigDecimal getBalance()
	  {
	    try
	    {
	      JSONObject request = createRequest("getbalance");
	      JSONObject response = this.session.sendAndReceive(request);
	      
	      return getBigDecimal(response, "result");
	    }
	    catch (Exception e)
	    {
	      throw new BitcoinClientException("Exception when getting balance", e);
	    }
	  }
	
	public JSONObject getMempoolInfo() {
		try {
			JSONObject request = createRequest("getmempoolinfo");
			JSONObject response = this.session.sendAndReceive(request);
			JSONObject result = (JSONObject) response.get("result");

			return result;
		} catch (Exception e) {
			throw new BitcoinClientException("Exception when getting the server info", e);
		}
	}
	
	
	public JSONObject getChainTips() {
		try {
			JSONObject request = createRequest("getchaintips");
			JSONObject response = this.session.sendAndReceive(request);
			JSONObject result = (JSONObject) response.get("result");

			return result;
		} catch (Exception e) {
			throw new BitcoinClientException("Exception when getting the server info", e);
		}
	}
	
	
	public JSONObject getBlockCount() {
		try {
			JSONObject request = createRequest("getblockcount");
			JSONObject response = this.session.sendAndReceive(request);
			JSONObject result = (JSONObject) response.get("result");

			return result;
		} catch (Exception e) {
			throw new BitcoinClientException("Exception when getting the server info", e);
		}
	}
	
	public JSONObject getBlockChainInfo() {
		try {
			JSONObject request = createRequest("getblockchaininfo");
			JSONObject response = this.session.sendAndReceive(request);
			JSONObject result = (JSONObject) response.get("result");

			return result;
		} catch (Exception e) {
			throw new BitcoinClientException("Exception when getting the server info", e);
		}
	}
	
	public JSONObject getInfo() {
		try {
			JSONObject request = createRequest("getinfo");
			JSONObject response = this.session.sendAndReceive(request);
			JSONObject result = (JSONObject) response.get("result");

			return result;
		} catch (Exception e) {
			throw new BitcoinClientException("Exception when getting the server info", e);
		}
	}
	
	public JSONObject getPrimeAddr() {
		try {
			JSONObject request = createRequest("getprimeaddr");
			JSONObject response = this.session.sendAndReceive(request);
			JSONObject result = (JSONObject) response.get("result");

			return result;
		} catch (Exception e) {
			throw new BitcoinClientException("Exception when getting the primeaddr", e);
		}
	}
	
	/*public String getRawtransaction(String txId) {
		try {
			JSONArray parameters = new JSONArray().element(txId).element(1);
			JSONObject request = createRequest("getrawtransaction", parameters);
			JSONObject response = this.session.sendAndReceive(request);
			if (response == null) {
				error = this.session.getError();
				return null;
			}

			return response.getString("result");
		} catch (Exception e) {
			error = "(getrawtransaction)Exception when getting the transaction ";
			return null;
		}
	}*/


	public static void main(String[] args) {
		System.out.println("begin call RPC:");
		
		NrcMain nm=new NrcMain();
		
		nm.initRPC(host,userName,password,port);		
		JSONObject res=nm.getInfo();			
		System.out.println("response:"+res.toString());
		JSONObject res1=nm.getPrimeAddr();			
		System.out.println("response:"+res1.toString());
		

	}

	public void initRPC(String _host, String _userName, String _password, int _port) {
		try {
			Credentials credentials = new UsernamePasswordCredentials(_userName, _password);
			URI uri = new URI("http", null, _host, _port, null, null, null);
			this.session = new WwHttpSession(uri, credentials);

		} catch (URISyntaxException e) {
			throw new BitcoinClientException("This host probably doesn't have correct syntax: " + host, e);
		}
	}
	
	private JSONObject createRequest(String functionName, JSONArray parameters) throws Exception {
		JSONObject request = new JSONObject();
		request.put("jsonrpc", "2.0");
		request.put("id", UUID.randomUUID().toString());
		request.put("method", functionName);
		request.put("params", parameters);

		return request;
	}

	private JSONObject createRequest(String functionName) throws Exception {
		return createRequest(functionName, new JSONArray());
	}

}
