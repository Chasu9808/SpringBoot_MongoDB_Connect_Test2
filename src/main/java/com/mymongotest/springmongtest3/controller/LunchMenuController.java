package com.mymongotest.springmongtest3.controller;



import com.mymongotest.springmongtest3.document.LunchMenu;

import com.mymongotest.springmongtest3.document.Memo;
import com.mymongotest.springmongtest3.service.ImageService;
import com.mymongotest.springmongtest3.service.LunchMenuService;

import lombok.RequiredArgsConstructor;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class LunchMenuController {

    private final LunchMenuService lunchMenuService;
    private final ImageService imageService;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    //실제사용
    @ResponseBody
    @PostMapping("/insertLunchMenuWithImage")
    public ResponseEntity insertLunchMenuWithImage(@RequestPart(value = "key") LunchMenu lunchMenu,
                                              @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {


        String filename = "";


        if (file != null) {
            //원본이미지
            filename = file.getOriginalFilename();
            String str = filename.substring(filename.lastIndexOf(".") + 1);
            if (!str.equals("mp4") && !str.equals("mov") && !str.equals("MOV") && !str.equals("avi") && !str.equals("wmv")) {

                // 전달 받은 이미지를 , 리사이즈에서, 다시 이미지 재정의
                InputStream inputStream = file.getInputStream();
                //썸네일 작업
                BufferedImage bo_img = ImageIO.read(inputStream);
//		    double ratio = 3;
//	        int width = (int) (bo_img.getWidth() / ratio);
//	        int height = (int) (bo_img.getHeight() / ratio);
                int newWidth = 200; // 새로운 너비
                int newHeight = 200; // 새로운 높이

                // 200x200 리사이즈 된 이미지
                BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics2D = resizedImage.createGraphics();
                graphics2D.drawImage(bo_img, 0, 0, newWidth, newHeight, null);
                graphics2D.dispose();

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(resizedImage, "jpg", outputStream);
                InputStream reSizeInputStream = new ByteArrayInputStream(outputStream.toByteArray());
                // 전달 받은 이미지를 , 리사이즈에서, 다시 이미지 재정의

//  몽고 디비에 해당 데이터를 저장하는 코드
// 몽고디비에 바이트 단위로 이미지 저장하는 코드
                ObjectId objectId = gridFsTemplate.store(reSizeInputStream, file.getOriginalFilename(), file.getContentType());
                // 몽고 디비에 저장된 이미지 파일 오브젝트 아이디를 가져오기.
                String objectIdToString = objectId.toString();
//		System.out.println("objectIdToString : " + objectIdToString);
                String imageFileName = file.getOriginalFilename();
                // 몽고 디비의 데이터 모델링, 마치 RDBMS 외래키 사용 하듯이,
                // 이미지 물리 파일의 저장한 아이디만 가지고 있기.
                lunchMenu.setImageFileObjectId(objectIdToString);
                lunchMenu.setImageFileName(imageFileName);
            } else {
                InputStream inputStream = file.getInputStream();
                ObjectId objectId = gridFsTemplate.store(inputStream, file.getOriginalFilename(), file.getContentType());
                String objectIdToString = objectId.toString();
//		System.out.println("objectIdToString : " + objectIdToString);
                String imageFileName = file.getOriginalFilename();
                lunchMenu.setImageFileObjectId(objectIdToString);
                lunchMenu.setImageFileName(imageFileName);
            }

        }
        lunchMenuService.lunchMenuInsert(lunchMenu);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ResponseBody
    @PostMapping("/insertLunchMenu")
    public ResponseEntity<String> insertMemo(@RequestBody LunchMenu lunchMenu) {
        lunchMenuService.lunchMenuInsert(lunchMenu);
        return new ResponseEntity<String>("success", HttpStatus.OK);
    }


    @ResponseBody
    @PostMapping("/updateLunchMenu")
    public ResponseEntity<String> updateLunchMenu(@RequestBody LunchMenu lunchMenu) {
        lunchMenuService.lunchMenuUpdate(lunchMenu);
        return new ResponseEntity<String>("success", HttpStatus.OK);
    }


    //실제사용
    @ResponseBody
    @GetMapping("/findAllLunchMenu")
    public List<LunchMenu> findAllLunchMenu() {
        List<LunchMenu> lunchMenuList = lunchMenuService.lunchMenuFindAll();
        return lunchMenuList;
    }

//실제사용
    @ResponseBody
    @DeleteMapping("/dbDeleteLunchMenu/{id}/{imageFileName}")
    public String delete(@PathVariable String id, @PathVariable String imageFileName) {

        lunchMenuService.lunchMenuDeleteDb("_id", id);
        imageService.deleteImage(imageFileName);
        return id;

    }
}
