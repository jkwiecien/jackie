package net.techbrewery.jackie

/**
 * Created by Jacek Kwiecień on 30.10.2017.
 */

//val Context.ipAddress: String
//    get() {
//        var ip = ""
//        try {
//            val enumNetworkInterfaces: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
//            while (enumNetworkInterfaces.hasMoreElements()) {
//                val networkInterface = enumNetworkInterfaces.nextElement()
//                Enumeration<InetAddress> enumInetAddress = networkInterface . getInetAddresses ();
//                while (enumInetAddress.hasMoreElements()) {
//                    InetAddress inetAddress = enumInetAddress . nextElement ();
//
//                    if (inetAddress.isSiteLocalAddress()) {
//                        ip += "SiteLocalAddress: "
//                        +inetAddress.getHostAddress() + "\n";
//                    }
//
//                }
//
//            }
//
//        } catch (error: SocketException) {
//            // TODO Auto-generated catch block
//            error.printStackTrace();
//            ip += "Something Wrong! " + e.toString() + "\n";
//        }
//
//        return ip;
//    }
//}