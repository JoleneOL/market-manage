package me.jiangcai.logistics.service;

import me.jiangcai.logistics.ProductTypeService;
import me.jiangcai.logistics.entity.ProductType;
import me.jiangcai.logistics.repository.ProductTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by helloztt on 2017-09-26.
 */
@Service("productTypeService")
public class ProductTypeServiceImpl implements ProductTypeService {
    @Autowired
    private ProductTypeRepository typeRepository;
    @Override
    public ProductType findOne(Long id) {
        return typeRepository.findOne(id);
    }
}
