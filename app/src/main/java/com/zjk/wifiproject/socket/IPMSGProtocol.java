package com.zjk.wifiproject.socket;

/**
 * IPMSG协议
 * <p/>
 * 数据包变化：一般是取毫秒数，唯一区别数据包
 * <br/>
 * SenderIMEI: 发送者的设备IMEI
 * <br/>
 *
 * Created by Administrator on 2015/4/28.
 */

import com.zjk.wifiproject.entity.WFile;

import java.util.Date;


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
    private static final String TAG = "SZU_IPMSGPProtocol";
    private static final String PACKETNO = "packetNo";
    private static final String COMMANDNO = "commandNo";
    private static final String ADDOBJECT = "addObject";
    private static final String ADDSTR = "addStr";
    private static final String ADDTYPE = "addType";

    private String packetNo;// 数据包编号
    private String senderIMEI; // 发送者IMEI
    private int commandNo; // 命令
    private ADDITION_TYPE addType; // 附加数据类型
    private WFile addObject; // 附加对象
    private String addStr; // 附加信息

    public IPMSGProtocol() {
        this.packetNo = getSeconds();
    }

    public String getProtocolJSON() {
        String result = "我是发送数据";
        return result;
    }

    public enum ADDITION_TYPE {
        USER, MSG, STRING
    }

    // 根据协议字符串初始化
    public IPMSGProtocol(String paramProtocolJSON) {
//        try {
//            JSONObject protocolJSON = new JSONObject(paramProtocolJSON);
//            packetNo = protocolJSON.getString(PACKETNO);
//            commandNo = protocolJSON.getInt(COMMANDNO);
//            senderIMEI = protocolJSON.getString(Users.IMEI);
//            if (protocolJSON.has(ADDTYPE)) { // 若有附加信息
//                String addJSONStr = null;
//                if (protocolJSON.has(ADDOBJECT)) { // 若为Entity类型
//                    addJSONStr = protocolJSON.getString(ADDOBJECT);
//                }
//                else if (protocolJSON.has(ADDSTR)) { // 若为String类型
//                    addJSONStr = protocolJSON.getString(ADDSTR);
//                }
//                switch (ADDITION_TYPE.valueOf(protocolJSON.getString(ADDTYPE))) {
//                    case USER: // 为用户数据
//                        addObject = JsonUtils.getObject(addJSONStr, Users.class);
//                        break;
//
//                    case MSG: // 为消息数据
//                        addObject = JsonUtils.getObject(addJSONStr, Message.class);
//                        break;
//
//                    case STRING: // 为String数据
//                        addStr = addJSONStr;
//                        break;
//
//                    default:
//                        break;
//                }
//
//            }
//        }
//        catch (JSONException e) {
//            e.printStackTrace();
//            LogUtils.e(TAG, "非标准JSON文本");
//        }
    }

    public IPMSGProtocol(String paramSenderIMEI, int paramCommandNo, WFile paramObject) {
        super();
        this.packetNo = getSeconds();
        this.senderIMEI = paramSenderIMEI;
        this.commandNo = paramCommandNo;
        this.addObject = paramObject;
//        if (paramObject instanceof Message) { // 若为Message对象
//            this.addType = ADDITION_TYPE.MSG;
//        }
//        else if (paramObject instanceof Users) { // 若为NearByPeople对象
//            this.addType = ADDITION_TYPE.USER;
//        }
    }

    public IPMSGProtocol(String paramSenderIMEI, int paramCommandNo, String paramStr) {
        super();
        this.packetNo = getSeconds();
        this.senderIMEI = paramSenderIMEI;
        this.commandNo = paramCommandNo;
        this.addStr = paramStr;
        this.addType = ADDITION_TYPE.STRING;
    }

    public IPMSGProtocol(String paramSenderIMEI, int paramCommandNo) {
        super();
        this.packetNo = getSeconds();
        this.senderIMEI = paramSenderIMEI;
        this.commandNo = paramCommandNo;
    }


    private String getSeconds() {
        Date nowDate = new Date();
        return Long.toString(nowDate.getTime());
    }

    public String getPacketNo() {
        return packetNo;
    }

    public void setPacketNo(String packetNo) {
        this.packetNo = packetNo;
    }

    public String getSenderIMEI() {
        return senderIMEI;
    }

    public void setSenderIMEI(String senderIMEI) {
        this.senderIMEI = senderIMEI;
    }

    public int getCommandNo() {
        return commandNo;
    }

    public void setCommandNo(int commandNo) {
        this.commandNo = commandNo;
    }

    public ADDITION_TYPE getAddType() {
        return addType;
    }

    public void setAddType(ADDITION_TYPE addType) {
        this.addType = addType;
    }

    public WFile getAddObject() {
        return addObject;
    }

    public void setAddObject(WFile addObject) {
        this.addObject = addObject;
    }

    public String getAddStr() {
        return addStr;
    }

    public void setAddStr(String addStr) {
        this.addStr = addStr;
    }
}