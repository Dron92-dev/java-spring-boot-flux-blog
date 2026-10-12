package io.hexlet.controller;

import io.hexlet.model.Article;
import io.hexlet.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    @Autowired private ArticleService articleService;

    @GetMapping(path = "")
    public Flux<Article> getArticles() {
        return articleService.findAll();
    }

    @GetMapping(path = "/{id}")
    public Mono<Article> getArticle(@PathVariable long id) {
        return articleService.findById(id);
    }

    @PostMapping(path = "")
    public Mono<Article> createArticle(@RequestBody Article article) {
        return articleService.create(article);
    }

    @DeleteMapping(path = "/{id}")
    public Mono<Void> deleteArticle(@PathVariable long id) {
        return articleService.delete(id);
    }

    @PatchMapping(path = "/{id}")
    public Mono<Article> updateArticle(@PathVariable long id, @RequestBody Article article) {
        return articleService.update(id, article);
    }
}
