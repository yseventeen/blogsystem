package com.yseventeen.blogsystem.service;

import com.yseventeen.blogsystem.domain.Catalog;
import com.yseventeen.blogsystem.domain.User;
import com.yseventeen.blogsystem.repository.CatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CatalogService {

    @Autowired
    private CatalogRepository catalogRepository;

    public List<Catalog> listCatalogs(User user) {
        return catalogRepository.findAll();
    }

    public Catalog getCatalogById(Long catalogId) {
        Optional<Catalog> catalog = catalogRepository.findById(catalogId);
        return catalog.get();
    }
}
