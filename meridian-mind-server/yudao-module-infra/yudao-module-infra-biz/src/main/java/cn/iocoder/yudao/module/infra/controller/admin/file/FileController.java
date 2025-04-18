package cn.iocoder.yudao.module.infra.controller.admin.file;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.infra.controller.admin.file.vo.file.*;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileDO;
import cn.iocoder.yudao.module.infra.framework.file.core.utils.FileTypeUtils;
import cn.iocoder.yudao.module.infra.service.file.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.module.infra.framework.file.core.utils.FileTypeUtils.writeAttachment;

@Tag(name = "管理后台 - 文件存储")
@RestController
@RequestMapping("/infra/file")
@Validated
@Slf4j
public class FileController {

    @Resource
    private FileService fileService;

    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "模式一：后端上传文件")
    public CommonResult<String> uploadFile(FileUploadReqVO uploadReqVO) throws Exception {
        MultipartFile file = uploadReqVO.getFile();
        String path = uploadReqVO.getPath();
        return success(fileService.createFile(file.getOriginalFilename(), path, IoUtil.readBytes(file.getInputStream())));
    }

    @GetMapping("/presigned-url")
    @Operation(summary = "获取文件预签名地址", description = "模式二：前端上传文件：用于前端直接上传七牛、阿里云 OSS 等文件存储器")
    public CommonResult<FilePresignedUrlRespVO> getFilePresignedUrl(@RequestParam("path") String path) throws Exception {
        return success(fileService.getFilePresignedUrl(path));
    }

    @PostMapping("/create")
    @Operation(summary = "创建文件", description = "模式二：前端上传文件：配合 presigned-url 接口，记录上传了上传的文件")
    public CommonResult<Long> createFile(@Valid @RequestBody FileCreateReqVO createReqVO) {
        return success(fileService.createFile(createReqVO));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除文件")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('infra:file:delete')")
    public CommonResult<Boolean> deleteFile(@RequestParam("id") Long id) throws Exception {
        fileService.deleteFile(id);
        return success(true);
    }

    @GetMapping("/{configId}/get/**")
    @PermitAll
    @Operation(summary = "下载或预览文件")
    @Parameter(name = "configId", description = "配置编号", required = true)
    public void getFileContent(HttpServletRequest request,
                               HttpServletResponse response,
                               @PathVariable("configId") Long configId) throws Exception {
        // 获取请求的路径
        String path = StrUtil.subAfter(request.getRequestURI(), "/get/", false);
        if (StrUtil.isEmpty(path)) {
            throw new IllegalArgumentException("结尾的 path 路径必须传递");
        }
        // 解码，解决中文路径的问题 https://gitee.com/zhijiantianya/ruoyi-vue-pro/pulls/807/
        path = URLUtil.decode(path);

        // 读取内容
        byte[] content = fileService.getFileContent(configId, path);
        if (content == null) {
            log.warn("[getFileContent][configId({}) path({}) 文件不存在]", configId, path);
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }

        // 获取文件类型
        String contentType = FileTypeUtils.getMineType(content, path);

        // 处理音视频文件的流式传输
        if (contentType.contains("video/") || contentType.contains("audio/")) {
            // 支持断点续传和范围请求
            String rangeHeader = request.getHeader("Range");
            if (StrUtil.isNotBlank(rangeHeader)) {
                // 处理范围请求
                handleRangeRequest(response, content, contentType, rangeHeader);
                return;
            }
        }

        writeAttachment(response, path, content);
    }

    /**
     * 处理范围请求，支持视频音频的拖动播放
     */
    private void handleRangeRequest(HttpServletResponse response, byte[] content,
                                    String contentType, String rangeHeader) throws IOException {
        // 解析请求范围
        long[] range = parseRange(rangeHeader, content.length);
        long start = range[0];
        long end = range[1];
        long contentLength = end - start + 1;

        // 设置响应头
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        response.setContentType(contentType);
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + content.length);
        response.setContentLengthLong(contentLength);

        // 写入指定范围的数据
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            outputStream.write(content, (int) start, (int) contentLength);
            outputStream.flush();
        }
    }

    /**
     * 解析Range头信息
     * @return [start, end]
     */
    private long[] parseRange(String rangeHeader, int contentLength) {
        // 默认返回整个文件
        long start = 0;
        long end = contentLength - 1;

        // 解析范围请求
        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String rangeValue = rangeHeader.substring("bytes=".length());
            String[] parts = rangeValue.split("-");
            if (parts.length == 2) {
                // 处理 "bytes=x-y" 格式
                if (!parts[0].isEmpty()) {
                    start = Long.parseLong(parts[0]);
                }
                if (!parts[1].isEmpty()) {
                    end = Long.parseLong(parts[1]);
                }
            } else if (parts.length == 1 && !parts[0].isEmpty()) {
                // 处理 "bytes=x-" 格式
                start = Long.parseLong(parts[0]);
            }
        }

        // 确保范围有效
        if (start < 0) {
            start = 0;
        }
        if (end >= contentLength) {
            end = contentLength - 1;
        }

        return new long[]{start, end};
    }

    @GetMapping("/page")
    @Operation(summary = "获得文件分页")
    @PreAuthorize("@ss.hasPermission('infra:file:query')")
    public CommonResult<PageResult<FileRespVO>> getFilePage(@Valid FilePageReqVO pageVO) {
        PageResult<FileDO> pageResult = fileService.getFilePage(pageVO);
        return success(BeanUtils.toBean(pageResult, FileRespVO.class));
    }

}
