package com.realbeatz.frontendtest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(value = "*")
@RestController
@RequestMapping("/api/test/")
public class FrontendTestController {

    private static List<TestFriendTabDetails> friendList = List.of(
            new TestFriendTabDetails("abc", "John", "Smith"),
            new TestFriendTabDetails("def", "Mary", "Smith"),
            new TestFriendTabDetails("ghi", "Bob", "Jones"),
            new TestFriendTabDetails("jkl", "Michael", "Carson")
    );

    @GetMapping("/friends")
    public ResponseEntity<?> getFriendsList() {
        return ResponseEntity.ok(friendList);
    }
}
