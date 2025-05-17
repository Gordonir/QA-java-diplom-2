package api.models;

import java.util.List;

public class OrderRequest {
    private List<String> ingredients;

    public OrderRequest(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    // Геттеры
    public List<String> getIngredients() {
        return ingredients;
    }
}