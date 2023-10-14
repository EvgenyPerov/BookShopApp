package com.example.MyBookShopApp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value(value = "${uploadCovers.path}")
    private String uploadCoversPath;

    @Value(value = "${uploadPhoto.path}")
    private String uploadPhotoPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/book-covers/**","/author-photo/**")
                .addResourceLocations("file:" + uploadCoversPath + "/")
                .addResourceLocations("file:" + uploadPhotoPath + "/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/403").setViewName("403");
        registry.addViewController("/errorspage").setViewName("errorspage");
        registry.addViewController("/notFoundPage").setViewName("404");
    }
    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> containerCustomizer() {
        return container -> container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND,"/notFoundPage"));

    }
    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

}
