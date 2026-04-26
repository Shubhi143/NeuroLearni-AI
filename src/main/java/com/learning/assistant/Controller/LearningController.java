package com.learning.assistant.Controller;



import com.learning.assistant.Service.LearningService;
import com.learning.assistant.dto.EvaluationRequest;
import com.learning.assistant.dto.LearningRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/api")
@CrossOrigin(origins = "*")
@RestController
public class LearningController {

    @Autowired
    private LearningService learningService;

    @PostMapping("/learn")
    public String learn(@RequestBody LearningRequest request) {
        return learningService.getExplanation(request);
    }

    @PostMapping("/quiz")
    public String generateQuiz(@RequestBody LearningRequest request) {
        return learningService.generateQuiz(request);
    }

    @PostMapping("/evaluate")
    public String evaluate(@RequestBody EvaluationRequest request) {
        return learningService.evaluateAnswer(request);
    }
}