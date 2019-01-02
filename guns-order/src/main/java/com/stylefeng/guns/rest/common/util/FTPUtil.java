package com.stylefeng.guns.rest.common.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @ClassName FTPUtil
 * @Description FTP工具类
 * @Author lxd
 * @Date 2019/1/2 10:31
 **/
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "ftp")
public class FTPUtil {
    // 地址 端口 用户名 密码
    private String hostName;
    private Integer port;
    private String userName;
    private String password;

    private FTPClient ftpClient = null;

    private void initFTPClient(){
        try{
            ftpClient = new FTPClient();
            ftpClient.setControlEncoding("utf-8");
            ftpClient.connect(hostName,port);
            ftpClient.login(userName,password);
        }catch (Exception e){
            log.error("初始化FTP失败",e);
        }
    }

    // 输入一个路径，然后将路径里的文件转换成字符串返回
    public String getFileContentByAddress(String path) {
        BufferedReader bufferedReader = null;
        try{
            initFTPClient();
            bufferedReader = new BufferedReader(new InputStreamReader(ftpClient.retrieveFileStream(path)));
            StringBuffer stringBuffer = new StringBuffer();
            while (true) {
                String str = bufferedReader.readLine();
                if (str == null) {
                    break;
                }
                stringBuffer.append(str);
            }
            ftpClient.logout();
            return stringBuffer.toString();
        }catch (Exception ex) {
            log.error("获取文件失败",ex);
        }finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        FTPUtil ftpUtil = new FTPUtil();
        String fileStrByAddress = ftpUtil.getFileContentByAddress("seats/cgs.json");

        System.out.println(fileStrByAddress);
    }

}
