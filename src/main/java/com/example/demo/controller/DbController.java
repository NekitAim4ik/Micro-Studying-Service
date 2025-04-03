package com.example.demo.controller;


import com.example.demo.entity.Comment;
import com.example.demo.repository.AuthorityRepository;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@CrossOrigin("*")
public class DbController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private AuthorityRepository authorityRepository;


    @PostMapping("/api/add")
    public @ResponseBody Iterable<Comment> addComment(@RequestBody Comment comment) {
        log.info("New entry: " + commentRepository.save(comment));
        return getAll();
    }

    @GetMapping("/api/greeting")
    public @ResponseBody String greeting() {
        return "Goida ZOV!";
    }

    @GetMapping("/api/getAll")
    public Iterable<Comment> getAll() {
        log.info("Returned all rows");
        return commentRepository.findAll();
    }

    @GetMapping("/api/get")
    public @ResponseBody Comment getComment(@RequestParam Integer id) {
        return commentRepository.findById(id).orElseThrow();
    }

    @DeleteMapping("/api/delete")
    public void deleteComment(@RequestParam Integer id) {
        commentRepository.deleteById(id);
    }
}
