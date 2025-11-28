import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            String source;
           
            source = new 
                        String(Files.readAllBytes(Paths.get("C:\\Users\\elias\\Downloads\\a3 de teoria\\a3 de teoria\\programa.txt")),
                         StandardCharsets.UTF_8);

            // 1) Léxico
            Lexer lexer = new Lexer(source);
            List<Token> tokens = lexer.tokenize();

            // 2) Sintático
            Parser parser = new Parser(tokens);
            List<Stmt> program = parser.parse();

            // 3) Semântico
            SemanticAnalyzer semantic = new SemanticAnalyzer();
            semantic.analyze(program);

            // 4) Geração de código intermediário
            CodeGenerator generator = new CodeGenerator();
            List<Instruction> code = generator.generate(program);

            System.out.println("=== Código intermediário gerado ===");
            for (int i = 0; i < code.size(); i++) {
                System.out.println(i + ": " + code.get(i));
            }
            System.out.println("===================================");

            // 5) Execução
            Interpreter interpreter = new Interpreter(code);
            System.out.println("\n=== Execução ===");
            interpreter.run();

        } catch (Exception e) {
            System.err.println("ERRO: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
