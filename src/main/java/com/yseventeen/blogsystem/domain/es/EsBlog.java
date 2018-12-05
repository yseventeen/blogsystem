package com.yseventeen.blogsystem.domain.es;


import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Id;
import java.io.Serializable;

@Document(indexName = "blog",type = "blog")
public class EsBlog implements Serializable {
    @Id
    private String id;
    private String title;
    private String summery;
    private String content;

    public EsBlog() {
    }

    public EsBlog(String id, String title, String summery, String content) {
        this.id = id;
        this.title = title;
        this.summery = summery;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummery() {
        return summery;
    }

    public void setSummery(String summery) {
        this.summery = summery;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
