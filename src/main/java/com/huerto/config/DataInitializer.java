package com.huerto.config;

import com.huerto.model.Product;
import com.huerto.model.Recipe;
import com.huerto.model.User;
import com.huerto.repository.ProductRepository;
import com.huerto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Inicializamos los usuarios
        initializeUsers();
        
        // Inicializamos los productos
        initializeProducts();
    }

    private void initializeUsers() {
        if (userRepository.count() == 0) {
            // Creamos el usuario administrador
            User admin = new User();
            admin.setName("Administrador");
            admin.setEmail("huertohogar.info@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEnabled(true);
            Set<User.Role> adminRoles = new HashSet<>();
            adminRoles.add(User.Role.ROLE_USER);
            adminRoles.add(User.Role.ROLE_ADMIN);
            admin.setRoles(adminRoles);
            userRepository.save(admin);

            // Creamos el segundo usuario administrador
            User admin2 = new User();
            admin2.setName("Hugo Durant");
            admin2.setEmail("ha.durant@duocuc.cl");
            admin2.setPassword(passwordEncoder.encode("admin123"));
            admin2.setEnabled(true);
            admin2.setRoles(adminRoles);
            userRepository.save(admin2);

            // Creamos el usuario de prueba
            User user = new User();
            user.setName("Usuario Test");
            user.setEmail("usuario@test.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setEnabled(true);
            Set<User.Role> userRoles = new HashSet<>();
            userRoles.add(User.Role.ROLE_USER);
            user.setRoles(userRoles);
            userRepository.save(user);

            System.out.println("Usuarios iniciales creados");
            System.out.println("Admin: huertohogar.info@gmail.com / admin123");
            System.out.println("Admin 2: ha.durant@duocuc.cl / admin123");
            System.out.println("Usuario: usuario@test.com / user123");
        }
    }

    private void initializeProducts() {
        if (productRepository.count() == 0) {
            List<Product> products = Arrays.asList(
                    createProduct(
                            "Manzanas Orgánicas",
                            "Manzanas frescas, crujientes y ricas en fibra. Ideales para colaciones y postres saludables.",
                            BigDecimal.valueOf(2500),
                            BigDecimal.valueOf(2990),
                            "Frutas",
                            "/static/img/manzanas-fuji.jpg",
                            20,
                            4.5,
                            27,
                            true,
                            false,
                            16,
                            "52 kcal / 100g",
                            Arrays.asList("Aportan fibra y vitamina C", "Favorecen la digestión", "Bajas en calorías"),
                            Arrays.asList("Snack saludable", "Ensaladas y tartas", "Jugos y compotas"),
                            new Recipe("Ensalada de manzana y nueces",
                                    Arrays.asList("2 manzanas", "Hojas verdes", "Nueces", "Aceite de oliva", "Jugo de limón", "Sal"),
                                    Arrays.asList("Corta las manzanas en cubos", "Mezcla con hojas y nueces", "Aliña y sirve"))
                    ),
                    createProduct(
                            "Espinaca Americana",
                            "Espinaca fresca y crujiente, rica en hierro y vitaminas.",
                            BigDecimal.valueOf(2990),
                            BigDecimal.valueOf(3490),
                            "verduras",
                            "/static/img/Espinaca-Americana.jpg",
                            25,
                            4.5,
                            28,
                            true,
                            true,
                            14,
                            null,
                            Arrays.asList("Alta en hierro", "Rica en vitaminas A, C y K", "Baja en calorías"),
                            Arrays.asList("Ensaladas", "Batidos verdes", "Cocida al vapor"),
                            null
                    ),
                    createProduct(
                            "Yogurt Natural",
                            "Yogurt natural cremoso, sin azúcares añadidos.",
                            BigDecimal.valueOf(1990),
                            BigDecimal.valueOf(2490),
                            "lacteos",
                            "/static/img/jogurt.png",
                            40,
                            4.7,
                            35,
                            false,
                            true,
                            20,
                            null,
                            Arrays.asList("Fuente de probióticos", "Alto en calcio", "Proteínas de calidad"),
                            Arrays.asList("Desayunos", "Postres", "Salsas"),
                            null
                    ),
                    createProduct(
                            "Leche Orgánica",
                            "Leche proveniente de vacas alimentadas de manera natural, sin hormonas ni antibióticos.",
                            BigDecimal.valueOf(1990),
                            BigDecimal.valueOf(2490),
                            "Lácteos",
                            "/static/img/leche.webp",
                            30,
                            4.8,
                            41,
                            false,
                            true,
                            20,
                            "61 kcal / 100ml",
                            Arrays.asList("Fuente de calcio y proteína", "Aporta vitaminas A y D"),
                            Arrays.asList("Batidos y cafés", "Postres y repostería", "Salsas y cremas"),
                            new Recipe("Batido de plátano con leche",
                                    Arrays.asList("1 plátano", "250 ml de leche orgánica", "1 cdta de miel", "Hielos al gusto"),
                                    Arrays.asList("Licúa todos los ingredientes", "Sirve frío"))
                    ),
                    createProduct(
                            "Pan Integral",
                            "Pan elaborado con harinas integrales, alto en fibra y de fermentación lenta.",
                            BigDecimal.valueOf(3490),
                            BigDecimal.valueOf(3990),
                            "Panadería",
                            "/static/img/pan-integral.avif",
                            15,
                            4.2,
                            18,
                            true,
                            false,
                            13,
                            "247 kcal / 100g",
                            Arrays.asList("Alto en fibra", "Mayor saciedad", "Aporta minerales y vitaminas del grupo B"),
                            Arrays.asList("Sándwiches saludables", "Tostadas con palta", "Acompañamiento de sopas"),
                            null
                    ),
                    createProduct(
                            "Naranjas",
                            "Naranjas jugosas y dulces, ricas en vitamina C.",
                            BigDecimal.valueOf(2490),
                            BigDecimal.valueOf(2990),
                            "frutas",
                            "/static/img/naranjas.jpg",
                            50,
                            4.7,
                            45,
                            false,
                            true,
                            17,
                            null,
                            Arrays.asList("Alto contenido en vitamina C", "Refuerza el sistema inmunológico", "Hidratante natural"),
                            Arrays.asList("Jugos naturales", "Postres", "Consumo directo"),
                            null
                    ),
                    createProduct(
                            "Kit de Inicio de Huerto",
                            "Todo lo necesario para comenzar tu huerto en casa: semillas, sustrato y herramientas básicas.",
                            BigDecimal.valueOf(29990),
                            BigDecimal.valueOf(34990),
                            "kits",
                            "/static/img/kit-inicio.webp",
                            15,
                            4.5,
                            24,
                            true,
                            true,
                            14,
                            null,
                            Arrays.asList("Aprendizaje práctico", "Cultivo en espacios reducidos", "Autoabastecimiento"),
                            Arrays.asList("Iniciar huerto urbano", "Actividades familiares"),
                            null
                    ),
                    createProduct(
                            "Macetero de Madera 30L",
                            "Macetero robusto de 30 litros, ideal para hortalizas y plantas medianas.",
                            BigDecimal.valueOf(12990),
                            BigDecimal.valueOf(15990),
                            "maceteros",
                            "/static/img/macetero-madera.jpg",
                            8,
                            4.2,
                            18,
                            false,
                            true,
                            19,
                            null,
                            Arrays.asList("Madera tratada y durable", "Buen drenaje", "Estética natural"),
                            Arrays.asList("Cultivo de tomates, lechugas y hierbas", "Decoración de terrazas"),
                            null
                    ),
                    createProduct(
                            "Tijeras de Podar Profesionales",
                            "Corte limpio y preciso para podas de mantenimiento y formación.",
                            BigDecimal.valueOf(8990),
                            BigDecimal.valueOf(9990),
                            "herramientas",
                            "/static/img/Tijera-de-poda.webp",
                            0,
                            4.8,
                            42,
                            true,
                            false,
                            10,
                            null,
                            Arrays.asList("Hojas de acero de alta calidad", "Ergonómicas y seguras", "Ajuste de tensión"),
                            Arrays.asList("Poda de ramas y flores", "Mantenimiento de frutales y arbustos"),
                            null
                    ),
                    createProduct(
                            "Tomate",
                            "Tomates frescos y jugosos, ideales para ensaladas y salsas.",
                            BigDecimal.valueOf(1990),
                            BigDecimal.valueOf(2490),
                            "verduras",
                            "/static/img/product-6.jpg",
                            45,
                            4.6,
                            41,
                            false,
                            true,
                            20,
                            null,
                            Arrays.asList("Rico en licopeno", "Bajo en calorías", "Hidratante natural"),
                            Arrays.asList("Ensaladas", "Salsas", "Guisos"),
                            null
                    )
            );

            productRepository.saveAll(products);
            System.out.println("Productos iniciales creados: " + products.size() + " productos");
        }
    }

    private Product createProduct(String name, String description, BigDecimal price, BigDecimal originalPrice,
                                   String category, String image, Integer stock, Double rating, Integer reviews,
                                   Boolean isNew, Boolean isSale, Integer discount, String energy,
                                   List<String> benefits, List<String> uses, Recipe recipe) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setOriginalPrice(originalPrice);
        product.setCategory(category);
        product.setImage(image);
        product.setStock(stock);
        product.setRating(rating);
        product.setReviews(reviews);
        product.setIsNew(isNew);
        product.setIsSale(isSale);
        product.setDiscount(discount);
        product.setEnergy(energy);
        product.setBenefits(benefits);
        product.setUses(uses);
        product.setRecipe(recipe);
        product.setPurchaseCount(0);
        return product;
    }
}

