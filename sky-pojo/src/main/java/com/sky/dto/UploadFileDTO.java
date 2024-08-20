package com.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@ApiModel("分片上传文件数据")
public class UploadFileDTO {

    @ApiModelProperty("文件名")
    private String fileName;
    @ApiModelProperty("文件hash")
    private String fileHash;
    @ApiModelProperty("分片hash")
    private String chunkHash;
    @ApiModelProperty("分片内容")
    private MultipartFile chunk;
    @ApiModelProperty("分片大小")
    private Long size;
}
