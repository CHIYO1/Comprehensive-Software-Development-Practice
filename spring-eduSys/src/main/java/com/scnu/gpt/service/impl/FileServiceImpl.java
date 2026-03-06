package com.scnu.gpt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.scnu.gpt.config.ApiConfig;
import com.scnu.gpt.entity.Document;
import com.scnu.gpt.entity.Video;
import com.scnu.gpt.mapper.DocumentMapper;
import com.scnu.gpt.mapper.VideoMapper;
import com.scnu.gpt.pojo.ApiResponse;
import com.scnu.gpt.pojo.file.FileQueryDTO;
import com.scnu.gpt.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.math.BigDecimal;
@Service
public class FileServiceImpl implements IFileService {
    @Autowired
    private DocumentMapper documentMapper;
    @Autowired
    private VideoMapper videoMapper;

    private final RestTemplate restTemplate;
    // Nginx上传地址
    private final String NGINX_UPLOAD_URL;

    @Autowired
    public FileServiceImpl(RestTemplate restTemplate,ApiConfig apiConfig) {
        this.restTemplate = restTemplate;
        this.NGINX_UPLOAD_URL = apiConfig.nginx().url();
    }
    // 视频扩展名集合
    private static final Set<String> VIDEO_EXTENSIONS = Set.of(
            "mp4", "mov", "avi", "mkv", "flv", "wmv", "webm", "mpg", "mpeg"
    );
    // 图片扩展名集合
    private static final Set<String> IMAGE_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "webp", "bmp",
            "tiff", "tif", "svg", "ico", "psd", "heic", "heif",
            "raw", "arw", "cr2", "nef", "dng", "ai", "eps"
    );
    @Override
    public ArrayList<String> uploadFile(ArrayList<MultipartFile> files) {
        ArrayList<String> urls = new ArrayList<>();

        for (MultipartFile file : files) {
            String extension = getExtension(file.getOriginalFilename());
            String fileName = UUID.randomUUID() + extension;
            String nginxUrl = NGINX_UPLOAD_URL + fileName;
//            if (VIDEO_EXTENSIONS.contains(extension)){//视频文件处理
//                nginxUrl = NGINX_UPLOAD_URL + "videos" + fileName;
//            }else if (IMAGE_EXTENSIONS.contains(extension)){//图片文件处理
//                nginxUrl = NGINX_UPLOAD_URL + "imgs" + fileName;
//            }else {//其它文件处理
//                nginxUrl = NGINX_UPLOAD_URL + "files" + fileName;
//            }
            try {
                // 构建PUT请求
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                HttpEntity<byte[]> entity = new HttpEntity<>(
                        file.getBytes(),
                        headers
                );
                // 发送文件到Nginx
                restTemplate.put(nginxUrl, entity);

                urls.add(fileName);
            } catch (Exception exception) {
                System.out.println("nginx上传错误："+exception);
            }
        }
        return urls;
    }

    public boolean deleteFile(String url) {
        try {
            restTemplate.exchange(NGINX_UPLOAD_URL+url, HttpMethod.DELETE, null, Void.class);
        }catch (Exception exception){
            System.out.println("nginx删除错误："+exception);
            return false;
        }
        return true;
    }

    @Override
    public ApiResponse<Video> uploadTeacherVideo(MultipartFile video, MultipartFile cover, int userId) {
        ArrayList<MultipartFile> tempVideoList = new ArrayList<>();
        tempVideoList.add(video);
        ArrayList<MultipartFile> tempPicList = new ArrayList<>();
        tempPicList.add(cover);

        String videoUrl = uploadFile(tempVideoList).getFirst();
        String coverUrl = uploadFile(tempPicList).getFirst();
        if(videoUrl==null || coverUrl==null){
            return new ApiResponse<>("500","nginx文件上传失败",null);
        }
        Video videoEntity = new Video();//视频插入数据库
        videoEntity.setVideoPath(videoUrl);
        videoEntity.setVideoName(video.getOriginalFilename());
        videoEntity.setCoverPath(coverUrl);
        videoEntity.setUserId(userId);
        videoEntity.setCreatedDate(LocalDate.now());
        videoMapper.insert(videoEntity);
        videoEntity.setVideoPath(NGINX_UPLOAD_URL + videoUrl);
        videoEntity.setCoverPath(NGINX_UPLOAD_URL + coverUrl);
        return new ApiResponse<>("200","成功上传",videoEntity);
    }

    @Override
    public ArrayList<Video> queryTeacherVideos(int userId) {
        QueryWrapper<Video> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("video_id");
        ArrayList<Video> results = (ArrayList<Video>) videoMapper.selectList(wrapper);
        for (Video video : results) {
            video.setVideoPath(NGINX_UPLOAD_URL+video.getVideoPath());
            video.setCoverPath(NGINX_UPLOAD_URL+video.getCoverPath());
        }
        return results;
    }

    @Override
    public ApiResponse<Void> deleteVideo(int videoId) {
        Video video = videoMapper.selectById(videoId);
        if(!deleteFile(video.getVideoPath())){
            return new ApiResponse<>("500","nginx删除失败",null);
        }
        if(!deleteFile(video.getCoverPath())){
            return new ApiResponse<>("500","nginx删除失败",null);
        }
        videoMapper.deleteById(videoId);
        return new ApiResponse<>("200","成功删除",null);
    }

    @Override
    public ArrayList<Document> queryTeacherDocuments(int userId) {
        QueryWrapper<Document> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("document_id");
        ArrayList<Document> results = (ArrayList<Document>) documentMapper.selectList(wrapper);
        for (Document document : results) {
            document.setPath(NGINX_UPLOAD_URL+document.getPath());
        }
        return results;
    }

    @Override
    public ApiResponse<Document> uploadTeacherDocument(MultipartFile document, String documentDesc,int userId) {
        ArrayList<MultipartFile> tempDocumentList = new ArrayList<>();
        tempDocumentList.add(document);

        String documentUrl = uploadFile(tempDocumentList).getFirst();
        if(documentUrl==null){
            return new ApiResponse<>("500","nginx文件上传失败",null);
        }

        Document documentEntity = new Document();
        documentEntity.setPath(documentUrl);
        documentEntity.setDocumentName(document.getOriginalFilename());
        documentEntity.setUserId(userId);
        documentEntity.setCreatedDate(LocalDate.now());
        documentEntity.setDocumentDesc(documentDesc);
        documentEntity.setDocumentSize(unitTransform((double) document.getSize())); //单位转化
        documentMapper.insert(documentEntity);
        documentEntity.setPath(NGINX_UPLOAD_URL+document.getOriginalFilename());
        return new ApiResponse<>("200","成功上传",documentEntity);
    }

    @Override
    public ApiResponse<Void> deleteDocument(int documentId) {
        Document document = documentMapper.selectById(documentId);
        if(!deleteFile(document.getPath())){
            return new ApiResponse<>("500","nginx删除失败",null);
        }
        documentMapper.deleteById(documentId);
        return new ApiResponse<>("200","成功删除",null);
    }

    @Override
    public ApiResponse<FileQueryDTO> queryFile(FileQueryDTO fileQueryDTO) {
        switch (fileQueryDTO.fileType()){
            case "document":
                Document document = documentMapper.selectById(fileQueryDTO.fileId());
                return new ApiResponse<>("200","成功",new FileQueryDTO(
                        fileQueryDTO.fileId(),
                        fileQueryDTO.fileType(),
                        document.getDocumentName(),
                        NGINX_UPLOAD_URL+document.getPath()
                ));
            case "video":
                videoMapper.selectById(fileQueryDTO.fileId());
                Video video = videoMapper.selectById(fileQueryDTO.fileId());
                return new ApiResponse<>("200","成功",new FileQueryDTO(
                        fileQueryDTO.fileId(),
                        fileQueryDTO.fileType(),
                        video.getVideoName(),
                        NGINX_UPLOAD_URL+video.getVideoPath()
                ));
            default:
                return new ApiResponse<>("500","未知的文件类型",null);
        }
    }

    //将单位为B的size转化为适合的单位，至多为GB，保留2位小数(以String的形式输出，带单位)。
    private static String unitTransform(double size){
        String[] units = {"B", "KB", "MB", "GB"};
        int i;
        for(i=0;i<3;i++){
            double nextValue = size / 1024.0;
            if (nextValue < 1) {
                break;
            }
            size = nextValue;
        }
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(size) + units[i];
    }

    //获取文件扩展名
    private static String getExtension(String fileName) {
        return fileName != null && fileName.contains(".")
                ? fileName.substring(fileName.lastIndexOf("."))
                : "";
    }

}
