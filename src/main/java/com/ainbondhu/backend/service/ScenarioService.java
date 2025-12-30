package com.ainbondhu.backend.service;

import com.ainbondhu.backend.domain.entity.Category;
import com.ainbondhu.backend.domain.entity.Question;
import com.ainbondhu.backend.domain.entity.QuestionOption;
import com.ainbondhu.backend.dto.CategoryDto;
import com.ainbondhu.backend.dto.QuestionDto;
import com.ainbondhu.backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScenarioService implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(c -> CategoryDto.builder()
                        .id(c.getId().toString())
                        .nameBn(c.getNameBn())
                        .descriptionBn(c.getDescriptionBn())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<QuestionDto> getQuestionsByCategory(String categoryId) {
        Category category = categoryRepository.findById(java.util.UUID.fromString(categoryId))
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return category.getQuestions().stream()
                .sorted((q1, q2) -> Integer.compare(q1.getSequenceOrder(), q2.getSequenceOrder()))
                .map(q -> QuestionDto.builder()
                        .id(q.getId().toString())
                        .textBn(q.getTextBn())
                        .order(q.getSequenceOrder())
                        .options(q.getOptions().stream().map(QuestionOption::getTextBn).collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void run(String... args) throws Exception {
        if (categoryRepository.count() == 0) {
            seedData();
        }
    }

    private void seedData() {
        // Divorce Category
        Category divorce = new Category();
        divorce.setNameBn("বিবাহ বিচ্ছেদ (Divorce)");
        divorce.setDescriptionBn("পারিবারিক আইন সংক্রান্ত সহায়তা");

        List<Question> questions = new ArrayList<>();

        Question q1 = new Question();
        q1.setTextBn("আপনার বিবাহের সময়কাল কত?");
        q1.setSequenceOrder(1);
        q1.setCategory(divorce);
        q1.setOptions(createOptions(q1, "১ বছরের কম", "১-৫ বছর", "৫ বছরের বেশি"));
        questions.add(q1);

        Question q2 = new Question();
        q2.setTextBn("বিবাহ বিচ্ছেদের কারণ কি?");
        q2.setSequenceOrder(2);
        q2.setCategory(divorce);
        q2.setOptions(createOptions(q2, "পারিবারিক কলহ", "নির্যাতন", "অন্যান্য"));
        questions.add(q2);

        divorce.setQuestions(questions);
        categoryRepository.save(divorce);

        // GD Category
        Category gd = new Category();
        gd.setNameBn("জিডি (General Diary)");
        gd.setDescriptionBn("থানায় সাধারণ ডায়েরি সংক্রান্ত");

        List<Question> gdQuestions = new ArrayList<>();
        Question gdQ1 = new Question();
        gdQ1.setTextBn("কি ধরণের জিডি করতে চান?");
        gdQ1.setSequenceOrder(1);
        gdQ1.setCategory(gd);
        gdQ1.setOptions(createOptions(gdQ1, "হারানো বিজ্ঞপ্তি", "হুমকি", "অন্যান্য"));
        gdQuestions.add(gdQ1);

        gd.setQuestions(gdQuestions);
        categoryRepository.save(gd);
    }

    private List<QuestionOption> createOptions(Question q, String... texts) {
        List<QuestionOption> options = new ArrayList<>();
        for (String text : texts) {
            QuestionOption opt = new QuestionOption();
            opt.setTextBn(text);
            opt.setQuestion(q);
            options.add(opt);
        }
        return options;
    }
}
