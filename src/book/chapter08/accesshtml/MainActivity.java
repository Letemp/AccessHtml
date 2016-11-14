package book.chapter08.accesshtml;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.webkit.WebView;
import android.widget.TextView;

/*用WebView解析HTML代码*/
public class MainActivity extends Activity {

	private WebView show;
	private TextView result_show;
	private Handler myHandler;
	private String result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		show=(WebView)this.findViewById(R.id.show);
		result_show=(TextView)this.findViewById(R.id.result);
		//直接加载URL也可以实现该程序功能
		//show.loadUrl("http://www.baidu.com/");

		myHandler = new Handler(){
			public void handleMessage(Message msg){
				if(msg.what==0x1122){
					result_show.setText(result);
					//使用TextView显示结果
					show.loadDataWithBaseURL(null,result,"text/html","utf-8",null);
				}
			}
		};
		
		new Thread(){
			public void run(){
				try{
					URL httpUrl=new URL("http://www.baidu.com/");
					HttpURLConnection conn=(HttpURLConnection)httpUrl.openConnection();
					conn.setConnectTimeout(5*1000);//设置连接超时
					conn.setRequestMethod("GET");//以get方式发起请求，GET一定要大写
					if(conn.getResponseCode()!=200)
						throw new RuntimeException("请求url失败");
					InputStream iStream=conn.getInputStream();//得到网络返回的输入流
					result=readData(iStream,"utf-8");
					conn.disconnect();
					myHandler.sendEmptyMessage(0x1122);
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}.start();
	}
		
		//获取网络资源
		public static String readData(InputStream inStream,String charsetName)throws Exception{
				ByteArrayOutputStream outStream=new ByteArrayOutputStream();
				byte[] buffer=new byte[1024];
				int len=-1;
				while((len=inStream.read(buffer))!=-1){
					outStream.write(buffer,0,len);
				}
				byte []data=outStream.toByteArray();//将字节输出流转为字节组
				outStream.close();//关闭字节流输入
				inStream.close();//关闭输入流
				return new String(data,charsetName);//返回获取的内容，网页源代码
		}

}
