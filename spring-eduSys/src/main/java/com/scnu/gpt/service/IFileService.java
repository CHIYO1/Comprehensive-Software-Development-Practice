package com.scnu.gpt.service;


import com.scnu.gpt.entity.Document;
import com.scnu.gpt.entity.Video;
import com.scnu.gpt.pojo.ApiResponse;
import com.scnu.gpt.pojo.file.FileQueryDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

public interface IFileService {
    //返回值：文件保存在nginx的相对路径。拼接NGINX_UPLOAD_URL后即可通过这个url访问这个文件资源
    ArrayList<String> uploadFile(ArrayList<MultipartFile> files);

    ApiResponse<Video> uploadTeacherVideo(MultipartFile video, MultipartFile cover, int userId);

    ArrayList<Video> queryTeacherVideos(int userId);

    ApiResponse<Void> deleteVideo(int videoId);

    ArrayList<Document> queryTeacherDocuments(int userId);

    ApiResponse<Document> uploadTeacherDocument(MultipartFile document, String documentDesc,int userId);

    ApiResponse<Void> deleteDocument(int documentId);

    ApiResponse<FileQueryDTO> queryFile(FileQueryDTO fileQueryDTO);
}
