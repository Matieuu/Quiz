package quiz;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import quiz.abstracts.Question;
import quiz.states.Quiz;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class QuestionYaml {

    private final String filePath;
    private Map<String, Map<String, Object>> questions;

    public QuestionYaml() throws IOException {
        this.filePath = System.getProperty("user.dir") + "/questions.yml";
        questions = new HashMap<>();
        checkAndCreateFile();
        loadYaml();
    }

    private void checkAndCreateFile() throws IOException {
        Path path = Paths.get(filePath);
        if (Files.notExists(path)) {
            Files.createFile(path);
            saveYaml();
        }
    }

    private void loadYaml() throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            Yaml yaml = new Yaml(new CustomClassLoaderConstructor(Question.class.getClassLoader(), new LoaderOptions()));
            questions = yaml.load(fis);
            if (questions == null) questions = new HashMap<>();
            if (!questions.isEmpty())
                for (Map<String, Object> question : questions.values())
                    Quiz.getInstance().addQuestion(question);
        }
    }

    public void addOrUpdateQuestion(String questionName, Question question) throws IOException {
        Map<String, Object> questionMap = new HashMap<>() {{
            put("question", question.getQuestion());
            put("questionType", question.getQuestionType().toString());
            put("correctAnswer", question.getCorrectAnswer());
            if (question.getAnswers() != null) put("answers", question.getAnswers());
        }};
        questions.put(questionName, questionMap);
        saveYaml();
    }

    public void saveYaml() throws IOException {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
        try (FileWriter writer = new FileWriter(filePath)) {
            yaml.dump(questions, writer);
        }
    }
}
