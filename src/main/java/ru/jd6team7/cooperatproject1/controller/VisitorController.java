package ru.jd6team7.cooperatproject1.controller;

import liquibase.pro.packaged.V;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.jd6team7.cooperatproject1.model.visitor.Visitor;
import ru.jd6team7.cooperatproject1.service.VisitorService;

@RestController
@RequestMapping("/visitor")
public class VisitorController {

    private final VisitorService visitorService;

    public VisitorController(VisitorService visitorService) {
        this.visitorService = visitorService;
    }

    @PostMapping("/add")
    public ResponseEntity<Visitor> addVisitor(@RequestBody Visitor visitor) {
        Visitor addVisitor = visitorService.addVisitor(visitor);
        return ResponseEntity.ok(addVisitor);
    }

    @GetMapping("/find")
    public ResponseEntity<Visitor> findVisitor(@PathVariable long id) {
        return ResponseEntity.ok(visitorService.findVisitor(id));
    }

    @PutMapping("/update")
    public ResponseEntity<Visitor> updateVisitor(@RequestBody Visitor visitor) {
        Visitor updateVisitor = visitorService.updateVisitor(visitor);
        return ResponseEntity.ok(updateVisitor);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteVisitor(@RequestBody Visitor visitor) {
        visitorService.deleteVisitor(visitor);
        return ResponseEntity.ok().build();
    }
}
