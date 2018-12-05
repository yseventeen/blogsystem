package com.yseventeen.blogsystem.controller;

import com.yseventeen.blogsystem.domain.es.EsBlog;
import com.yseventeen.blogsystem.repository.es.EsBlogRepository;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "blogs")
public class BlogController {

    @Autowired
    private EsBlogRepository esBlogRepository;

    public void initEsBlog(){
        EsBlog esBlog = new EsBlog(UUID.randomUUID().toString(),"相思","唐代：王维","红豆生南国，春来发几枝。\n" +
                "愿君多采撷，此物最相思。");
        esBlogRepository.save(esBlog);
        esBlog = new EsBlog(UUID.randomUUID().toString(),"登鹳雀楼","唐代：王之涣","白日依山尽，黄河入海流。\n" +
                "欲穷千里目，更上一层楼。");
        esBlogRepository.save(esBlog);
        esBlog = new EsBlog(UUID.randomUUID().toString(),"静夜思","唐代：李白","床前明月光，疑是地上霜。\n" +
                "举头望明月，低头思故乡。");
        esBlogRepository.save(esBlog);
    }

    @GetMapping
    public List<EsBlog> findEsBlog(@RequestParam(name = "title", required = false) String title, @RequestParam(name = "summery", required = false) String summery, @RequestParam(name = "contain", required = false) String contain, @RequestParam(name = "pageIndex", required = false, defaultValue = "0") int pageIndex, @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize) {
        this.initEsBlog();
        Pageable pageable = PageRequest.of(pageIndex,pageSize);
        title = "楼";
        summery = "";
        contain = "";
        Page<EsBlog> pageEsBlog = esBlogRepository.findDistinctByTitleContainingOrSummeryContainingOrContentContaining(title, summery, contain, pageable);
        return pageEsBlog.getContent();
    }
}
