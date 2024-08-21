package com.sky.controller.admin;

import com.sky.constant.FileUrlConstant;
import com.sky.dto.UploadFileDTO;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.Array;
import java.util.Arrays;

@RestController
@RequestMapping("/upload")
@Slf4j
@Api(tags = "文件上传接口")
public class UploadFileController {

    @PostMapping("/chunk")
    @ApiOperation("文件分片上传")
    public Result uploadFile(@RequestParam MultipartFile chunk ,@RequestParam String chunkHash,@RequestParam String fileHash,@RequestParam String fileName) throws IOException {

        //定义接受文件的地址
        String dir = FileUrlConstant.UPLOAD_FILE_PATH +fileHash;

        //判断文件是否存在
        String ext = fileName.substring(fileName.lastIndexOf("."));
        if(new File(dir+ext).exists()){
            return Result.success("上传成功");
        }
        //文件不存在
        File file = new File(dir);
        if(!file.exists()){
            file.mkdir();
        }else{
            if(!file.isDirectory()){
                file.mkdir();
            }else{
                // TODO 断点续传的逻辑
            }
        }
        chunk.transferTo(new File(dir,chunkHash));

        return Result.success();
    }

    @PostMapping("/merge")
    @ApiOperation("文件合并请求")
    public Result merge(@RequestBody UploadFileDTO uploadFileDTO) throws IOException {
        //判断该文件是否已存在
        //获取文件后缀
        String fileName = uploadFileDTO.getFileName();
        String extension = fileName.substring(fileName.lastIndexOf("."));
        File dir = new File(FileUrlConstant.UPLOAD_FILE_PATH+uploadFileDTO.getFileHash());

        String filePath = FileUrlConstant.UPLOAD_FILE_PATH+uploadFileDTO.getFileHash()+extension;

        if(new File(filePath).exists()){
            return Result.success("合并成功");
        }

        //读取文件夹,如果不存在,return
        if(!dir.exists()){
            return Result.error("合并失败,请重试");
        }

        //开始合并
        File[] chunks = dir.listFiles();

        Arrays.sort(chunks);
        log.info("开始读取: {}",uploadFileDTO.getFileName());

        assert chunks != null;
        if(chunks.length != 0){
            for (int i = 0; i < chunks.length; i++) {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(FileUrlConstant.UPLOAD_FILE_PATH+uploadFileDTO.getFileHash()+"\\\\"+uploadFileDTO.getFileHash()+"-"+i));
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(FileUrlConstant.UPLOAD_FILE_PATH + uploadFileDTO.getFileHash() + extension,true));
                int b = 0;
                while ((b = bis.read()) != -1){
                    bos.write(b);
                }
                bos.close();
                bis.close();
            }
            FileUtils.deleteDirectory(new File(FileUrlConstant.UPLOAD_FILE_PATH + uploadFileDTO.getFileHash()));
            return Result.success("合并成功");
        }
        return Result.success("合并成功");
    }
}
