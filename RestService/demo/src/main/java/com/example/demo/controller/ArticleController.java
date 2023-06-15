package com.example.demo.controller;


import com.example.demo.exceptions.ArticleExistEx;
import com.example.demo.exceptions.ArticleNotFoundEx;
import com.example.demo.model.Article;
import com.example.demo.model.CustomImage;
import com.example.demo.rabbit.RabbitMQProducerClient;
import com.example.demo.rabbit.RabbitMQRpcClient;
import com.example.demo.repo.ArticleRepository;
import com.example.demo.repo.ArticleRepositoryImpl;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@CrossOrigin(origins="*")
public class ArticleController {

    private ArticleRepository dataRepo = new ArticleRepositoryImpl();
    private RabbitMQProducerClient mqClient;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public ArticleController() throws IOException, TimeoutException {
        this.mqClient = new RabbitMQProducerClient();
    }

    @GetMapping("/articles/{id}")
    public EntityModel<Article> getArticle(@PathVariable int id) throws IOException{
        mqClient.sendInfo("GET /articles/" + id);
        try {
            return EntityModel.of(dataRepo.getArticle(id),
                    linkTo(methodOn(ArticleController.class).deleteArticle(id)).withRel("delete"),
                    linkTo(methodOn(ArticleController.class).sellArticle(id)).withRel("sell"),
                    linkTo(methodOn(ArticleController.class).getAllArticles()).withRel("all"));
        } catch (ArticleNotFoundEx e) {
            mqClient.sendError("ERROR ArticleNotFoundEx on GET /articles/" + id);
            throw e;
        }
    }

    @GetMapping("/articles")
    public CollectionModel<EntityModel<Article>> getAllArticles() throws IOException{
        mqClient.sendInfo(LocalDateTime.now().format(formatter) + "  --  GET /articles");
        try {
            List<EntityModel<Article>> articles =
                    dataRepo.getAllArticles().stream().map(article ->
                            {
                                try {
                                    return EntityModel.of(article,
                                            linkTo(methodOn(ArticleController.class).deleteArticle(article.getId())).withRel("delete"),
                                            linkTo(methodOn(ArticleController.class).sellArticle(article.getId())).withRel("sell"),
                                            linkTo(methodOn(ArticleController.class).getArticle(article.getId())).withSelfRel()
                                    );
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            }
                    ).collect(Collectors.toList());
            return CollectionModel.of(articles, linkTo(methodOn(ArticleController.class)
                    .getAllArticles()).withSelfRel());
        } catch (ArticleNotFoundEx e) {
            mqClient.sendError(LocalDateTime.now().format(formatter) + "  --  ERROR ArticleNotFoundEx on GET /articles");
            throw e;
        }
    }

    @PostMapping("/articles")
    public EntityModel<Article> postArticle(@Valid @RequestBody Article requestBody) throws IOException{
        mqClient.sendInfo(LocalDateTime.now().format(formatter) + "  --  POST /articles");
        try {
            Article article = dataRepo.addArticle(requestBody.getId(), requestBody.getName(), requestBody.getPrice(), requestBody.isSold(), requestBody.isAvailable());
            return EntityModel.of(article,
                    linkTo(methodOn(ArticleController.class).deleteArticle(article.getId())).withRel("delete"),
                    linkTo(methodOn(ArticleController.class).sellArticle(article.getId())).withRel("sell"),
                    linkTo(methodOn(ArticleController.class).getArticle(article.getId())).withRel("get"),
                    linkTo(methodOn(ArticleController.class).getAllArticles()).withRel("get all"));
        } catch (ArticleExistEx e) {
            mqClient.sendError(LocalDateTime.now().format(formatter) + "  --  ERROR ArticleExistEx on POST /articles");
            throw e;
        }
    }

    @DeleteMapping("/articles/{id}")
    public EntityModel<Article> deleteArticle(@PathVariable int id) throws IOException{
        mqClient.sendInfo(LocalDateTime.now().format(formatter) + "  --  DELETE /articles/" + id);
        try {
            Article article = dataRepo.deleteArticle(id);
            return EntityModel.of(article,
                    linkTo(methodOn(ArticleController.class).sellArticle(article.getId())).withRel("sell"),
                    linkTo(methodOn(ArticleController.class).getArticle(article.getId())).withRel("get"),
                    linkTo(methodOn(ArticleController.class).getAllArticles()).withRel("get all"));
        } catch (ArticleNotFoundEx e) {
            mqClient.sendError(LocalDateTime.now().format(formatter) + "  --  ERROR ArticleNotFoundEx on DELETE /articles/" + id);
            throw e;
        }
    }

    @PutMapping("/articles/{id}/sell")
    public EntityModel<Article> sellArticle(@PathVariable int id) throws IOException{
        mqClient.sendInfo(LocalDateTime.now().format(formatter) + "  --  PUT /articles/" + id + "/sell");
        try {
            Article article = dataRepo.sellArticle(id, true);
            return EntityModel.of(article,
                    linkTo(methodOn(ArticleController.class).deleteArticle(id)).withRel("delete"),
                    linkTo(methodOn(ArticleController.class).getArticle(article.getId())).withRel("get"),
                    linkTo(methodOn(ArticleController.class).getAllArticles()).withRel("get all"));
        }
        catch (ArticleNotFoundEx e) {
            mqClient.sendError(LocalDateTime.now().format(formatter) + "  --  ERROR ArticleNotFoundEx on PUT /articles/" + id + "/sell");
            throw e;
        }
    }

    @GetMapping("/articles/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable int id) throws IOException, TimeoutException, ExecutionException, InterruptedException {
        mqClient.sendInfo(LocalDateTime.now().format(formatter) + "  --  GET /articles/" + id + "/image");
        RabbitMQRpcClient rpcClient = new RabbitMQRpcClient();
        Article article = dataRepo.getArticle(id);
        if (article.getFileName() == null) return ResponseEntity.ok().body(new byte[1]);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return ResponseEntity.ok()
                .headers(headers)
                .body(rpcClient.send(article.getFileName()));
    }


}
