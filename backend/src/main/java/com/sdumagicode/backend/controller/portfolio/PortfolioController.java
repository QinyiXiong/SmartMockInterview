package com.sdumagicode.backend.controller.portfolio;

import com.github.pagehelper.PageInfo;
import com.sdumagicode.backend.core.exception.ServiceException;
import com.sdumagicode.backend.core.result.GlobalResult;
import com.sdumagicode.backend.core.result.GlobalResultGenerator;
import com.sdumagicode.backend.core.service.security.annotation.AuthorshipInterceptor;
import com.sdumagicode.backend.dto.PortfolioArticleDTO;
import com.sdumagicode.backend.dto.PortfolioDTO;
import com.sdumagicode.backend.entity.Portfolio;
import com.sdumagicode.backend.entity.User;
import com.sdumagicode.backend.enumerate.Module;
import com.sdumagicode.backend.service.PortfolioService;
import com.sdumagicode.backend.service.UserService;
import com.sdumagicode.backend.util.UserUtils;
import com.sdumagicode.backend.util.PhotoUploadUtil;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import javax.annotation.Resource;

/**
 * @author ronger
 */
@RestController
@RequestMapping("/api/v1/portfolio")
public class PortfolioController {

    @Resource
    private PortfolioService portfolioService;
    @Resource
    private UserService userService;

    @GetMapping("/detail/{idPortfolio}")
    public GlobalResult<PortfolioDTO> detail(@PathVariable Long idPortfolio, @RequestParam(defaultValue = "0") Integer type) {
        if (idPortfolio == null || idPortfolio == 0) {
            throw new IllegalArgumentException("作品集主键参数异常!");
        }
        return GlobalResultGenerator.genSuccessResult(portfolioService.findPortfolioDTOById(idPortfolio, type));
    }

    @GetMapping("/image/{idPortfolio}/base64")
    public GlobalResult<String> getPortfolioImageAsBase64(@PathVariable Long idPortfolio, @RequestParam(defaultValue = "0") Integer type) throws IOException {
        // Get the portfolio DTO which contains the image path
        PortfolioDTO portfolioDTO = portfolioService.findPortfolioDTOById(idPortfolio, type);

        if (portfolioDTO == null || portfolioDTO.getHeadImgUrl() == null) {
            throw new FileNotFoundException("Portfolio or image not found");
        }
        String path = portfolioDTO.getHeadImgUrl().replace("src/main/resources/", "");
        String extension = path.substring(path.lastIndexOf(".") + 1);
        // 3. 确定MIME类型
        String mimeType;
        switch (extension) {
            case "png":  mimeType = "image/png"; break;
            case "jpg":
            case "jpeg": mimeType = "image/jpeg"; break;
            case "gif":  mimeType = "image/gif"; break;
            case "svg":  mimeType = "image/svg+xml"; break;
            case "webp": mimeType = "image/webp"; break;
            default:     mimeType = "application/octet-stream";
        }
        ClassPathResource resource = new ClassPathResource(path);
        // Read file content and encode as base64
        try (InputStream inputStream = resource.getInputStream()) {
            byte[] fileContent = IOUtils.toByteArray(inputStream);
            String base64 = Base64.getEncoder().encodeToString(fileContent);
            String res = "data:" + mimeType + ";base64," + base64;
            GlobalResult<String> result = GlobalResultGenerator.genSuccessResult("success");
            result.setData(res);
            return result;
        } catch (IOException e) {
            throw new RuntimeException("加载图片失败: " + path, e);
        }
    }


    @PostMapping("/post")
    @RequiresPermissions(value = "user")
    public GlobalResult<Portfolio> add(@RequestBody Portfolio portfolio) {
        User user = UserUtils.getCurrentUserByToken();
        portfolio.setPortfolioAuthorId(user.getIdUser());
        portfolio = portfolioService.postPortfolio(portfolio);
        return GlobalResultGenerator.genSuccessResult(portfolio);
    }

    @PutMapping("/post")
    @AuthorshipInterceptor(moduleName = Module.PORTFOLIO)
    public GlobalResult<Portfolio> update(@RequestBody Portfolio portfolio) {
        if (portfolio.getIdPortfolio() == null || portfolio.getIdPortfolio() == 0) {
            throw new IllegalArgumentException("作品集主键参数异常!");
        }
        User user = UserUtils.getCurrentUserByToken();
        portfolio.setPortfolioAuthorId(user.getIdUser());
        portfolio = portfolioService.postPortfolio(portfolio);
        return GlobalResultGenerator.genSuccessResult(portfolio);
    }

    @GetMapping("/{idPortfolio}/unbind-articles")
    @AuthorshipInterceptor(moduleName = Module.PORTFOLIO)
    public GlobalResult<PageInfo> unbindArticles(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer rows, @RequestParam(defaultValue = "") String searchText, @PathVariable Long idPortfolio) {
        if (idPortfolio == null || idPortfolio == 0) {
            throw new IllegalArgumentException("作品集主键参数异常!");
        }
        User user = UserUtils.getCurrentUserByToken();
        PageInfo pageInfo = portfolioService.findUnbindArticles(page, rows, searchText, idPortfolio, user.getIdUser());
        return GlobalResultGenerator.genSuccessResult(pageInfo);
    }

    @PostMapping("/bind-article")
    @AuthorshipInterceptor(moduleName = Module.PORTFOLIO)
    public GlobalResult<Boolean> bindArticle(@RequestBody PortfolioArticleDTO portfolioArticle) throws ServiceException {
        if (portfolioArticle.getIdPortfolio() == null || portfolioArticle.getIdPortfolio() == 0) {
            throw new IllegalArgumentException("作品集主键参数异常!");
        }
        boolean flag = portfolioService.bindArticle(portfolioArticle);
        return GlobalResultGenerator.genSuccessResult(flag);
    }

    @PutMapping("/update-article-sort-no")
    @AuthorshipInterceptor(moduleName = Module.PORTFOLIO)
    public GlobalResult<Boolean> updateArticleSortNo(@RequestBody PortfolioArticleDTO portfolioArticle) throws ServiceException {
        if (portfolioArticle.getIdPortfolio() == null || portfolioArticle.getIdPortfolio() == 0) {
            throw new IllegalArgumentException("作品集主键参数异常!");
        }
        if (portfolioArticle.getIdArticle() == null || portfolioArticle.getIdArticle() == 0) {
            throw new IllegalArgumentException("文章主键参数异常!");
        }
        if (portfolioArticle.getSortNo() == null) {
            throw new IllegalArgumentException("排序号不能为空!");
        }
        boolean flag = portfolioService.updateArticleSortNo(portfolioArticle);
        return GlobalResultGenerator.genSuccessResult(flag);
    }

    @DeleteMapping("/unbind-article")
    @AuthorshipInterceptor(moduleName = Module.PORTFOLIO)
    public GlobalResult<Boolean> unbindArticle(Long idArticle, Long idPortfolio) throws ServiceException {
        if (idPortfolio == null || idPortfolio == 0) {
            throw new IllegalArgumentException("作品集主键参数异常");
        }
        if (idArticle == null || idArticle == 0) {
            throw new IllegalArgumentException("文章主键参数异常");
        }
        boolean flag = portfolioService.unbindArticle(idPortfolio, idArticle);
        return GlobalResultGenerator.genSuccessResult(flag);
    }

    @DeleteMapping("/delete")
    @AuthorshipInterceptor(moduleName = Module.PORTFOLIO)
    public GlobalResult<Boolean> delete(Long idPortfolio) {
        if (idPortfolio == null || idPortfolio == 0) {
            throw new IllegalArgumentException("参数异常!");
        }
        User user = UserUtils.getCurrentUserByToken();
        Long idUser = user.getIdUser();
        Integer roleWeights = userService.findRoleWeightsByUser(idUser);
        boolean flag = portfolioService.deletePortfolio(idPortfolio, idUser, roleWeights);
        return GlobalResultGenerator.genSuccessResult(flag);
    }

}
