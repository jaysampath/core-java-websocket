package com.tryout.websocket.test;

import com.google.gson.Gson;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ProductDataRetriever {

    private static final String API_URL = "https://fakestoreapi.com/products";

    //MySQL properties
    private static final String DB_URL = "jdbc:mysql://localhost:3306/products";
    private static final String DB_USERNAME = "admin";
    private static final String DB_PASSWORD = "admin";

    private Connection connection;


    public void fetchDataFromApiAndStoreInDb() throws IOException {
       List<Product> products = fetchDataFromApi();
       storeInDb(products);
    }

    private List<Product> fetchDataFromApi() throws IOException {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Error while fetching products data from API");
        }

        InputStream inputStream = connection.getInputStream();
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        String responseBodyString = scanner.hasNext() ? scanner.next() : "";
        scanner.close();
        connection.disconnect();

        Gson gson = new Gson();
        List<Product> products = gson.fromJson(responseBodyString, List.class);
        System.out.println("products fetched" + products.size());
        return products;

    }

    private void storeInDb(List<Product> products) {
        try {
            String insertQuery = "INSERT INTO PRODUCTS (id, title, price, category) VALUES (?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            for (Product product : products) {
                preparedStatement.setInt(1, product.getId());
                preparedStatement.setString(2, product.getTitle());
                preparedStatement.setFloat(3, product.getPrice());
                preparedStatement.setString(4, product.getCategory());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            preparedStatement.close();

            System.out.println("Stored products into DB. Number of products saved - " + products.size());
        } catch (SQLException e) {
            throw new RuntimeException("Something went wrong while saving into Mysql");
        }

    }

    public ProductDataRetriever() {
        try {
            //connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't get connection from MySql");
        }
    }

    public static void main(String[] args) throws IOException {
        ProductDataRetriever productDataRetriever = new ProductDataRetriever();
        productDataRetriever.fetchDataFromApiAndStoreInDb();
    }

    @Data
    @Getter
    @Setter
    class Product {
        private int id;
        private String title;
        private Long price;
        private String description;
        private String category;
        private String image;
        private Rating rating;
    }

    @Data
    @Getter
    @Setter
    class Rating {
        private Double rate;
        private Long count;
    }
}
