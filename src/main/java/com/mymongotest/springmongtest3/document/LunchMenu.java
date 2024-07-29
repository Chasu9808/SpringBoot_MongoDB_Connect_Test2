package com.mymongotest.springmongtest3.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Document("lunchmenu")
public class LunchMenu {

    @Id
    private String id;

    private String lunchMenu;
    private String lunchWriter;
    private String dateField;
    private String imageFileObjectId;
    private String imageFileName;
}