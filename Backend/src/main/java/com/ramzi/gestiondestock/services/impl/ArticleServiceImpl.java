package com.ramzi.gestiondestock.services.impl;

import com.ramzi.gestiondestock.dto.ArticleDto;
import com.ramzi.gestiondestock.dto.LigneCommandeClientDto;
import com.ramzi.gestiondestock.dto.LigneCommandeFournisseurDto;
import com.ramzi.gestiondestock.dto.LigneVenteDto;
import com.ramzi.gestiondestock.exception.EntityNotFoundException;
import com.ramzi.gestiondestock.exception.ErrorCodes;
import com.ramzi.gestiondestock.exception.InvalidEntityException;
import com.ramzi.gestiondestock.exception.InvalidOperationException;
import com.ramzi.gestiondestock.model.LigneCommandeClient;
import com.ramzi.gestiondestock.model.LigneCommandeFournisseur;
import com.ramzi.gestiondestock.model.LigneVente;
import com.ramzi.gestiondestock.repository.ArticleRepository;
import com.ramzi.gestiondestock.repository.LigneCommandeClientRepository;
import com.ramzi.gestiondestock.repository.LigneCommandeFournisseurRepository;
import com.ramzi.gestiondestock.repository.LigneVenteRepository;
import com.ramzi.gestiondestock.services.ArticleService;
import com.ramzi.gestiondestock.validator.ArticleValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

  private ArticleRepository articleRepository;
  private LigneVenteRepository venteRepository;
  private LigneCommandeFournisseurRepository commandeFournisseurRepository;
  private LigneCommandeClientRepository commandeClientRepository;

  @Autowired
  public ArticleServiceImpl(
      ArticleRepository articleRepository,
      LigneVenteRepository venteRepository, LigneCommandeFournisseurRepository commandeFournisseurRepository,
      LigneCommandeClientRepository commandeClientRepository) {
    this.articleRepository = articleRepository;
    this.venteRepository = venteRepository;
    this.commandeFournisseurRepository = commandeFournisseurRepository;
    this.commandeClientRepository = commandeClientRepository;
  }

  @Override
  public ArticleDto save(ArticleDto dto) {
    List<String> errors = ArticleValidator.validate(dto);
    if (!errors.isEmpty()) {
      log.error("Article is not valid {}", dto);
      throw new InvalidEntityException("L'article n'est pas valide", ErrorCodes.ARTICLE_NOT_VALID, errors);
    }

    return ArticleDto.fromEntity(
        articleRepository.save(
            ArticleDto.toEntity(dto)
        )
    );
  }

  @Override
  public ArticleDto findById(Integer id) {
    if (id == null) {
      log.error("Article ID is null");
      return null;
    }

    return articleRepository.findById(id).map(ArticleDto::fromEntity).orElseThrow(() ->
        new EntityNotFoundException(
            "Aucun article avec l'ID = " + id + " n' ete trouve dans la BDD",
            ErrorCodes.ARTICLE_NOT_FOUND)
    );
  }

  @Override
  public ArticleDto findByCodeArticle(String codeArticle) {
    if (!StringUtils.hasLength(codeArticle)) {
      log.error("Article CODE is null");
      return null;
    }

    return articleRepository.findArticleByCodeArticle(codeArticle)
        .map(ArticleDto::fromEntity)
        .orElseThrow(() ->
            new EntityNotFoundException(
                "Aucun article avec le CODE = " + codeArticle + " n' ete trouve dans la BDD",
                ErrorCodes.ARTICLE_NOT_FOUND)
        );
  }

  @Override
  public List<ArticleDto> findAll() {
    return articleRepository.findAll().stream()
        .map(ArticleDto::fromEntity)
        .collect(Collectors.toList());
  }

  @Override
  public List<LigneVenteDto> findHistoriqueVentes(Integer idArticle) {
    return venteRepository.findAllByArticleId(idArticle).stream()
        .map(LigneVenteDto::fromEntity)
        .collect(Collectors.toList());
  }

  @Override
  public List<LigneCommandeClientDto> findHistoriaueCommandeClient(Integer idArticle) {
    return commandeClientRepository.findAllByArticleId(idArticle).stream()
        .map(LigneCommandeClientDto::fromEntity)
        .collect(Collectors.toList());
  }

  @Override
  public List<LigneCommandeFournisseurDto> findHistoriqueCommandeFournisseur(Integer idArticle) {
    return commandeFournisseurRepository.findAllByArticleId(idArticle).stream()
        .map(LigneCommandeFournisseurDto::fromEntity)
        .collect(Collectors.toList());
  }

  @Override
  public List<ArticleDto> findAllArticleByIdCategory(Integer idCategory) {
    return articleRepository.findAllByCategoryId(idCategory).stream()
        .map(ArticleDto::fromEntity)
        .collect(Collectors.toList());
  }

  @Override
  public void delete(Integer id) {
    if (id == null) {
      log.error("Article ID is null");
      return;
    }
    List<LigneCommandeClient> ligneCommandeClients = commandeClientRepository.findAllByArticleId(id);
    if (!ligneCommandeClients.isEmpty()) {
      throw new InvalidOperationException("Impossible de supprimer un article deja utilise dans des commandes client", ErrorCodes.ARTICLE_ALREADY_IN_USE);
    }
    List<LigneCommandeFournisseur> ligneCommandeFournisseurs = commandeFournisseurRepository.findAllByArticleId(id);
    if (!ligneCommandeFournisseurs.isEmpty()) {
      throw new InvalidOperationException("Impossible de supprimer un article deja utilise dans des commandes fournisseur",
          ErrorCodes.ARTICLE_ALREADY_IN_USE);
    }
    List<LigneVente> ligneVentes = venteRepository.findAllByArticleId(id);
    if (!ligneVentes.isEmpty()) {
      throw new InvalidOperationException("Impossible de supprimer un article deja utilise dans des ventes",
          ErrorCodes.ARTICLE_ALREADY_IN_USE);
    }
    articleRepository.deleteById(id);
  }
}
