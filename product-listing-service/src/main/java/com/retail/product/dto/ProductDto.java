package com.retail.product.dto;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

@Data
public class ProductDto {
  @JsonProperty("productId")
  private Long id;
  private String name;
  private double price;
  private Long categoryId;
  private String categoryName;

  // When JSON contains a nested 'category' object, extract its productCategoryId and name
  @JsonProperty("category")
  private void unpackCategory(JsonNode category) {
    if (category != null) {
      if (category.has("productCategoryId") && !category.get("productCategoryId").isNull()) {
        this.categoryId = category.get("productCategoryId").asLong();
      }
      if (category.has("name") && !category.get("name").isNull()) {
        this.categoryName = category.get("name").asText();
      }
    }
  }
}
