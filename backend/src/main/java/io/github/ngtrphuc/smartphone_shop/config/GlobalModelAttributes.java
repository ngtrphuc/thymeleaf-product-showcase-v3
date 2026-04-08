package io.github.ngtrphuc.smartphone_shop.config;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute
    public void addCommonAttributes(Model model) {
        model.addAttribute("shopname", "Smartphone Shop");
        model.addAttribute("address", "Asaka, Saitama, Japan");
    }
}