package org.example.justgongdae.data;

import lombok.Data;

import java.util.List;


@Data
public class Paper {
    private String DOI;
    private String title;
    private String abstractText;
    private List<String> authors;
}