/**
 * 
 */
package com.hujuan;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author hujuan
 *
 */
public class Testting_1 {

    /**
     * @param args
     * @throws UnknownHostException
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Testting_1 test = new Testting_1();
//        test.getLocalAddress();
//        test.getNetWork();
//        System.out.print("\nenp2s0:\n");
//        test.getIPs("enp2s0");
//        System.out.print("\nlo:\n");
//        test.getIPs("lo");
//        System.out.println("\nIPS");
        test.getLocalIPList();
    }

    /**
     * 
     */
    public void getLocalAddress() {
        // TODO Auto-generated constructor stub
        try {
            InetAddress me = InetAddress.getLocalHost();
            String dottedQuad = me.getHostAddress();
            System.out.println("My Address is " + dottedQuad);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
     /*
      * 
      */
    public void getLocalIPList() {
        // TODO Auto-generated constructor stub
        List<String> ipList = new ArrayList<String>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            NetworkInterface networkInterface;
            Enumeration<InetAddress> inetAddresses;
            InetAddress inetAddress;
            String ip;
            while (networkInterfaces.hasMoreElements()) {
                networkInterface = networkInterfaces.nextElement();
                System.out.println(networkInterface);
                inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    inetAddress = inetAddresses.nextElement();
                    if (inetAddress != null && inetAddress instanceof Inet4Address) { // IPV4
                        ip = inetAddress.getHostAddress();
                        ipList.add(ip);
                        System.out.println(ip);
                    }
                }
                System.out.println();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void getNetWork() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                System.out.println(ni);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getIPs(String netName) {
        try {
            NetworkInterface eth0 = NetworkInterface.getByName(netName);
            Enumeration adddressEnumeration = eth0.getInetAddresses();
            while (adddressEnumeration.hasMoreElements()) {
                System.out.println(adddressEnumeration.nextElement());

            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

}
