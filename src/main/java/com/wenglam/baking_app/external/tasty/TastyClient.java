package com.wenglam.baking_app.external.tasty;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.wenglam.baking_app.config.TastyFeignConfig;

@FeignClient(
    name = "tastyClient",
    url = "https://tasty.p.rapidapi.com/recipes",
    configuration = TastyFeignConfig.class)
public interface TastyClient {

    @GetMapping("/list")
    public TastyListResponseDto getDessertRecipes(
        @RequestHeader("X=RapidAPI-Host") String rapidApiHost,
        @RequestHeader("X-RapidAPI-Key") String rapidApiKey,
        @RequestParam(value = "from", required = true, defaultValue = "0") int from, // The offset of items to be ignored in response for paging
        @RequestParam(value = "size", required = true, defaultValue = "20") int size,
        @RequestParam(value = "tags", required = false, defaultValue = "") String tags,
        @RequestParam(value = "q", required = false, defaultValue = "dessert") String q,
        @RequestParam(value = "sort", required = false, defaultValue = "") String sort //Leave empty to sort by popular as default OR one of the following : approved_at:desc|approved_at:asc - not very useful
    );
}