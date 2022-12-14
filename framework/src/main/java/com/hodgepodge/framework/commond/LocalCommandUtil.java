package com.hodgepodge.framework.commond;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class LocalCommandUtil {
    /**
     * 执行命令并显示结果
     */
    public static String exec4show(String command) throws Exception {
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
        BufferedReader fromServer = new BufferedReader(new InputStreamReader(process.getInputStream()));

        StringBuffer sb = new StringBuffer();
        while (fromServer.ready()) {
            String tt = fromServer.readLine();
            sb.append(tt + '\n');
        }
        if (null != fromServer) {
            fromServer.close();
        }
        return sb.toString();
    }

    public static void exec4wait(String command) throws Exception {
        Runtime.getRuntime().exec(command).waitFor();
    }

    public static void exec(String command) throws Exception {
        Runtime.getRuntime().exec(command);
    }
}
