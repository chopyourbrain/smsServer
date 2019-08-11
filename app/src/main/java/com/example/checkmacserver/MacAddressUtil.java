package com.example.checkmacserver;

import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;
import jpcap.packet.ARPPacket;
import jpcap.packet.EthernetPacket;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Arrays;

public class MacAddressUtil {

    public static Boolean checkMACAddress(String message) throws IOException, IllegalArgumentException {
        boolean f = false;
        jpcap.NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        NetworkInterface networkDevice = devices[0];
        JpcapCaptor captor = JpcapCaptor.openDevice(networkDevice, 2000, false, 3000);
        captor.setFilter("arp", true);
        JpcapSender sender = captor.getJpcapSenderInstance();
        byte[] mac;
        InetAddress srcip;
        for (NetworkInterfaceAddress addr : networkDevice.addresses) {
            if (addr.address instanceof Inet4Address) {
                srcip = addr.address;
                byte[] broadcast = new byte[]{(byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255};
                ARPPacket arp = new ARPPacket();
                arp.hardtype = ARPPacket.HARDTYPE_ETHER;
                arp.prototype = ARPPacket.PROTOTYPE_IP;
                arp.operation = ARPPacket.ARP_REQUEST;
                arp.hlen = 6;
                arp.plen = 4;
                arp.sender_hardaddr = networkDevice.mac_address;
                arp.sender_protoaddr = srcip.getAddress();
                arp.target_protoaddr = srcip.getAddress();
                arp.target_hardaddr = broadcast;
                EthernetPacket ether = new EthernetPacket();
                ether.frametype = EthernetPacket.ETHERTYPE_ARP;
                ether.src_mac = networkDevice.mac_address;
                ether.dst_mac = broadcast;
                arp.datalink = ether;
                sender.sendPacket(arp);
                while (true) {
                    ARPPacket p = (ARPPacket) captor.getPacket();
                    if (p == null) {
                        throw new IllegalArgumentException(srcip + " is not a local address");
                    }
                    if (Arrays.equals(p.target_protoaddr, srcip.getAddress())) {
                        mac = p.sender_hardaddr;
                        break;
                    }
                }
                if (mac != null) {
                    StringBuilder formattedMac = new StringBuilder();
                    boolean first = true;
                    for (byte b : mac) {
                        if (first) {
                            first = false;
                        } else {
                            formattedMac.append(":");
                        }
                        String hexStr = Integer.toHexString(b & 0xff);
                        if (hexStr.length() == 1) {
                            formattedMac.append("0");
                        }
                        formattedMac.append(hexStr);
                    }
                    if (formattedMac.toString().equals(message))
                        f = true;
                }
            }
        }
        return f;
    }
}
