package io.github.ngtrphuc.smartphone_shop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**")
                .addResourceLocations(
                        "classpath:/static/customer/css/",
                        "classpath:/static/admin/css/"
                );
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/customer/images/");
        registry.addResourceHandler("/fonts/**")
                .addResourceLocations("classpath:/static/customer/fonts/");
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
        registry.addResourceHandler("/svg/**")
                .addResourceLocations("classpath:/static/svg/");
    }
}
