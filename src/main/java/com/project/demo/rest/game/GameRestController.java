package com.project.demo.rest.game;

import com.project.demo.logic.entity.game.Game;
import com.project.demo.logic.entity.game.GameRepository;
import com.project.demo.logic.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
public class GameRestController {
    @Autowired
    private GameRepository gameRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'USER')")
    public List<Game> getAllGames(){
        return gameRepository.findAll();
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Game updateGame(@PathVariable Long id, @RequestBody Game game) {
        return gameRepository.findById(id)
                .map(existingGame -> {
                    existingGame.setName(game.getName());
                    existingGame.setDescription(game.getDescription());
                    existingGame.setImgURL(game.getImgURL());
                    existingGame.setStatus(game.getStatus());
                    return gameRepository.save(existingGame);
                })
                .orElseGet(() -> {
                    game.setId(id);
                    return gameRepository.save(game);
                });
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Game addGame(@RequestBody Game game) {
        return  gameRepository.save(game);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteGame (@PathVariable Long id) {
        gameRepository.deleteById(id);
    }

}
