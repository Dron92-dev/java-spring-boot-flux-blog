package io.hexlet.service;

import io.hexlet.model.Article;
import io.hexlet.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ArticleService {

    @Autowired private ArticleRepository articleRepository;

    public Flux<Article> findAll() {
        return articleRepository.findAll();
    }

    public Mono<Article> create(Article article) {
        return articleRepository.save(article);
    }

    public Mono<Article> findById(long articleId) {
        return articleRepository.findById(articleId);
    }

    public Mono<Article> update(long articleId, Article article) {
        article.setId(articleId);
        return articleRepository.save(article);
    }

    public Mono<Void> delete(long articleId) {
        return articleRepository.deleteById(articleId);
    }
}
