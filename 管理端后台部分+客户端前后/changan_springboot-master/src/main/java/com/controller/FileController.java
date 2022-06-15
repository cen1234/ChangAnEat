package com.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    private static final String PATH = "http://" + "192.168.1.238" + ":9090/changAn";

    /**
     * 上传接口
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {
        log.info("有图片需要上传");
        //获取当前文件的名称
        String originalFilename = file.getOriginalFilename();

        //定义文件的唯一标识
        String flag = IdUtil.fastSimpleUUID() + "_" +originalFilename;

        //获取需要上传的路径
        String path = System.getProperty("user.dir") + "/src/main/resources/img/" + flag;

        //将文件写入需要上传的路径
        FileUtil.writeBytes(file.getBytes(),path);

        return R.success(flag);
    }

    @GetMapping("/download")
    public R<String> getFile(HttpServletResponse response, String flag) throws IOException {
        log.info("图片回显！！！！");

        //获取文件的的根路径
        String path = System.getProperty("user.dir") + "/src/main/resources/img/";

        //输出流 通过输出流将文件写回浏览器
        FileInputStream fileInputStream = new FileInputStream(new File(path + flag));

        log.info(path + "" +flag);

        //新建一个输出流对象
        ServletOutputStream outputStream = response.getOutputStream();

        //设置返回的格式
        response.setContentType("image/jpeg");

        //传输
        int len = 0;
        byte[] bytes = new byte[1024];
        while ((len = fileInputStream.read(bytes))!= -1){
            outputStream.write(bytes,0,len);
            outputStream.flush();
        }
        //关闭资源
        outputStream.close();
        fileInputStream.close();

        return R.success("加载成功");
    }
}
