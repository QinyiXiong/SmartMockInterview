package com.sdumagicode.backend.service.impl;

import com.sdumagicode.backend.core.service.AbstractService;
import com.sdumagicode.backend.dto.ProductDTO;
import com.sdumagicode.backend.entity.Product;
import com.sdumagicode.backend.enumerate.FilePath;
import com.sdumagicode.backend.enumerate.FileDataType;
import com.sdumagicode.backend.mapper.ProductMapper;
import com.sdumagicode.backend.service.ProductService;
import com.sdumagicode.backend.util.BeanCopierUtil;
import com.sdumagicode.backend.controller.common.UploadController;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created on 2022/6/21 9:26.
 *
 * @author ronger
 * @email ronger-x@outlook.com
 * @packageName com.rymcu.forest.service.impl
 */
@Service
public class ProductServiceImpl extends AbstractService<Product> implements ProductService {

    @Resource
    private ProductMapper productMapper;

    @Override
    public List<ProductDTO> findProducts() {
        return productMapper.selectProducts();
    }

    @Override
    public ProductDTO findProductDTOById(Long idProduct, Integer type) {
        return productMapper.selectProductDTOById(idProduct, type);
    }

    @Override
    public List<ProductDTO> findOnlineProducts() {
        return productMapper.selectOnlineProducts();
    }

    /**
     * @param product 产品信息
     * @return 产品信息
     */
    @Override
    public Product postProduct(ProductDTO product) {
        boolean isUpdate = product.getIdProduct() > 0;
        if (FileDataType.BASE64.equals(product.getProductImgType())) {
            String headImgUrl = UploadController.uploadBase64File(product.getProductImgUrl(), FilePath.PRODUCT);
            product.setProductImgUrl(headImgUrl);
            product.setProductImgType(FileDataType.URL);
        }
        Product newProduct;
        if (isUpdate) {
            newProduct = productMapper.selectByPrimaryKey(product.getIdProduct());
            newProduct.setProductImgUrl(product.getProductImgUrl());
            newProduct.setProductTitle(product.getProductTitle());
            newProduct.setProductPrice(product.getProductPrice());
            newProduct.setTags(product.getTags());
            newProduct.setProductDescription(product.getProductDescription());
            newProduct.setUpdatedTime(new Date());
            productMapper.updateByPrimaryKeySelective(newProduct);
            // 更新产品详情
            productMapper.updateProductContent(newProduct.getIdProduct(), product.getProductContent(), product.getProductContentHtml());
        } else {
            newProduct = new Product();
            BeanCopierUtil.convert(product, newProduct);
            newProduct.setCreatedTime(new Date());
            productMapper.insertSelective(newProduct);
            // 创建产品详情
            productMapper.insertProductContent(newProduct.getIdProduct(), product.getProductContent(), product.getProductContentHtml());
        }
        return newProduct;
    }

    /**
     * @param idProduct 产品主键
     * @param status    状态
     * @return 更新成功状态
     */
    @Override
    public boolean updateStatus(Long idProduct, Integer status) {
        return productMapper.updateStatus(idProduct, status) > 0;
    }
}
