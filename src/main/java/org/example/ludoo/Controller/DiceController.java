package org.example.ludoo.Controller;

import org.example.ludoo.Service.DiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/game")
public class DiceController {

    private final DiceService diceService;

    public DiceController(DiceService diceService) {
        this.diceService = diceService;
    }

    // Roll the dice
    @PostMapping("/{gameId}/roll-dice/{playerId}")
    public ResponseEntity<Integer> rollDice(@PathVariable String gameId, @PathVariable String playerId) {
        int diceValue = diceService.rollDice();
        return ResponseEntity.ok(diceValue);
    }
}