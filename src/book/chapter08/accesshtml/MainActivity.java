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

/*��WebView����HTML����*/
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
		//ֱ�Ӽ���URLҲ����ʵ�ָó�����
		//show.loadUrl("http://www.baidu.com/");

		myHandler = new Handler(){
			public void handleMessage(Message msg){
				if(msg.what==0x1122){
					result_show.setText(result);
					//ʹ��TextView��ʾ���
					show.loadDataWithBaseURL(null,result,"text/html","utf-8",null);
				}
			}
		};
		
		new Thread(){
			public void run(){
				try{
					URL httpUrl=new URL("http://www.baidu.com/");
					HttpURLConnection conn=(HttpURLConnection)httpUrl.openConnection();
					conn.setConnectTimeout(5*1000);//�������ӳ�ʱ
					conn.setRequestMethod("GET");//��get��ʽ��������GETһ��Ҫ��д
					if(conn.getResponseCode()!=200)
						throw new RuntimeException("����urlʧ��");
					InputStream iStream=conn.getInputStream();//�õ����緵�ص�������
					result=readData(iStream,"utf-8");
					conn.disconnect();
					myHandler.sendEmptyMessage(0x1122);
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}.start();
	}
		
		//��ȡ������Դ
		public static String readData(InputStream inStream,String charsetName)throws Exception{
				ByteArrayOutputStream outStream=new ByteArrayOutputStream();
				byte[] buffer=new byte[1024];
				int len=-1;
				while((len=inStream.read(buffer))!=-1){
					outStream.write(buffer,0,len);
				}
				byte []data=outStream.toByteArray();//���ֽ������תΪ�ֽ���
				outStream.close();//�ر��ֽ�������
				inStream.close();//�ر�������
				return new String(data,charsetName);//���ػ�ȡ�����ݣ���ҳԴ����
		}

}
