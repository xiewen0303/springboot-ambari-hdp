package com.mybatis.demo.utils;

public class OSInfoUtil {
	 	private static String OS = System.getProperty("os.name").toLowerCase();  
	 	
	 	public static String[] getExecCmd(String command){
	 		if(isWindows()){
	 			return new String[]{ "cmd", "/c",command};
	 		}else if(isLinux()){
	 			return new String[]{"/bin/sh", "-c", command};
	 		}
	 		return null;
	 	}
	 	
	    private static EPlatform platform;  
	  
	    public static boolean isLinux(){  
	        return OS.indexOf("linux")>=0;  
	    }  
	      
	    public static boolean isMacOS(){  
	        return OS.indexOf("mac")>=0&&OS.indexOf("os")>0&&OS.indexOf("x")<0;  
	    }  
	      
	    public static boolean isMacOSX(){  
	        return OS.indexOf("mac")>=0&&OS.indexOf("os")>0&&OS.indexOf("x")>0;  
	    }  
	      
	    public static boolean isWindows(){  
	        return OS.indexOf("windows")>=0;  
	    }  
	      
	    public static boolean isOS2(){  
	        return OS.indexOf("os/2")>=0;  
	    }  
	      
	    public static boolean isSolaris(){  
	        return OS.indexOf("solaris")>=0;  
	    }  
	      
	    public static boolean isSunOS(){  
	        return OS.indexOf("sunos")>=0;  
	    }  
	      
	    public static boolean isMPEiX(){  
	        return OS.indexOf("mpe/ix")>=0;  
	    }  
	      
	    public static boolean isHPUX(){  
	        return OS.indexOf("hp-ux")>=0;  
	    }  
	      
	    public static boolean isAix(){  
	        return OS.indexOf("aix")>=0;  
	    }  
	      
	    public static boolean isOS390(){  
	        return OS.indexOf("os/390")>=0;  
	    }  
	      
	    public static boolean isFreeBSD(){  
	        return OS.indexOf("freebsd")>=0;  
	    }  
	      
	    public static boolean isIrix(){  
	        return OS.indexOf("irix")>=0;  
	    }  
	      
	    public static boolean isDigitalUnix(){  
	        return OS.indexOf("digital")>=0&&OS.indexOf("unix")>0;  
	    }  
	      
	    public static boolean isNetWare(){  
	        return OS.indexOf("netware")>=0;  
	    }  
	      
	    public static boolean isOSF1(){  
	        return OS.indexOf("osf1")>=0;  
	    }  
	      
	    public static boolean isOpenVMS(){  
	        return OS.indexOf("openvms")>=0;  
	    }  
	      
	    /** 
	     * 获取操作系统名字 
	     * @return 操作系统名 
	     */  
	    public static EPlatform getOSname(){  
	        if(isAix()){  
	            platform = EPlatform.AIX;  
	        }else if (isDigitalUnix()) {  
	            platform = EPlatform.Digital_Unix;  
	        }else if (isFreeBSD()) {  
	            platform = EPlatform.FreeBSD;  
	        }else if (isHPUX()) {  
	            platform = EPlatform.HP_UX;  
	        }else if (isIrix()) {  
	            platform = EPlatform.Irix;  
	        }else if (isLinux()) {  
	            platform = EPlatform.Linux;  
	        }else if (isMacOS()) {  
	            platform = EPlatform.Mac_OS;  
	        }else if (isMacOSX()) {  
	            platform = EPlatform.Mac_OS_X;  
	        }else if (isMPEiX()) {  
	            platform = EPlatform.MPEiX;  
	        }else if (isNetWare()) {  
	            platform = EPlatform.NetWare_411;  
	        }else if (isOpenVMS()) {  
	            platform = EPlatform.OpenVMS;  
	        }else if (isOS2()) {  
	            platform = EPlatform.OS2;  
	        }else if (isOS390()) {  
	            platform = EPlatform.OS390;  
	        }else if (isOSF1()) {  
	            platform = EPlatform.OSF1;  
	        }else if (isSolaris()) {  
	            platform = EPlatform.Solaris;  
	        }else if (isSunOS()) {  
	            platform = EPlatform.SunOS;  
	        }else if (isWindows()) {  
	            platform = EPlatform.Windows;  
	        }else{  
	            platform = EPlatform.Others;  
	        }  
	        return platform;  
	    }  
}

enum EPlatform {  
    Any("any"),  
    Linux("Linux"),  
    Mac_OS("Mac OS"),  
    Mac_OS_X("Mac OS X"),  
    Windows("Windows"),  
    OS2("OS/2"),  
    Solaris("Solaris"),  
    SunOS("SunOS"),  
    MPEiX("MPE/iX"),  
    HP_UX("HP-UX"),  
    AIX("AIX"),  
    OS390("OS/390"),  
    FreeBSD("FreeBSD"),  
    Irix("Irix"),  
    Digital_Unix("Digital Unix"),  
    NetWare_411("NetWare"),  
    OSF1("OSF1"),  
    OpenVMS("OpenVMS"),  
    Others("Others");  
      
    private EPlatform(String desc){  
        this.description = desc;  
    }  
      
    public String toString(){  
        return description;  
    }  
      
    private String description;  
}  
