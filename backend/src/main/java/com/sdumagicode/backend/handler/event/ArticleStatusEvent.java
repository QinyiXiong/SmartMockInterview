package com.sdumagicode.backend.handler.event;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created on 2022/8/20 18:51.
 *
 * @author ronger
 * @email ronger-x@outlook.com
 */
@Data
@AllArgsConstructor
public class ArticleStatusEvent {

    private Long idArticle;

    private Long articleAuthor;

    private String message;

}
