package com.hodgepodge.framework.commond;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class JSchUtil {
    private static JSchUtil instance;

    public static JSchUtil getInstance() {
        if (null == instance) {
            instance = new JSchUtil();
        }
        return instance;
    }

    private JSchUtil() {

    }

    private Session getSession(String host, int port, String userName)
            throws Exception {
        JSch jSch = new JSch();
        //采用指定的端口连接服务器
        Session session = jSch.getSession(userName, host, port);
        return session;
    }

    public Session connect(String host, int port, String userName,
                           String password) throws Exception {
        Session session = getSession(host, port, userName);
        //设置登陆主机的密码
        session.setPassword(password);
        //设置第一次登陆的时候提示，可选值：(ask | yes | no)
        session.setConfig("StrictHostKeyChecking", "no");
        //设置登陆超时时间
        //session.connect(3000);
        session.connect();
        return session;
    }

    public String execCmd(Session session, String command) throws Exception {
        if (null == session) {
            throw new RuntimeException("Session is null!");
        }
        ChannelExec exec = (ChannelExec) session.openChannel("exec");
        exec.setCommand(command);

        BufferedReader fromServer = new BufferedReader(new InputStreamReader(
                exec.getInputStream()));
        exec.connect();
        StringBuffer sb = new StringBuffer();
        Thread.sleep(1000);
        while (fromServer.ready()) {
            String tt = fromServer.readLine();
            sb.append(tt + '\n');
        }
        if (null != fromServer) {
            fromServer.close();
        }
        exec.disconnect();
        if (null != session && session.isConnected()) {
            session.disconnect();
            session = null;
        }
        return sb.toString();
    }

    // public String execCmd(Session session, String command) throws Exception {
    // if (null == session) {
    // throw new RuntimeException("Session is null!");
    // }
    // ChannelExec exec = (ChannelExec) session.openChannel("exec");
    // InputStream in = exec.getInputStream();
    // byte[] b = new byte[1024];
    //
    // exec.setCommand(command);
    // exec.connect();
    // StringBuffer buffer = new StringBuffer();
    // while (in.read(b) > 0) {
    // buffer.append(new String(b));
    // }
    // exec.disconnect();
    //
    // return buffer.toString();
    // }

    public void clear(Session session) {
        if (null != session && session.isConnected()) {
            session.disconnect();
            session = null;
        }
    }

    public static void main(String[] args) throws Exception {
        Session session = JSchUtil.getInstance().connect("192.168.1.254", 22, "joy", "joyriver");
        String cmd = "cd /opt/JRGC-srv4old/bin/;./shutdown;./start";
        String result = JSchUtil.getInstance().execCmd(session, cmd);
        System.out.println(result);
    }
}