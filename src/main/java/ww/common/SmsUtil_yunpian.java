package ww.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;




import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
import com.yunpian.sdk.YunpianClient;
import com.yunpian.sdk.model.Result;
import com.yunpian.sdk.model.SmsSingleSend;

public class SmsUtil_yunpian {
	
	public static String message="";//错误信息
	
	public static String apikey="e50da878f115a22f9031df0b0ffe9214";//AppKey
	
	//单发送  普通
	public static boolean singleSend_NRC(String mobile,String verifyNum){
		//String verifyNum=getNewVerifyNum();
		String msg="【NRC】您的验证码是"+verifyNum;
		String URL="https://sms.yunpian.com/v2/sms/single_send.json";
		String extend="643505";
		return send(mobile, URL, msg, extend);
	}
	
	/*public static boolean send(String mobile,String URL,String msg,String extend) {
			//初始化clnt,使用单例方式
			YunpianClient clnt = new YunpianClient(apikey).init();
			//发送短信API
			Map<String, String> param = clnt.newParam(4);
			param.put("mobile", mobile);
			param.put(YunpianClient.LONG_URL, "https://sms.yunpian.com/v2/sms/single_send.json");
			  param.put("text", "【NRC】您的验证码是1234");
			  param.put("extend", "643505");
			    国内号码：15205201314
		            国际号码：urlencode("+93701234567");
			param.put(YunpianClient.LONG_URL, URL);
			param.put("text", msg);
			param.put("extend", extend);
			Result<SmsSingleSend> r = clnt.sms().single_send(param);
			//获取返回结果，返回码:r.getCode(),返回码描述:r.getMsg(),API结果:r.getData(),其他说明:r.getDetail(),调用异常:r.getThrowable()
			//账户:clnt.user().* 签名:clnt.sign().* 模版:clnt.tpl().* 短信:clnt.sms().* 语音:clnt.voice().* 流量:clnt.flow().* 隐私通话:clnt.call().*
			//释放clnt
			clnt.close();
			if (r.getCode()==0) {
				return true;
			}
			return false;
	}*/
	
	public static boolean send(String mobile,String URL,String text,String extend) {
		try {
			//初始化clnt,使用单例方式
			YunpianClient clnt = new YunpianClient(apikey).init();
			//发送短信API
			Map<String, String> param = clnt.newParam(4);
			param.put("mobile", mobile);
				/*param.put(YunpianClient.LONG_URL, "https://sms.yunpian.com/v2/sms/single_send.json");
			    param.put("text", "【NRC】您的验证码是1234");
			    param.put("extend", "643505");
			    国内号码：15205201314
		               国际号码：urlencode("+93701234567");*/
			param.put(YunpianClient.LONG_URL, URL);
			param.put("text", text);
			param.put("extend", extend);
			Result<SmsSingleSend> r = clnt.sms().single_send(param);
			//获取返回结果，返回码:r.getCode(),返回码描述:r.getMsg(),API结果:r.getData(),其他说明:r.getDetail(),调用异常:r.getThrowable()
			//账户:clnt.user().* 签名:clnt.sign().* 模版:clnt.tpl().* 短信:clnt.sms().* 语音:clnt.voice().* 流量:clnt.flow().* 隐私通话:clnt.call().*
			//释放clnt
			clnt.close();
			if (r.getCode()==0) {
				return true;
			}else if (r.getCode()==8) {
				message="同一手机号30秒内重复提交相同的内容";
				return false;
			}else if (r.getCode()==9) {
				message="同一手机号5分钟内重复提交相同的内容超过3次";
				return false;
			}else if (r.getCode()==17) {
				message="24小时内同一手机号发送次数超过限制";
				return false;
			}else if (r.getCode()==43) {
				message="一天内同一手机号发送次数超过限制";
				return false;
			}else {
				message="其他错误";
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block			
			e.printStackTrace();
			Log log=LogFactory.getLog(SmsUtil_yunpian.class);
			log.equals("发短信异常："+e.getMessage());
			
			return false;
		}
	}
	
	public static String getNewVerifyNum(){
	  int num=6;//验证码长度
	  Random random=new Random();
	  //初始化种子
	  String[] str={"0","1","2","3","4","5","6","7","8","9"};
				   //,"a","b","c","d","e","f","g","h","i","j",
				   //"k","l","m","n","p","q","r","s","t"};
	  int number=str.length;
	  //接收随机字符
	  String text = "";
	  //随机产生4个字符的字符串
	  for(int i=0;i<num;i++){		  
	   text+=str[random.nextInt(number)];
	  }
	  return text;
	}

}
