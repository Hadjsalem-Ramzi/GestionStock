package com.ramzi.gestiondestock.controller;

import com.flickr4java.flickr.FlickrException;
import com.ramzi.gestiondestock.controller.api.PhotoApi;
import com.ramzi.gestiondestock.services.strategy.StrategyPhotoContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class PhotoController implements PhotoApi {

  private StrategyPhotoContext strategyPhotoContext;

  @Autowired
  public PhotoController(StrategyPhotoContext strategyPhotoContext) {
    this.strategyPhotoContext = strategyPhotoContext;
  }

  @Override
  public Object savePhoto(String context, Integer id, MultipartFile photo, String title) throws IOException, FlickrException {
    return strategyPhotoContext.savePhoto(context, id, photo.getInputStream(), title);
  }
}
