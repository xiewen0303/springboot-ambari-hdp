package com.mybatis.demo.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CreateJavaFileUtil  extends ClassLoader{

	String _compiler;  
	String _classpath; 
	
	
	public CreateJavaFileUtil(String fileName,Map<String,String> messageMap) {
		super(ClassLoader.getSystemClassLoader()); 
		System.out.println("MessageCode.txt转JVAV文件");
		//默认编译器  
		if (_compiler == null){
			if(OSInfoUtil.isWindows()){
				_compiler = "C://Program Files//Java//jdk1.8.0_171//bin//javac";  
			}else{
				_compiler = "//usr//java//jdk1.7.0_80//bin//javac";  //linux编辑器
			}
			_classpath = "."; 
		} 
		String extraclasspath = null;
		if(OSInfoUtil.isWindows()){
			extraclasspath =  "C://Program Files//Java//jre8//lib//rt.jar";  
		}else{
			extraclasspath =  "//usr//java//jdk1.7.0_80//jre//lib//rt.jar";  
		}
		if (extraclasspath != null) {  
			_classpath =  _classpath + System.getProperty("path.separator") + extraclasspath;  
		}  
		compile(fileName,messageMap); 
	}
	
	public void compile(String fileName,Map<String,String> messageMap) {       
		String filename = "";  
		String classname = "";  
		try{  
			File javafile = null;
			if(OSInfoUtil.isWindows()){
				javafile =  new File(System.getProperty("user.dir")+"\\document\\com\\message\\MessageCode.java");  
			}else{
				javafile =  new File("/usr/local/three_king/messagebean/com/message/MessageCode.java");  
			}
			filename = javafile.getName();  
			classname = filename.substring(0, filename.lastIndexOf("."));  
			generateJavaFile(javafile, classname,messageMap); 
			
		}catch (IOException e){  
		   System.out.println(e.getMessage());
		   throw new RuntimeException(e.getMessage());  
		}  
	}  
	
	//生成java文件  
	public void generateJavaFile(File javafile, String classname,Map<String,String> messageMap) throws IOException {  
		FileOutputStream out = new FileOutputStream(javafile);  
		StringBuffer str = new StringBuffer( 
			"package com.message;\r\r"
			+ "import java.util.HashMap;\r"
			+ "import java.util.Map;\r"
			+ "import com.google.protobuf.GeneratedMessageV3;\r"
			+ "import com.junyou.log.GameLog;\r"
			+"public class "  
		    + classname  
		    + " { \r\r"
		    + "\tpublic static Map<String,Short> nameCodes = new HashMap<>();\r\r");
		StringBuffer infoStr = new StringBuffer();
		StringBuffer cmdStr = new StringBuffer();
		if(messageMap != null){
			
			List<Entry<String,String>> list = comparatorMap(messageMap);
			for (int i = 0; i < list.size(); i++) {//有序
				Entry<String,String> mm = list.get(i);
				String[] value = mm.getValue().split(":::");
				
				str.append("\t").append("public final static short ")
				.append(value[0]).append(" = ").append(mm.getKey())
				.append(";").append("\r");
				
				infoStr.append("\t\tnameCodes.put(\"com.message."+value[1]+"$")
				.append(value[0]+"\",").append(value[0]+");")
				.append("\r");
				
				cmdStr.append("\t\t\t\t").append("case ").append(value[0]+":\r")
				.append("\t\t\t\t\t").append("return data==null?com.message."+value[1]+".")
				.append(value[0]).append(".newBuilder().build():com.message."+value[1]+".")
				.append(value[0]).append(".parseFrom(data);").append("\r");
			
			}
		}
		str.append("\tstatic {\r");
		str.append(infoStr);
		str.append("\t}\r\r");
		
		str.append("\tpublic static short getCode(String codeName) {\r");
		str.append("\t\treturn nameCodes.get(codeName);\r");
		str.append("\t}\r");
		
		str.append("\r");
		
		str.append("\tpublic static GeneratedMessageV3 parseMessage(short cmd, byte[] data) {\r");
		str.append("\t\ttry {\r");
		str.append("\t\t\tswitch (cmd) {\r");
		str.append(cmdStr);
		str.append("\t\t\t\t").append("default:\r");
		str.append("\t\t\t\t\t").append("GameLog.error(\"message cmd is not exist,cmd:\"+cmd);\r");
		str.append("\t\t\t\t\t").append("break;\r");
		str.append("\t\t\t}\r");
		str.append("\t\t").append("} catch (Exception e) {\r");
		str.append("\t\t\t").append("e.printStackTrace();\r");
		str.append("\t\t").append("}\r");
		str.append("\t\t").append("return null;\r");
		str.append("\t}\r");
		
		str.append("}");
		out.write(str.toString().getBytes());  
		out.close();  
		System.out.println("MessageCode.txt转JVAV文件");
	}  
	
	
	 private List<Entry<String,String>>  comparatorMap( Map<String,String> map){
	    	List<Entry<String,String>> list = new ArrayList<Entry<String,String>>(map.entrySet());
	        Collections.sort(list,new Comparator<Entry<String,String>>() {
	            //升序排序
	            public int compare(Entry<String, String> o1,
	                    Entry<String, String> o2) {
	                int o1V = Integer.parseInt(o1.getKey());
	                int o2V = Integer.parseInt(o2.getKey());
	            	
	                if(o1V > o2V){
	                	return 1;
	                }else if(o1V < o2V){
	                	return -1;
	                }
	                return 0;
	            }
	        });
	        return list;
	    }
}
