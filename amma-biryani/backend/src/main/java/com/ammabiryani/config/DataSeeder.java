package com.ammabiryani.config;

import com.ammabiryani.model.MenuItem;
import com.ammabiryani.repository.MenuItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final MenuItemRepository menuItemRepository;

    public DataSeeder(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    public void run(String... args) {
        if (menuItemRepository.count() > 0) {
            return; // already seeded
        }

        menuItemRepository.save(new MenuItem("Chicken Biryani", "Signature Amma Biryani recipe with tender chicken and aromatic basmati rice", 180.0, "Biryani", "", true));
        menuItemRepository.save(new MenuItem("Mutton Biryani", "Slow-cooked mutton biryani with rich spices", 260.0, "Biryani", "", true));
        menuItemRepository.save(new MenuItem("Egg Biryani", "Boiled eggs simmered with fragrant biryani rice", 130.0, "Biryani", "", true));
        menuItemRepository.save(new MenuItem("Veg Biryani", "Mixed vegetable biryani made in-house masala", 120.0, "Biryani", "", true));
        menuItemRepository.save(new MenuItem("Chicken 65", "Spicy, crispy fried chicken starter", 150.0, "Starters", "", true));
        menuItemRepository.save(new MenuItem("Mutton Chukka", "Dry roasted mutton with curry leaves and spices", 220.0, "Starters", "", true));
        menuItemRepository.save(new MenuItem("Boiled Egg (2 pcs)", "Side of boiled eggs", 20.0, "Add-ons", "", true));
        menuItemRepository.save(new MenuItem("Raita", "Cooling yogurt side served with biryani", 30.0, "Add-ons", "", true));
        menuItemRepository.save(new MenuItem("Gulab Jamun (2 pcs)", "Classic Indian sweet, served warm", 40.0, "Desserts", "", true));
        menuItemRepository.save(new MenuItem("Buttermilk", "Chilled spiced buttermilk", 25.0, "Beverages", "", true));

        System.out.println("Amma Biryani menu seeded successfully.");
    }
}
