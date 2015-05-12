package com.zjk.wifiproject.socket.udp;


import com.zjk.wifiproject.entity.Message;

/**
 * IPMSG协议抽象类
 * <p>
 * 数据包编号：一般是取毫秒数。用来唯一地区别每个数据包；
 * <p>
 * SenderIMEI：指的是发送者的设备IMEI
 * <p>
 * 命令：指的是飞鸽协议中定义的一系列命令，具体见下文；
 * <p>
 * 附加数据：额外发送的数据
 * 
 * @see IPMSGConst
 * 
 */
public class IPMSGProtocol {

    public String packetNo;  // 数据包编号
    public String senderIP;  // 发送者ip
    public String targetIP;  // 目的ip
    public int commandNo;    // 命令
    public int addType;      // 附加数据类型
    public Message addObject; // 附加对象

}
