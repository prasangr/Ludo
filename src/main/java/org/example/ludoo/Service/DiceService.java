package org.example.ludoo.Service;


import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class DiceService {

    private final Random random = new Random();

    // Simulates rolling a 6-sided dice
    public int rollDice() {
        return random.nextInt(6) + 1;
    }
}